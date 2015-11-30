package cl.uchile.coupling.web.services;

import java.util.List;
import java.io.Serializable;

public interface AccessService  extends Serializable{
	public class Credentials implements Serializable{ 
		/**
		 * 
		 */
		private static final long serialVersionUID = -6481260089337496410L;
		private String clientId;
		private String sessionId;

		public Credentials(String clientId, String sessionId) {
			super();
			this.clientId = clientId;
			this.sessionId = sessionId;
		}

		public String getClientId() {
			return clientId;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}

	}

	public Credentials joinSession(String clientId,Boolean echo, Object... options);

	public Credentials joinSession(String clientId,Boolean echo,String sessionId, Object... options);

	public void leaveSession(String clientId,String sessionId);
	
	public void clearSession(String sessionId);
	
	public List<String> getSessionsList();
}
