package cl.uchile.cos.persistance;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.ResourceManagerException;
import org.apache.commons.transaction.util.Log4jLogger;
import org.apache.log4j.Logger;

import cl.uchile.cos.sync.Message;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;

public class BasicEventStateManager extends AbstractStateManager implements
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(BasicEventStateManager.class);

	private LinkedHashMap<String, ArrayList<Message>> events;
	private LinkedHashMap<String, Message> states;
	private String filenameStates, filenameEvents;
	private FileResourceManager frm;
	private Serializer serializer;
	private Integer messagesSinceBackup = 0;
	private Integer messagesForBackup = Integer.MAX_VALUE;

	public BasicEventStateManager(Serializer serializer,
			Integer messagesForBackup, String filesDir) {
		super();
		this.serializer = serializer;
		this.messagesForBackup = messagesForBackup;
		events = new LinkedHashMap<String, ArrayList<Message>>();
		states = new LinkedHashMap<String, Message>();
		this.frm = new FileResourceManager(filesDir + "/data", filesDir
				+ "/work", false, new Log4jLogger(logger));
		try {
			frm.start();
		} catch (Exception e) {
			logger.error("Error al iniciar FileResourceManager", e);
			frm = null;
		}

	}

	public void setSessionId(String sessionId) {
		super.setSessionId(sessionId);
		this.restore();
	}

	@Override
	public void processMessage(Message message) {
		String objectId = message.getObjectId();
		switch (message.getMessageType()) {
		case EVENT:
			if (!events.containsKey(objectId)) {
				events.put(objectId, new ArrayList<Message>());
			}
			events.get(objectId).add(message);
			break;
		case STATE:
			states.put(objectId, message);
			if (events.containsKey(objectId)) {
				events.get(objectId).clear();
			}
			break;

		}
		persist();

	}

	@Override
	public List<Message> getPersistedState(String objectId) {
		ArrayList<Message> results = new ArrayList<Message>();
		if (states.containsKey(objectId)) {
			results.add(states.get(objectId));
		}
		if (events.containsKey(objectId)) {
			for (Message message : events.get(objectId)) {
				results.add(message);
			}
		}
		return results;

	}

	public void persist() {
		if (frm == null)
			return;

		String txId = null;

		try {
			txId = frm.generatedUniqueTxId();
			frm.startTransaction(txId);
		} catch (Exception e) {
			logger.error("Error al iniciar transaccion", e);
		}

		try {
			writeToFile(txId, getEventsFilename(), events);
			writeToFile(txId, getStatesFilename(), states);
			if (frm.prepareTransaction(txId) == FileResourceManager.PREPARE_SUCCESS) {
				this.messagesSinceBackup++;
				frm.commitTransaction(txId);
				if (this.messagesSinceBackup >= this.messagesForBackup)
					backup();
			}

		} catch (Exception e) {
			logger.error("Error al escribir archivo", e);
			try {
				frm.rollbackTransaction(txId);
			} catch (ResourceManagerException e1) {
				logger.error("Error al hacer rollback", e1);
			}
		}

	}

	private void writeToFile(String txId, String filename, LinkedHashMap map)
			throws IOException, ResourceManagerException {
		frm.createResource(txId, filename, true);
		frm.createResource(txId, filename + ".old", true);
		frm.moveResource(txId, filename, filename + ".old", true);
		frm.lockResource(filename, txId);
		Output out = new Output(frm.writeResource(txId, filename, true));
		serializer.writeObject(map, out);
		out.close();
	}

	private String getEventsFilename() {
		if (this.filenameEvents == null)
			this.filenameEvents = this.getSessionId() + "-events.bin";
		return this.filenameEvents;
	}

	private String getStatesFilename() {
		if (this.filenameStates == null)
			this.filenameStates = this.getSessionId() + "-states.bin";
		return this.filenameStates;
	}

	static public void main(String[] args) throws ResourceManagerException,
			IOException {

	}

	public void restore() {

		if (frm == null)
			return;

		String txId = null;
		try {

			txId = frm.generatedUniqueTxId();
			frm.startTransaction(txId);
			try {
				loadEventsFromFile(txId);
				loadStatesFromFile(txId);
			} catch (Exception e) {
				logger.error(
						"No se pudo restaurar la session "
								+ this.getSessionId(), e);
			} finally {

				if (frm.prepareTransaction(txId) == FileResourceManager.PREPARE_SUCCESS)
					frm.commitTransaction(txId);
				else
					frm.rollbackTransaction(txId);
			}

		} catch (Exception e) {
			logger.error("Error al restaurar", e);
		}
		backup();
	}

	private void backup() {
		String txId = null;
		try {
			if (!frm.resourceExists(getEventsFilename())
					|| !frm.resourceExists(getStatesFilename()))
				return;
			txId = frm.generatedUniqueTxId();
			frm.startTransaction(txId);
			Long timestamp = System.currentTimeMillis();
			this.messagesSinceBackup = 0;
			frm.copyResource(txId, getEventsFilename(), "resp/" + timestamp
					+ "-" + getEventsFilename() + ".resp", true);
			frm.copyResource(txId, getEventsFilename() + ".old", "resp/"
					+ timestamp + "-" + getEventsFilename() + ".old.resp", true);
			frm.copyResource(txId, getStatesFilename(), "resp/" + timestamp
					+ "-" + getStatesFilename() + ".resp", true);
			frm.copyResource(txId, getStatesFilename() + ".old", "resp/"
					+ timestamp + "-" + getStatesFilename() + ".old.resp", true);

			if (frm.prepareTransaction(txId) == FileResourceManager.PREPARE_SUCCESS)
				frm.commitTransaction(txId);
			else
				frm.rollbackTransaction(txId);

		} catch (Exception e) {
			logger.error("Error al hacer backuo", e);
		}

	}

	private void loadEventsFromFile(String txId) throws Exception {
		if (!frm.resourceExists(getEventsFilename())) {
			return;
		}
		Input in = null;
		frm.createResource(txId, getEventsFilename(), true);
		frm.createResource(txId, getEventsFilename() + ".old", true);

		try {
			in = new Input(frm.readResource(txId, getEventsFilename()));
			this.events = serializer.readObject(in, LinkedHashMap.class);
		} catch (Exception e) {
			if (in != null)
				in.close();
			in = new Input(frm.readResource(txId, getEventsFilename() + ".old"));
			this.events = serializer.readObject(in, LinkedHashMap.class);
		}
		in.close();
	}

	private void loadStatesFromFile(String txId) throws Exception {
		if (!frm.resourceExists(getStatesFilename())) {
			return;
		}
		Input in = null;
		frm.createResource(txId, getStatesFilename(), true);
		frm.createResource(txId, getStatesFilename() + ".old", true);
		try {
			in = new Input(frm.readResource(txId, getStatesFilename()));
			this.states = serializer.readObject(in, LinkedHashMap.class);
		} catch (Exception e) {
			if (in != null)
				in.close();
			in = new Input(frm.readResource(txId, getStatesFilename() + ".old"));
			this.states = serializer.readObject(in, LinkedHashMap.class);
		}
		in.close();

	}

	@Override
	public void destroy() {
		this.backup();
		if (frm == null)
			return;

		String txId = null;
		try {

			txId = frm.generatedUniqueTxId();
			frm.startTransaction(txId);
			deleteFile(txId,getStatesFilename());
			deleteFile(txId,getStatesFilename()+".old");
			deleteFile(txId,getEventsFilename());
			deleteFile(txId,getEventsFilename()+".old");
			
		} catch (Exception e) {
			logger.error(
					"No se pudo destruir la session " + this.getSessionId(), e);
		} finally {
			try {
				if (frm.prepareTransaction(txId) == FileResourceManager.PREPARE_SUCCESS)
					frm.commitTransaction(txId);
				else
					frm.rollbackTransaction(txId);
			} catch (ResourceManagerException e) {
				Log.error("Error en escritura de archivos", e);
			}
		}

	}
	private void deleteFile(String txId,String file) throws ResourceManagerException{
		if (frm.resourceExists(txId,file)) {
			frm.deleteResource(txId, file);
		}
	}

}
