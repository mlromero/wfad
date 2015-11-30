package cl.uchile.cos.persistance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.esotericsoftware.kryo.Kryo;

public class KryoConfigurator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -171650963891615346L;

	public void configure(Kryo kryo){
		kryo.register(LinkedHashMap.class,0);
		kryo.register(ArrayList.class,1);
		kryo.register(LinkedHashMap.class,2);
	}

}
