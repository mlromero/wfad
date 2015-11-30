package cl.uchile.cos.persistance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cl.uchile.cos.sync.Message;

public class PersistentObjectsStateManager extends AbstractStateManager {
	protected Map<String, PersistentCoupledObject> coupledObjects;

	public PersistentObjectsStateManager() {
		super();
		this.coupledObjects = new LinkedHashMap<String, PersistentCoupledObject>();
	}

	@Override
	public void processMessage(Message message) {
		String modelId = message.getObjectId();
		if (this.coupledObjects.containsKey(modelId)) {
			PersistentCoupledObject coupledObject = this.coupledObjects
					.get(modelId);
			switch (message.getMessageType()) {
			case EVENT:
				this.executeFuntion(coupledObject, message.getMessageName(),
						message.getArguments().toArray());
				break;
			case STATE:
				coupledObject.updateState(message.getObjectState());
				break;
			}

		}
	}

	public void registerPersistentCoupledObject(
			PersistentCoupledObject coupledObject) {
		this.coupledObjects.put(coupledObject.getModelId(), coupledObject);
	}

	private void executeFuntion(PersistentCoupledObject coupledObject,
			String methodName, Object[] args) {
		Method[] methods = coupledObject.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; ++i) {
			Method method = methods[i];
			if (method.getName().equals(methodName)) {
				try {
					Object result = method.invoke(coupledObject, args);
					if (result != null
							&& PersistentCoupledObject.class.isInstance(result)) {
						PersistentCoupledObject newCoupledObject = (PersistentCoupledObject) result;
						registerPersistentCoupledObject(newCoupledObject);
					}
				} catch (IllegalAccessException e) {
					continue;
				} catch (IllegalArgumentException e) {
					continue;
				} catch (InvocationTargetException e) {
					// TODO: LOG 
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public List<Message> getPersistedState(String objectId) {
		if (this.coupledObjects.containsKey(objectId)) {
			return this.coupledObjects.get(objectId).getStateMessages();
		}
		return new ArrayList<Message>();

	}



	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
