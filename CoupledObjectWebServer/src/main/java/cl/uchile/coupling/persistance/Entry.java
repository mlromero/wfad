package cl.uchile.coupling.persistance;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Entry {
	static final public int STATE = 1;
	static final public int EVENT = 2;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	private String session;
	private Integer type;
	private String message;
	private String objectId;
	private Long time;

	public Entry(){
	}
	public Entry(String session, Integer type, String message, String objectId) {
		super();
		this.session = session;
		this.type = type;
		this.message = message;
		this.objectId = objectId;
		this.time=new Date().getTime();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}

}
