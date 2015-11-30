package cl.uchile.cos.session;

import java.io.Serializable;

import cl.uchile.cos.sync.adapters.Adapter;

public interface Client extends Serializable{

	public void setId(Long id);
 
	public Long getId();

	public void addAdapter(String SessionId, Adapter adapter);

	public Adapter getAdapter(String sessionId);
	public boolean equals(Client c);
	public void destroy(); 

}
