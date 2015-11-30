package cl.uchile.cos.persistance;

import java.util.LinkedHashSet;
import java.util.List;

import cl.uchile.cos.sync.CouplingManagerImpl;
import cl.uchile.cos.sync.Message;
import cl.uchile.cos.sync.adapters.Adapter;
 
public abstract class AbstractStateManager implements StateManager{

	private CouplingManagerImpl manager;
	private LinkedHashSet<MessageValidator> validators;
	private String sessionId;

	public AbstractStateManager() {
		this.validators = new LinkedHashSet<MessageValidator>();
	}
	@Override
	public String getSessionId(){
		return this.sessionId;
	}
	@Override
	public void setSessionId(String sessionId){
		this.sessionId=sessionId;
	}

	@Override
	public boolean validateMessage(Message message, Adapter sender) { 
		for (MessageValidator validator : this.validators) {
			if (!validator.validateMessage(message, sender)) {
				return false;
			}
		}
		return true;
	}

	@Override
	abstract public void processMessage(Message message);

	@Override
	abstract public List<Message> getPersistedState(String objectId);

	

	public CouplingManagerImpl getManager() {
		return manager;
	}

	public void setManager(CouplingManagerImpl manager) {
		this.manager = manager;
	}

	@Override
	public void addValidator(MessageValidator validator) {
		this.validators.add(validator);
	}

	@Override
	public void removeValidator(MessageValidator validator) {
		this.validators.remove(validator);
	}

}
