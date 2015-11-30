package cl.uchile.coupling.resources;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class JSONMessageSerializer extends Serializer<JSONMessage>{

	@Override
	public JSONMessage read(Kryo arg0, Input arg1, Class<JSONMessage> arg2) {
		String json=arg1.readString();
		JSONMessage result=new JSONMessage();
		result.setJson(json);
		return result;
	}

	@Override
	public void write(Kryo arg0, Output arg1, JSONMessage arg2) {
		arg1.writeString(arg2.toString());
		
	}

}
