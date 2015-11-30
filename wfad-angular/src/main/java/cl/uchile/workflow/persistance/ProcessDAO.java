package cl.uchile.workflow.persistance;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cl.uchile.workflow.persistance.model.Estado;
import cl.uchile.workflow.persistance.model.Process;

@Repository
public class ProcessDAO {
	@PersistenceContext
	private EntityManager em;

	@Transactional
	public String getProcessOwner(String processName){
		TypedQuery<String> q = em.createQuery("SELECT owner FROM Process WHERE processId = : processName", String.class);
		q.setParameter("processName", processName);		
		return q.getSingleResult();		
	}
	
	@Transactional
	public Process getProcessByName(String name){
		try{
		TypedQuery<Process> q = em.createQuery("FROM Process WHERE processId=:name", Process.class);
		q.setParameter("name", name);
		Process p = q.getSingleResult();
		return p;
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
			
	}
	
	@Transactional
	public void update(Process p){
		this.em.merge(p);
	}
	
	public Estado getEstadoById(Integer id){
		return this.em.find(Estado.class, id);
	}
	
	
}
