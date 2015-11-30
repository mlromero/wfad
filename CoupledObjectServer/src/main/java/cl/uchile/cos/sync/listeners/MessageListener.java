package cl.uchile.cos.sync.listeners;

import cl.uchile.cos.sync.Message;
import cl.uchile.cos.sync.adapters.Adapter;

public interface MessageListener extends Listener {
	public void onRecive(Message message,Adapter adapter);
	public void onSendToAdapter(Message message,Adapter adapter);
	public void onPropagationEnd(Message message);
}
