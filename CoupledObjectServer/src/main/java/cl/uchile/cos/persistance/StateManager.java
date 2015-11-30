package cl.uchile.cos.persistance;

import java.io.Serializable;
import java.util.List;

import cl.uchile.cos.sync.Message;
import cl.uchile.cos.sync.adapters.Adapter;

public interface StateManager extends Serializable{

	public boolean validateMessage(Message message, Adapter sender);

	public void processMessage(Message message);

	public List<Message> getPersistedState(String objectId);

	public void addValidator(MessageValidator validator);

	public void removeValidator(MessageValidator validator);

	public String getSessionId();

	public void setSessionId(String sessionId);

	public void destroy();

}
