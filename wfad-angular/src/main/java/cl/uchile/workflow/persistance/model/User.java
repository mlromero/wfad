package cl.uchile.workflow.persistance.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
@Table(name="user", schema="public")
public class User implements Serializable {
	
	private static final long serialVersionUID = -2170165293671855171L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	@Column(name="username", unique=true)
	private String user;
	private String password;
	@ManyToMany(cascade=CascadeType.ALL, fetch= FetchType.LAZY)
	@JoinColumn(name="name", nullable=false)
	private List<Process> processes=new ArrayList<Process>();
	@Column(name="last_process")
	private String lastProcess;

	public User(String user, String password) {
		super();
		this.user = user;
		this.password = password;
	}

	public User() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public List<Process> getProcesses() {
		return processes;
	}

	public void setProcesses(List<Process> processes) {
		this.processes = processes;
	}

	public String getLastProcess() {
		return lastProcess;
	}

	public void setLastProcess(String lastProcess) {
		this.lastProcess = lastProcess;
	}
}
