package cl.uchile.workflow.external;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLAccess {
	static private class Credential{
		private String id;
		private String nombre;
		
		
		public Credential(String id, String nombre) {
			super();
			this.id = id;
			this.nombre = nombre;
		}
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getNombre() {
			return nombre;
		}
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		
	} 
	
	static public void login(String user,String pass) throws ClassNotFoundException, SQLException{
		  Class.forName("com.mysql.jdbc.Driver");
	      // Setup the connection with the DB
	      Connection connect = DriverManager
	          .getConnection("jdbc:mysql://200.63.97.119/sistemas_aula?"
	              + "user=sistemas_aula&password=aula514");

	      // Statements allow to issue SQL queries to the database
	      Statement statement = connect.createStatement();
	      ResultSet r=statement.executeQuery("SELECT * from usuario");
	      while(r.next()){
	    	  System.out.println(r.getString("id_usuario"));
	    	  System.out.println(r.getString("clave_usuario"));
	    	  
	      }
	      r.close();
	      statement.close();
	      connect.close();
	}
	static public void main(String[] args) throws ClassNotFoundException, SQLException{
		login("","");
	}

}
