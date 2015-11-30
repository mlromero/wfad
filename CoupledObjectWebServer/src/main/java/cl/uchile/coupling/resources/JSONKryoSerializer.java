package cl.uchile.coupling.resources;

import com.esotericsoftware.kryo.Kryo;

import cl.uchile.cos.persistance.KryoSerializer;

public class JSONKryoSerializer extends KryoSerializer{
	@Override
	protected void configure(){
		super.configure();
		Kryo kryo=super.getKryo();
		kryo.register(JSONMessage.class, new JSONMessageSerializer());
	}

}
