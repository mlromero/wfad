package cl.uchile.cos.sync.listeners;

import cl.uchile.cos.sync.adapters.Adapter;

public interface CouplingListener extends Listener {
	public void onCouple(String objectId,Adapter adapter);
	public void onDecouple(String objectId,Adapter adapter);

}
