package cl.uchile.coupling.resources;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uchile.cos.sync.Message;

public class JSONMessage extends Message {
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
			Message message = MessageFactory.parseJSON(this.json);
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
				MessageFactory.toJSON(this, writer);
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
