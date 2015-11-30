package cl.uchile.cos.persistance;

import java.util.List;
import java.util.Map;

import cl.uchile.cos.sync.Message;

public interface PersistentCoupledObject {
	public List<Message> getStateMessages();
	public String getModelId();
	public void updateState(Map<String, Object> state);
}
 