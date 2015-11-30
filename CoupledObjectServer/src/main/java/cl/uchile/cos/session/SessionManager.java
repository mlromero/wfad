package cl.uchile.cos.session;

import java.io.Serializable;
import java.util.Collection;

public interface SessionManager extends Serializable{
	public Client createClient();
	public Session createSession(Object...options);
	public Session getSessionById(String sessionId);
	public Client getClientById(Long clientId);
	public void destroyClient(Client client);
	public void destroySession(Session session);
	public void joinClientToSession(Client client,Session session,Boolean echo);
	public void removeClientFromSession(Client client, Session session);
	public Collection<Session> getActiveSessions();
	
}
