package cl.uchile.coupling.resources;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import cl.uchile.cos.sync.Message;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageFactory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4823224497133354544L;

	private class JSONMessage extends Message {
		/**
		 * 
		 */
		private static final long serialVersionUID = -948346662808374866L;
		transient private boolean modified = false;
		transient private String json;
		transient private boolean loaded = false;
		public JSONMessage(String objectId){
			super();
			super.setObjectId(objectId);
		}
		public JSONMessage() {
		}
		private void load() {
			Message message = parseJSON(this.json);
			super.setArguments(new ArrayList<Object>(message.getArguments()));
			super.setMessageId(message.getMessageId());
			super.setMessageName(message.getMessageName());
			super.setObjectId(message.getObjectId());
			super.setMessageType(message.getMessageType());
			this.loaded = true;
		}

		@Override
		public String getObjectId() {
			if (!loaded && super.getObjectId()==null) {
				load();
			}
			return super.getObjectId();
		}

		@Override
		public void setObjectId(String objectId) {
			if (this.getObjectId() != objectId) {
				modified = true;
			}
			super.setObjectId(objectId);
		}

		@Override
		public int getMessageId() {
			if (!loaded) {
				load();
			}
			return super.getMessageId();
		}

		@Override
		public void setMessageId(int eventId) {
			if (this.getMessageId() != eventId) {
				modified = true;
			}
			super.setMessageId(eventId);
		}

		@Override
		public String getMessageName() {
			if (!loaded) {
				load();
			}
			return super.getMessageName();
		}

		@Override
		public void setMessageName(String messageName) {
			if (this.getMessageName() != messageName) {
				modified = true;
			}
			super.setMessageName(messageName);
		}

		@Override
		public List<Object> getArguments() {
			if (!loaded) {
				load();
			}
			return super.getArguments();
		}

		@Override
		public void setArguments(List<Object> arguments) {
			if (!this.getArguments().equals(arguments)) {
				modified = true;
			}
			super.setArguments(arguments);
		}

		@Override
		public MessageType getMessageType() {
			if (!loaded) {
				load();
			}
			return super.getMessageType();
		}

		@Override
		public void setMessageType(MessageType messageType) {
			if (!this.getMessageType().equals(messageType)) {
				modified = true;
			}
			super.setMessageType(messageType);
		}

		public String getJson() {
			return json;
		}

		public void setJson(String json) {
			this.json = json;
		}

		public boolean isModified() {
			return modified;
		}
		public String toString(){
			if(this.isModified()){
				StringWriter writer=new StringWriter();
				toJSON(this, writer);
				writer.flush();
				String res=writer.toString();
				try {
					writer.close();
				} catch (IOException e) {
					// TODO LOG
					e.printStackTrace();
				}
				return res;
			}
			return this.json;
		}

	}

	transient private ObjectMapper mapper = new ObjectMapper();


	public Message createFromJSON(String objectId,CharSequence json) {
		if(json==null || json.length()==0)
			return null;
		JSONMessage event = new JSONMessage(objectId);
		event.setJson(json.toString());
		return event;
	}
	public Message createFromJSON(CharSequence json) {
		JSONMessage event = new JSONMessage();
		event.setJson(json.toString());
		return event;
	}

	private Message parseJSON(CharSequence json) {
		try {
			return getMapper().readValue(json.toString(), Message.class);
		} catch (IOException e) {
			// TODO logging
			e.printStackTrace();
			return null;
		}
	}

	public void toJSON(Message event, Writer writer) {
		if (JSONMessage.class.isInstance(event)
				&& !((JSONMessage) event).isModified()) {
			try {
				writer.write(((JSONMessage) event).getJson());
			} catch (IOException e) {
				// TODO logging
				e.printStackTrace();
			}
		} else {
			this.eventToJSON(event, writer);
		}
	}

	private void eventToJSON(Message event, Writer writer) {
		try {
			getMapper().writeValue(writer, event);
		} catch (IOException e) {
			// TODO logging
			e.printStackTrace();

		}
	}
	private final ObjectMapper getMapper(){
		if(mapper==null)
			mapper=new ObjectMapper();
		return mapper;
	}

}
