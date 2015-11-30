package cl.uchile.workflow.persistance.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="process", schema="public")
public class Process implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7788619177798333712L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	private String processId;
	private String name;
	private String owner;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Estado estado;
	
	private Date fecha;
	
	private Date fechaFin;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinColumn(name="username", nullable=false)
	private List<User> users=new ArrayList<User>();

	public Process(String name, String owner) {
		super();
		this.name=name;
		this.owner = owner;
		this.processId = name.trim().replaceAll(" ", "");
		this.fecha = new Date();
		 
	}

	public Process() {
		super();
	}

	@Column(name="processid")
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToOne(fetch=FetchType.LAZY, mappedBy = "process")
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fecha", nullable= false)
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fechafin")
	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	
	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
