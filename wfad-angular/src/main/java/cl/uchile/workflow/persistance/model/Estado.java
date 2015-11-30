package cl.uchile.workflow.persistance.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name="estado", schema="public")
public class Estado implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7788619177798333712L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Integer id;	
	@Column(name = "nombre")
	private String name;
	


	public Estado() {
		super();
	}
	
	public Estado(Integer id){
		super();
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	

}
