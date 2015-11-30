package cl.uchile.cos.sync;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import cl.uchile.cos.persistance.AbstractStateManager;
import cl.uchile.cos.persistance.StateManager;
import cl.uchile.cos.sync.adapters.Adapter;
import cl.uchile.cos.sync.listeners.CouplingListener;
import cl.uchile.cos.sync.listeners.Listener;
import cl.uchile.cos.sync.listeners.MessageListener;

public class CouplingManagerImpl implements CouplingManager { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6184655961711874295L;

	class MessagePropagator implements Runnable {
		private Message message;
		private Adapter adapter;
		private boolean echo;

		public MessagePropagator(Message message, Adapter adapter, boolean echo) {
			this.message = message;
			this.adapter = adapter;
			this.echo = echo;
		}

		@Override
		public void run() {
			try {
				for (MessageListener listener : messageListeners) {
					listener.onRecive(message, adapter);
				}
				propagate(message, adapter, echo);
			} catch (Exception e) {
					//TODO Logging
				//Muy importante manejar cualquier error que pueda suceder aca pues si el error se lanza el temporizador se detiene
				e.printStackTrace();
			}
		}

	}

	private Map<String, Set<Adapter>> adapters;
	private Set<MessageListener> messageListeners;
	private Set<CouplingListener> couplingListener;
	private StateManager stateManager;
	private Long messagesToGo = 0L;
	private transient Executor executor;
	private String sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
		this.stateManager.setSessionId(sessionId);
	}

	public CouplingManagerImpl(Executor executor) {
		this.adapters = new LinkedHashMap<String, Set<Adapter>>();
		this.messageListeners = new LinkedHashSet<MessageListener>();
		this.couplingListener = new LinkedHashSet<CouplingListener>();
		this.executor=executor;
	
	}

	@Override
	public synchronized void addListener(Listener listener) { 
		if (MessageListener.class.isInstance(listener))
			this.messageListeners.add((MessageListener) listener);
		else if (CouplingListener.class.isInstance(listener)) {
			this.couplingListener.add((CouplingListener) listener);
		}
	}

	@Override
	public synchronized void removeListener(Listener listener) {
		if (MessageListener.class.isInstance(listener))
			this.messageListeners.remove((MessageListener) listener);
		else if (CouplingListener.class.isInstance(listener)) {
			this.couplingListener.remove((CouplingListener) listener);
		}
	}

	private synchronized void propagate(Message message, Adapter adapter,
			boolean echo) {
		String id = message.getObjectId();
		messagesToGo--;
		if (this.stateManager != null)
			this.stateManager.processMessage(message);
		for (Adapter destAdapter : adapters.get(id)) {
			if (destAdapter.equals(adapter) && !echo)
				continue;
			destAdapter.sendToClient(message);
			for (MessageListener listener : this.messageListeners) {
				listener.onSendToAdapter(message, adapter);
			}
		}
		for (MessageListener listener : this.messageListeners) {
			listener.onPropagationEnd(message);
		}
	}

	/**
	 * Regista el mensaje para ser propadgado. Si el mensaje no es seguro por
	 * temas de sincronizacion retornara false y lo reenviara sincronizado al
	 * cliente en el futuro. De lo contrario no se lo enviara denuevo al cliente
	 * que lo envio.
	 * 
	 * @param message
	 * @param adapter
	 * @return true si el cliente puede ejecutar el evento, false si no.
	 */
	@Override
	public boolean registerMessage(Message message, Adapter adapter) {
		return this.registerMessage(message, adapter, false);

	}
	@Override
	public boolean registerMessage(Message message, Adapter adapter,
			boolean echo) {
		if (this.stateManager != null
				&& !this.stateManager.validateMessage(message, adapter)) {
			return false;
		}
		boolean safe = false;
		synchronized (this) {
			safe = isMessageSyncSafe(message, adapter);
			this.messagesToGo++;
		}

		this.executor.execute(new MessagePropagator(message, adapter, !safe
				|| echo));
		return safe;

	}

	/**
	 * Revisa si el mensaje puede o no causar problemas de sincronizacion con el
	 * cliente. Esto significa revisar si hay algun mensaje emitido antes que
	 * aun no ha sido entregado a al cliente que envio este mensaje.
	 * 
	 * @param message
	 * @return
	 */
	private boolean isMessageSyncSafe(Message message, Adapter adapter) {
		if (adapter.ready()) {
			if (this.messagesToGo == 0)
				return true;
		}
		return false;
	}

	@Override
	public synchronized void couple(String objectId, Adapter adapter) {
		this.couple(objectId, adapter, null);
	}

	@Override
	public synchronized void decouple(String objectId, Adapter adapter) {
		if (this.adapters.containsKey(objectId)) {
			this.adapters.get(objectId).remove(adapter);
		}
		for (CouplingListener listener : this.couplingListener) {
			listener.onDecouple(objectId, adapter);
		}
	}

	@Override
	public synchronized void decouple(Adapter adapter) {
		for (String objectId : this.adapters.keySet()) {
			this.decouple(objectId, adapter);
		}
	}


	@Override
	public void setStateManager(StateManager stateManager) {
		this.stateManager = stateManager;
	}

	@Override
	public synchronized void couple(String objectId, Adapter adapter,
			List<Message> messages) {
		List<Message> stateMessages = new ArrayList<Message>(0);
		if (this.stateManager != null) {
			stateMessages = this.stateManager.getPersistedState(objectId);
			for (Message message : stateMessages) {
				adapter.sendToClient(message);
			}
		}
		if (!this.adapters.containsKey(objectId)) {
			this.adapters.put(objectId, new LinkedHashSet<Adapter>());
		}
		this.adapters.get(objectId).add(adapter);
		for (CouplingListener listener : this.couplingListener) {
			listener.onCouple(objectId, adapter);
		}
		if (stateMessages.size() == 0 && messages != null) {
			for (Message message : messages) {
				this.registerMessage(message, adapter, true);
			}
		}

	}

	@Override
	public void destroy() {
		if(this.stateManager!=null)
			this.stateManager.destroy();
		
	}
}
