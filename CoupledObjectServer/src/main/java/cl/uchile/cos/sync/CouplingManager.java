package cl.uchile.cos.sync;

import java.io.Serializable;
import java.util.List;

import cl.uchile.cos.persistance.AbstractStateManager;
import cl.uchile.cos.persistance.StateManager;
import cl.uchile.cos.sync.adapters.Adapter;
import cl.uchile.cos.sync.listeners.Listener;

public interface CouplingManager extends Serializable {

	public void addListener(Listener listener);

	public void removeListener(Listener listener);

	public boolean registerMessage(Message message, Adapter adapter);

	public boolean registerMessage(Message message, Adapter adapter,
			boolean echo);

	public void couple(String objectId, Adapter adapter);

	public void couple(String objectId, Adapter adapter, List<Message> messages);

	public void decouple(Adapter adapter);

	public void decouple(String objectId, Adapter adapter);

	public void setStateManager(StateManager stateManager);
	
	public String getSessionId();

	public void setSessionId(String sessionId);
	public void destroy();

}
