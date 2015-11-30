package cl.uchile.coupling.messaging;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import cl.uchile.cos.session.Client;
import cl.uchile.cos.session.SessionManager;
import cl.uchile.cos.sync.Message;
import cl.uchile.cos.sync.adapters.ServerAdapter;
import cl.uchile.coupling.resources.MessageFactory;

public class JSONBufferAdapter extends ServerAdapter {
	/**
	 * 
	 */ 
	private static final long serialVersionUID = 6746589582608046977L;
	private StringBuffer buffer;
	private Client client;
	private String id;
	private static final int INIT_BUFFER_SIZE = 1024;
	private MessageFactory messageFactory;

	public JSONBufferAdapter(Client client, String sessionId,
			SessionManager manager, MessageFactory messageFactory, Boolean echo) {
		super(sessionId, manager,echo);
		this.client = client;
		this.buffer = new StringBuffer(INIT_BUFFER_SIZE);
		this.setMessageFactory(messageFactory);
		this.id = client.getId() + "-" + sessionId;
	}

	public JSONBufferAdapter() {
		super();
		this.buffer = new StringBuffer(INIT_BUFFER_SIZE);
	}

	@Override
	public synchronized void sendToClient(Message event) {

		Writer out = new StringWriter();
		try {
			if (buffer.length() == 0) {
				out.append("{\"messages\":[");
				messageFactory.toJSON(event, out);
			} else {
				out.append(",");
				messageFactory.toJSON(event, out);
			}
			out.flush();
			buffer.append(out.toString());
		} catch (IOException e) {
			// TODO: logging
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO: logging
				e.printStackTrace();
			}
		}

	}

	public synchronized void getMessages(Writer writer) {

		if (this.buffer.length() > 0) {
			this.buffer.append("]}");
			try {
				writer.write(this.buffer.toString());
			} catch (IOException e) {
				// TODO LOG
				e.printStackTrace();
			}
			this.buffer = new StringBuffer(INIT_BUFFER_SIZE);
		}
	}

	public synchronized String getMessages() {

		if (this.buffer.length() > 0) {
			this.buffer.append("]}");

			String result = buffer.toString();

			this.buffer = new StringBuffer(INIT_BUFFER_SIZE);
			return result;
		}
		return "";
	}

	public Client getClient() {

		return client;
	}

	@Override
	public void setSessionId(String sessionId) {
		super.setSessionId(sessionId);
		if (client != null)
			this.id = client.getId() + "-" + getSessionId();
	}

	public void setClient(Client client) {
		this.id = client.getId() + "-" + getSessionId();
		this.client = client;
	}

	public boolean equals(JSONBufferAdapter adapter) {
		return adapter.getClient().equals(this.client)
				&& adapter.getSessionId() == this.getSessionId();
	}

	public void sendToServer(CharSequence json) {
		this.sendToServer(messageFactory.createFromJSON(json));
	}

	@Override
	public boolean ready() {
		return this.buffer.length() == 0;
	}

	public MessageFactory getMessageFactory() {
		return messageFactory;
	}

	public void setMessageFactory(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	public String toString() {
		return this.client.toString() + ", Id Sesion:" + this.getSessionId();
	}

	@Override
	public int hashCode() {
		if(this.id==null)
			return super.hashCode();
		return this.id.hashCode();
	}
}
