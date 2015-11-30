package cl.uchile.cos.persistance;

import cl.uchile.cos.sync.Message;
import cl.uchile.cos.sync.adapters.Adapter;

public interface MessageValidator {
	public boolean validateMessage(Message message, Adapter sender); 
}
 