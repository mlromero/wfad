package cl.uchile.cos.util;

import java.io.Serializable;

public interface IdGenerator<T> extends Serializable{
	public T nextId();   

}
