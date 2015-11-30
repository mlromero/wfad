package cl.uchile.coupling.web.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.uchile.cos.session.Client;
import cl.uchile.cos.session.Session;
import cl.uchile.cos.session.SessionManager;

@Service("accessService")
public class AccessServiceImpl implements AccessService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4108200024182094702L;
	@Autowired
	private SessionManager sessionManager;

	@Override
	public Credentials joinSession(String clientId,Boolean echo, Object... options) {
		return this.joinSession(clientId,echo,null, options);
	}

	@Override
	public Credentials joinSession(String clientId,Boolean echo, String sessionId,
			Object... options) {
		Session session = null;
		if (sessionId == null || sessionId.trim().length() == 0)
			session = sessionManager.createSession(options);
		else
			session = sessionManager.getSessionById(sessionId);
		Client client=null;
		if(clientId==null){
			client=sessionManager.createClient();
		}else{
			client=sessionManager.getClientById(Long.parseLong(clientId));
			if(client==null)
				client=sessionManager.createClient();
		}
		sessionManager.joinClientToSession(client, session,echo);
		return new Credentials(client.getId().toString(),
				session.getSessionId());
	}

	@Override
	public void leaveSession(String clientId, String sessionId) {
		sessionManager.removeClientFromSession(sessionManager.getClientById(Long.parseLong(clientId)),
				sessionManager.getSessionById(sessionId));
	}

	@Override
	public void clearSession(String sessionId) {
		sessionManager.destroySession(sessionManager.getSessionById(sessionId));
	}

	@Override
	public List<String> getSessionsList() {
		Collection<Session> sessions = sessionManager.getActiveSessions();
		List<String> ssids = new ArrayList<String>(sessions.size());
		for (Session session : sessions) {
			ssids.add(session.getSessionId());
		}
		Collections.sort(ssids);
		return ssids;
	}

}
