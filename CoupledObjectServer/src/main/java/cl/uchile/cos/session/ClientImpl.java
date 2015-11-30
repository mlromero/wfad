package cl.uchile.cos.session;

import java.util.LinkedHashMap;
import java.util.Map;

import cl.uchile.cos.sync.adapters.Adapter;
import cl.uchile.cos.util.IdGenerator;

public class ClientImpl  implements Client{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6090680716260870439L;
	private Long id;
	private Map<String,Adapter> adapters;
	public ClientImpl(){
		
	}
	
	public ClientImpl(IdGenerator<Long> generator) {
		this.id = generator.nextId();
		this.adapters = new LinkedHashMap<String, Adapter>();
	}
	@Override
	public Adapter getAdapter(String sessionId) { 
		return adapters.get(sessionId);
	} 
	@Override
	public void addAdapter(String SessionId,Adapter adapter) {
		this.adapters.put(SessionId, adapter);
	}
	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	public boolean equals(Client c){
		return this.id==c.getId();
	}
	public void destroy(){
		for(Adapter adapter:adapters.values()){
			adapter.decoupleAll();
		}
	}
	

}
