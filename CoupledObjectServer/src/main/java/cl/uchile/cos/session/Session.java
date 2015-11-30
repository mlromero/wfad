package cl.uchile.cos.session;

import java.io.Serializable;

import cl.uchile.cos.sync.CouplingManager;

public interface Session extends Serializable {

	public CouplingManager getCouplingManager();

	public void destroy();

	public void setSessionId(String sessionId);

	public String getSessionId();

	public void close();

}
