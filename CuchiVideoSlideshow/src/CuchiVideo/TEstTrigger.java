package CuchiVideo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TEstTrigger {
	static Connection c = null;
	static Statement stmt = null;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String strFile="D:/test.db";

		 Class.forName("org.sqlite.JDBC");
		 c = DriverManager.getConnection("jdbc:sqlite:"+strFile);
	     System.out.print("Opened database successfully in "+ strFile +".");

	     stmt = c.createStatement();
	    stmt.execute("UPDATE VIDEOS SET SELECCIONADA=0 WHERE FILE=(SELECT FILE FROM VIDEOS_ORDERED LIMIT 1, 1)") ;
	    
		//cerramos la BD
		 stmt.close();
	      c.close();
		
	}

}
