package cl.uchile.cos.persistance;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoSerializer implements Serializer {
	private transient Kryo kryo = null;

	protected void configure() {
		kryo = new Kryo();
		 kryo.register(LinkedHashMap.class, 100);
		 kryo.register(ArrayList.class, 101);
		 kryo.register(LinkedHashMap.class, 102);
	}

	@Override
	public void writeObject(Object object, Output out) {
		if (kryo == null)
			configure();
		kryo.writeObject(out, object);
	}

	@Override
	public <T> T readObject(Input in, Class<T> type) {
		if (kryo == null)
			configure();
		return kryo.readObject(in, type);
	}

	protected Kryo getKryo() {
		return kryo;
	}

	protected void setKryo(Kryo kryo) {
		this.kryo = kryo;
	}

}
