package cl.uchile.coupling.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import cl.uchile.cos.session.Client;
import cl.uchile.cos.session.ClientImpl;
import cl.uchile.cos.session.Session;
import cl.uchile.cos.session.SessionImpl;
import cl.uchile.cos.session.SessionManager;
import cl.uchile.cos.sync.CouplingManager;
import cl.uchile.cos.sync.adapters.ServerAdapter;
import cl.uchile.cos.util.IdGenerator;
import cl.uchile.coupling.messaging.JSONBufferAdapter;
import cl.uchile.coupling.resources.MessageFactory;

@Component
public class GenericSessionManager implements SessionManager {
	/**
	 * 
	 */ 
	private static final long serialVersionUID = 5621719662941058L; 
	private Map<String, Session> sessions;
	private Map<String, List<Long>> sessionClients;
	private Map<Long, List<String>> clientSessions;
	private Map<Long, Client> clients;
	@Autowired
	private IdGenerator<?> sessionIdGenerator;
	@Autowired
	private IdGenerator<Long> clientIdGenerator;
	private Boolean destroySessionIfEmpty = false;

	@Autowired
	private MessageFactory messageFactory;
	@Autowired
	transient private ApplicationContext applicationContext;

	private GenericSessionManager(Boolean destroySessionIfEmpty) {
		sessions = new HashMap<String, Session>();
		clients = new HashMap<Long, Client>();
		this.destroySessionIfEmpty = destroySessionIfEmpty;
		sessionClients = new HashMap<String, List<Long>>();
		clientSessions = new HashMap<Long, List<String>>();
	}

	@Override
	public synchronized Client createClient() {
		Client c = new ClientImpl(clientIdGenerator);
		this.clients.put(c.getId(), c);
		this.clientSessions.put(c.getId(), new ArrayList<String>());
		return c;
	}

	@Override
	public synchronized Session createSession(Object... options) {
		String id = "";
		if (options.length > 0) {
			id = options[0].toString();
		} else {
			while (true) {
				id = sessionIdGenerator.nextId().toString();
				if (!this.sessions.containsKey(id))
					break;
			}
		}
		CouplingManager cm = this.createCouplingManager();
		cm.setSessionId(id);
		Session s = new SessionImpl(id, cm);

		this.sessions.put(s.getSessionId(), s);
		this.sessionClients.put(s.getSessionId(), new ArrayList<Long>());
		return s;
	}

	private CouplingManager createCouplingManager() {
		CouplingManager c = applicationContext.getBean(CouplingManager.class);
		return c;

	}

	private ServerAdapter createAdapter(Client client, Session session,
			Boolean echo) {
		// ServerAdapter adapter =
		// this.applicationContext.getBean(ServerAdapter.class);
		ServerAdapter adapter = new JSONBufferAdapter(client,
				session.getSessionId(), this, messageFactory, echo);
		adapter.setSessionId(session.getSessionId());
		return adapter;

	}

	@Override
	public Session getSessionById(String sessionId) {
		if (this.sessions.containsKey(sessionId)) {
			return this.sessions.get(sessionId);
		}
		return this.createSession(sessionId);
	}

	@Override
	public Client getClientById(Long clientId) {
		return this.clients.get(clientId);
	}

	@Override
	public synchronized void destroyClient(Client client) {
		if (client == null)
			return;
		for (String sessionId : this.clientSessions.get(client.getId())) {
			Session session = this.sessions.get(sessionId);
			this.removeClientFromSession(client, session);
		}
		this.clients.remove(client.getId());
		this.clientSessions.remove(client.getId());
		client.destroy();

	}

	@Override
	public synchronized void destroySession(Session session) {
		if (session == null)
			return;
		this.sessions.remove(session.getSessionId());
		List<Long> src = this.sessionClients.get(session.getSessionId());
		ArrayList<Long> dest = new ArrayList<Long>(src.size());
		dest.addAll(src);
		for (Long clientId : dest) {
			Client client = this.clients.get(clientId);
			this.removeClientFromSession(client, session);
		}

		if (!destroySessionIfEmpty) {
			session.destroy();
			this.sessionClients.remove(session.getSessionId());
		}

	}

	@Override
	public synchronized void joinClientToSession(Client client,
			Session session, Boolean echo) {
		if (session == null || client == null)
			return;
		if (sessionClients.get(session.getSessionId()).contains(client))
			return;
		this.clientSessions.get(client.getId()).add(session.getSessionId());
		this.sessionClients.get(session.getSessionId()).add(client.getId());
		client.addAdapter(session.getSessionId(),
				this.createAdapter(client, session, echo));

	}

	@Override
	public synchronized void removeClientFromSession(Client client,
			Session session) {
		if (session == null || client == null)
			return;
		this.clientSessions.get(client.getId()).remove(session.getSessionId());
		this.sessionClients.get(session.getSessionId()).remove(client.getId());
		if (this.sessionClients.get(session.getSessionId()).size() == 0) {
			this.sessionClients.remove(session.getSessionId());
			if (this.destroySessionIfEmpty) {
				session.destroy();
			} else {
				session.close();
			}
		}

	}

	@Override
	public Collection<Session> getActiveSessions() {
		return this.sessions.values();
	}

}
