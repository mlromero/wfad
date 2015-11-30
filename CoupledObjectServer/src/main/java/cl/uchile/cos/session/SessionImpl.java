package cl.uchile.cos.session;

import cl.uchile.cos.sync.CouplingManager;
import cl.uchile.cos.util.IdGenerator;

public class SessionImpl implements Session{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2456548149626967567L;
	private String sessionId; 
	private CouplingManager couplingManager;
	private boolean closed=false;
	public SessionImpl(IdGenerator<?> generator,CouplingManager couplingManager) {
		this.sessionId=generator.nextId().toString();
		this.couplingManager = couplingManager;
	}
	public SessionImpl(String sessionId, CouplingManager couplingManager) {
		this.sessionId = sessionId;
		this.couplingManager = couplingManager;
	}
	@Override
	public String getSessionId() {
		return sessionId;
	}
	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	@Override
	public void destroy(){
		this.close();
		couplingManager.destroy();
	}
	@Override
	public CouplingManager getCouplingManager(){
		if(this.closed){
			throw new SessionClosedException();
		}
		return this.couplingManager;
	}
	public boolean equals(Object session){
			return this.sessionId.equals(((SessionImpl)session).getSessionId());
	}
	@Override
	public void close() {
		this.closed=true;
	}
	

	

}
