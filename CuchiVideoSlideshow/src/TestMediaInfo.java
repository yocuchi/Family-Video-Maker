import java.io.File;
import net.sourceforge.filebot.mediainfo.*;

public class TestMediaInfo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
	  	System.out.println(System.getProperty("java.version"));
	  	System.out.println(System.getProperty("os.arch"));
		
		  File f = new File("C:\\Program Files\\MediaInfo\\MediaInfo.dll");
		   if(f.exists() && !f.isDirectory()) { 
			   System.out.println("Si existe la libreria  ");
		   }
		   else{System.out.println("No existe la libreria");}
		  
		   
		   String fileName   = "D:\\Fotos de Familia\\1900\\a.jpg";
		   File file         = new File(fileName);
		   	System.out.println("Creando media info");
		   MediaInfo info    = new MediaInfo();
		   info.open(file);
		   
		   info.close();
		   
		 	System.out.println("Cerrado media info");
		   //MediaInfo ff = new MediaInfo();
		      
		   	
		   	System.out.println("Librería Cargada");

	}

}
