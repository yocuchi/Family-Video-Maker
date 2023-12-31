package CuchiVideo;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lnkparser.LnkParser;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import CuchiVideo.Interfaces.FileMaker;
import CuchiVideo.Interfaces.MovieMaker2014;
import CuchiVideo.Tools.VideoTools;
import CuchiVideo.logica.SelectMediaAlgorithm;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import com.drew.metadata.exif.ExifSubIFDDirectory;


public class Init {

	/**
	 * @param args
	 * 
	 * 1 Carpeta y subcarpetas para buscar
	 * 2 Duracion del video (segundos)
	 * 3 Duracion de los trozos de fotos en segundos puede ser 2.5
	 * 4 Duracion de los trozos de video en segundos puede ser 4.5
	 * 5 Fichero de Destino (extension wlmp)
	 * 6 Carpeta con los audios
	 * @throws MagicException 
	 * @throws MagicMatchNotFoundException 
	 * @throws MagicParseException 
	 * 
	 * Un ejemplo de params
	 * 
	 * 
	 * "D:\Video Reyes 2021\input" 2500 2.5 4.5 "e:\Video Reyes 2021\seleccion" "C:\Users\Default\Music" 
	 *
	 */
	
	static Connection c = null;
	static Statement stmt = null;
	static PreparedStatement pstmt = null;
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static int NumFotos;
	public static int NumVideos;
	public static double DuracionTotalVideos;
	 
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		if (args.length != 6){ 
		System.out.println("La cagaste. Mal argumentos." + args.length);
		System.out.println(args[1]);
		System.exit(1);}
		
		
		String strFile="test.db";
		//CUCHI este booleano te puede alegrar la vida, porque no escanea todas las
		//fotos, solamente una vez y luego con el sqlite es feliz
		
		boolean escanea=true;
		File myFile = new File(strFile);
		
		if(myFile.exists() && escanea ){
		    myFile.delete();
		}
		
		System.out.println(".: CUCHI PROGRAMA PARA HACER VIDEOS FELICES :.");
		System.out.println("Parametros del video a crear:");
		System.out.println("Carpeta Origen:\t"+ args[0]);
		System.out.println("Carpeta Audio:\t"+ args[5]);
		System.out.println("Salida:\t"+ args[4]);
		System.out.println("Duracion:\t"+ args[1]+"seg");
		System.out.println("Duracion Foto:\t"+ args[2]+"seg");
		System.out.println("Duracion Video:\t"+ args[3]+"seg");
		
		SetUp(strFile);
		
		if (escanea){
		//creamos la base de datos sqlite
		CreaBD(strFile);
		
		
		//leemos todos los ficheros y los metemos en BD
		ProcesaFicheros(args[0]);
		}
		
		
		//se han quedado fuera
		InformeFicherosBd();
		
		//limpio el campo seleccion
		
		stmt.executeUpdate( "UPDATE IMAGES SET SELECCIONADA=0");
		stmt.executeUpdate( "UPDATE VIDEOS SET SELECCIONADA=0");
		
		//A insertar ficheros en el sistema
		//SelectMediaAlgorithm.RandomSelect(c,new MovieMaker2014(args[4]) , args);
		SelectMediaAlgorithm.RandomSelect(c,new FileMaker(args[4]) , args);
		
		
		System.out.println("FINAL FELIZ!!");
		//cerramos la BD
		 stmt.close();
	      c.close();
	      
	}

	
	
	
	private static void InformeFicherosBd() throws SQLException {

		
		System.out.println("=========\n FIN DE ESCANEO");
		
		 ResultSet rs = stmt.executeQuery( "SELECT count(*),avg(SIZE) FROM IMAGES;" );
		 rs.next();
		System.out.println("Procesadas correctamente "+ rs.getString(1) + "imagenes, con un tama�o medio de " +
		 rs.getString(2));
		NumFotos=Integer.parseInt(rs.getString(1));
		
		//SIZE,DATE,LENGHT,QUALITY
		 rs = stmt.executeQuery( "SELECT count(*),avg(SIZE),avg(LENGHT),avg(QUALITY) FROM VIDEOS;" );
		 rs.next();
		
		 System.out.println("Procesados correctamente "+ rs.getString(1) + " videos, con un tama�o medio de " +
		 rs.getString(2)+", con una duracion media de "+ rs.getString(3)+", y una calidad media de "+ rs.getString(4));

		

		
		NumVideos=Integer.parseInt(rs.getString(1));
		DuracionTotalVideos=rs.getDouble(1)*rs.getDouble(3);
				
		 rs = stmt.executeQuery( "SELECT count(*) FROM OUTS;" );
		 rs.next();
		
		 System.out.println("Se han quedado fuera "+ rs.getString(1) + " ficheros");

		
	}




	private static void ProcesaFicheros(String Directorio) throws Exception {
		
		listf(Directorio);
	}

	public static void listf(String directoryName) throws Exception {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    int salto=0;
	    for (File file : fList) {
	    	//let's check file links
	    	String filename= file.getName();
	    	if (filename.substring(filename.lastIndexOf(".") + 1, filename.length()).equalsIgnoreCase("lnk")){
	    		//lnk
	    		LnkParser l = new LnkParser(file);
	    		file= new File(l.getRealFilename());
	    	}

	    	
	        if (file.isFile() && file.length() >= 200 * 1024) {
	        	//a a�adir a la base de datos
	        	try{
	            InsertFile(file);
	        	}
	        	catch(Exception e){
	        		e.printStackTrace();
	        	}
	            salto++;
	            if (salto%30==0)System.out.println();
	        } else if (file.isDirectory()) {
	            listf(file.getAbsolutePath());
	        }
	    }
	}


	private static void InsertFile(File file) throws Exception  {
		// TODO Auto-generated method stub

		//System.out.print("Procesando fichero:"+file);
		//check what it is
		String mimeType = "";
		
			mimeType = getMime(file);
	
		//System.out.println(" cuyo Mimetype es:"+ mimeType);
		
		if (mimeType.equalsIgnoreCase("image")){
			
			//SIZE
			long Size= file.length();
			//DATE
			//este se complica, puesto que la fecha buena es la de exif
			String Date=ImageFechaFoto(file);
			
			System.out.println("DATE="+Date);
			
			if (Date.equalsIgnoreCase("")) {
				throw new Exception("FECHA VACIA!!");}
			//QUALITY
			
			
            String sql = "INSERT INTO IMAGES (FILE,SIZE,DATE,QUALITY) " +
		            "VALUES ('"+file.getAbsolutePath()+"',"+ Size +",'"+Date+"',0) "; 
            //System.out.println(sql);
            //stmt.executeUpdate(sql);
            pstmt = c.prepareStatement("INSERT INTO IMAGES (FILE,SIZE,DATE,QUALITY)"
            		+ " values (?,?,?, 0)");
		      pstmt.setString(1, file.getAbsolutePath());
		      pstmt.setLong(2, Size);
		      pstmt.setString(3, Date);
		      pstmt.executeUpdate();	
			
		}
		
		//==============VIDEOS =====================
		if (mimeType.equalsIgnoreCase("video")){
			System.out.print("V");
			//SIZE
			long Size=file.length();
			
			//DATE
			
			//este se complica, puesto que la fecha buena es la de exif
			//String Date=ImageFechaFoto(file);
			String Date=dateFormat.format(file.lastModified());
			
			
			//usar media info mejor!!
			//http://www.filebot.net/
			//length y quality de la funcion
			long [] temp;
			
			temp=VideoTools.GetVideoLenghtAndRate(file);
			
			
		
			
           /* String sql = "INSERT INTO VIDEOS (FILE,SIZE,DATE,LENGHT,QUALITY,WIDTH,HEIGHT) " +
		            "VALUES ('"+file.getAbsolutePath()+"',"+ Size +",'"+Date+"',"+temp[0]+","+
            				temp[1]+","+temp[2]+","+temp[3]+") "; 
            //System.out.println(sql);
			stmt.executeUpdate(sql);
			*/
			pstmt = c.prepareStatement("INSERT INTO VIDEOS  (FILE,SIZE,DATE,LENGHT,QUALITY,WIDTH,HEIGHT)"
            		+ " values (?,?,?, ?,?,?,?)");
		      pstmt.setString(1, file.getAbsolutePath());
		      pstmt.setLong(2, Size);
		      pstmt.setString(3, Date);
		      pstmt.setLong(4, temp[0]);
		      pstmt.setLong(5, temp[1]);
		      pstmt.setLong(6, temp[2]);
		      pstmt.setLong(7, temp[3]);
		      
		      
		      pstmt.executeUpdate();	
		}
		
	}


	private static String ImageFechaFoto(File file) throws ImageProcessingException, IOException{
		//ya se que es foto
		System.out.println("File:"+ file.getAbsolutePath());
		Metadata metadata = ImageMetadataReader.readMetadata( file );
		

        // Read Exif Data
        Directory directory = metadata.getDirectory( ExifSubIFDDirectory.class );
        if( directory != null )
        {
            // Read the date
            Date date = directory.getDate( ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL );
            

            //ojo que aqui date puede ser null
            
            if (date !=null){
            
            	System.out.print("i");
            return dateFormat.format( date );
            }
            
        }
            
        	//System.out.println( "EXIF is null" );
            //VAMOS A POR FECHA DE MODIFICACION
        	System.out.print("X");
        	
        	//aqui esta el problema, el formato debe ser 2014-04-19 22:48:01
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            
          
        	return sdf.format(file.lastModified());
        
	}


	private static String getMime(File file) throws SQLException {
		// TODO Auto-generated method stub
		
		String ext = FilenameUtils.getExtension(file.getAbsolutePath());
		if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg") 
				 //|| ext.equalsIgnoreCase("png") 
				){
			return "image";
		}
		else if(ext.equalsIgnoreCase("mov") || ext.equalsIgnoreCase("avi")
				|| ext.equalsIgnoreCase("mts")|| ext.equalsIgnoreCase("wmv")
				|| ext.equalsIgnoreCase("mp4")|| ext.equalsIgnoreCase("mpg") ) {
			return "video";
			
		}
		else{
			String sql = "INSERT INTO OUTS (FILE,SIZE) " +
		            "VALUES ('"+file.getAbsolutePath()+"',0) ";
			
			
			pstmt = c.prepareStatement("INSERT INTO OUTS (FILE,SIZE) values (?, 0)");
		      pstmt.setString(1, file.getAbsolutePath());
		      pstmt.executeUpdate();	
		 //stmt.executeUpdate(sql);
		 System.out.println("OUT" + file.getAbsolutePath());
		return "";
		}
	}

public static void SetUp(String strFile) throws Exception{
	
	
	
	
	 Class.forName("org.sqlite.JDBC");
     c = DriverManager.getConnection("jdbc:sqlite:"+strFile);
     System.out.print("Opened database successfully in "+ strFile +".");

     stmt = c.createStatement();
}


	public static void CreaBD(String strFile){
		
		
		
		    try {
		     
		      String sql = "CREATE TABLE IMAGES " +
		                   "(FILE           TEXT    NOT NULL, " + 
		                   " SIZE            INT     NOT NULL, " + 
		                   " DATE        DATE         ," +
		                   " QUALITY     VARCHAR(5)   ," +
		                   " SELECCIONADA     INT   " +
		                   ")"; 
		      stmt.executeUpdate(sql);
		      
		     sql = "CREATE VIEW IMAGES_ORDERED AS " +
	                   " SELECT FILE,SIZE,DATE,QUALITY,SELECCIONADA" +
	                   " FROM IMAGES ORDER BY DATE ASC";
	      stmt.executeUpdate(sql);
	      
	      //TRIGGER PARA QUE FUNCIONE EL UPDATE SOBRE LA VISTA NO FUNCIONA DESDE JAVA
	     /* 
	      sql = "CREATE TRIGGER update_images_seleccionada INSTEAD OF UPDATE OF"+
	    		  " SELECCIONADA ON IMAGES_ORDERED " +
	    		  "BEGIN" +
	    		  " UPDATE IMAGES SET SELECCIONADA=new.SELECCIONADA WHERE" +
	    		  " FILE=old.FILE;" +
	    		  "END ";
	    stmt.executeUpdate(sql);
	      */
	      
		      
		      sql = "CREATE TABLE VIDEOS " +
	                   "(FILE           TEXT    NOT NULL, " + 
	                   " SIZE            INT     NOT NULL, " + 
	                   " DATE        DATE         ," +
	                   " LENGHT        INT         ," +
	                   " QUALITY     VARCHAR(5)   ," +
	                   " WIDTH     INT   ," +
	                   " HEIGHT     INT   ," +
	                   " SELECCIONADA     INT   " +
	                   ")"; 
		      stmt.executeUpdate(sql);
		      sql = "CREATE VIEW VIDEOS_ORDERED AS" +
	                   " SELECT FILE,SIZE,DATE,LENGHT,QUALITY,WIDTH,HEIGHT,SELECCIONADA" +
	                   " FROM VIDEOS ORDER BY DATE ASC";
	      stmt.executeUpdate(sql);
	      
	      //TRIGGER PARA QUE FUNCIONE EL UPDATE SOBRE LA VISTA
	     /* 
	      sql = "CREATE TRIGGER update_VIDEO_seleccionada INSTEAD OF UPDATE OF"+
	    		  " SELECCIONADA ON VIDEOS_ORDERED " +
	    		  "BEGIN" +
	    		  " UPDATE VIDEOS SET SELECCIONADA=new.SELECCIONADA WHERE" +
	    		  " FILE=old.FILE;" +
	    		  "END ";
	    stmt.executeUpdate(sql);
	       */
		      
	       
		 
		      sql = "CREATE TABLE OUTS " +
	                   "(FILE           TEXT    NOT NULL, " + 
	                   " SIZE            INT     NOT NULL " + 
	                   ")"; 
		      stmt.executeUpdate(sql);
		     
		    } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		    }
		    System.out.println("Table created successfully");
		  }
		
		
	}

