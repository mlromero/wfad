package cl.uchile.cos.sync.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cl.uchile.cos.session.Session;
import cl.uchile.cos.session.SessionManager;
import cl.uchile.cos.sync.Message;

public abstract class ServerAdapter implements Adapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3387011301324889477L;
	private SessionManager sessionManager;
	private String sessionId;
	private Boolean echo=false;
	public ServerAdapter(){}
	public ServerAdapter(String sessionId, SessionManager sesionManager,Boolean echo) {
		this.sessionId = sessionId;
		this.sessionManager = sesionManager;
		this.echo=echo;
	}

	public String getSessionId() {
		return sessionId;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public boolean sendToServer(Message message) {
		if(sessionManager==null)
			return false;
		Session session = sessionManager.getSessionById(sessionId);
		if (session != null)
			return session.getCouplingManager().registerMessage(message, this,echo);
		return true;
	}

	public void couple(String objectId) {
		if(sessionManager==null)
			return;
		Session session = sessionManager.getSessionById(sessionId);
		if (session != null)
			session.getCouplingManager().couple(objectId, this);
	}

	public void decouple(String objectId) {
		if(sessionManager==null)
			return;
		Session session = sessionManager.getSessionById(sessionId);
		if (session != null)
			session.getCouplingManager().decouple(objectId, this);
	}

	public void decoupleAll() {
		if(sessionManager==null)
			return;
		Session session = sessionManager.getSessionById(sessionId);
		if (session != null) {
			session.getCouplingManager().decouple(this);
		}

	}

	public void couple(String objectId, Message... state) {
		if(sessionManager==null)
			return;
		Session session = sessionManager.getSessionById(sessionId);
		if (session != null) {
			List<Message> messages = new ArrayList<Message>(state.length);
			Collections.addAll(messages, state);
			session.getCouplingManager().couple(objectId, this, messages);

		}
	}
	

}
