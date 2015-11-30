package cl.uchile.coupling.access;

import java.io.Serializable;
import java.util.List;

public interface UserData extends Serializable{
	
		
		public List<String> getClients() ;
		
		public void setClients(List<String> clients) ;
		public String getSessionClient() ;
		public void setSessionClient(String sessionClient) ;
		public String getUserId();


	

}
