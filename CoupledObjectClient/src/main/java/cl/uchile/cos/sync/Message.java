package cl.uchile.cos.sync;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4690502808583763524L;
	private String objectId;
	private int messageId;
	private String messageName;
	private List<Object> arguments;
	private MessageType messageType;
	private Map<String, Object> objectState;
	public enum MessageType{
		EVENT,STATE;
	}

	public Message() {
		arguments = new ArrayList<Object>();
	}
	

	public Message(String objectId, String messageName, MessageType messageType) {
		super();
		this.objectId = objectId;
		this.messageName = messageName;
		this.messageType = messageType;
		this.arguments = new ArrayList<Object>();
	}


	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public List<Object> getArguments() {
		return arguments;
	}

	public void setArguments(List<Object> arguments) {
		this.arguments = arguments;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}


	public Map<String, Object> getObjectState() {
		return objectState;
	}


	public void setObjectState(Map<String, Object> objectState) {
		this.objectState = objectState;
	}

}
