package cl.uchile.cos.util;

public class BasicIdGenerator implements IdGenerator<Long> {

	
	private static final long serialVersionUID = -5754515100392616484L;

	private Long nextId=1L;
	@Override 
	public Long nextId() { 
		return this.nextId++;
	}

}
