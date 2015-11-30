package cl.uchile.cos.persistance;

import java.io.Serializable;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public interface Serializer extends Serializable{
	public void writeObject(Object object, Output out);
	public <T> T readObject(Input in, Class<T> out);
}
