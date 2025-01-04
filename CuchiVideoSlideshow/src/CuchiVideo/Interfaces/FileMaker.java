package CuchiVideo.Interfaces;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import CuchiVideo.VideoOutputInterface;

public class FileMaker implements VideoOutputInterface {
	/*
	 * Clase que genera videos y los saca en una carpeta, poniendo por orden de nombre las imagenes y losvideos.
	 * 
	 * 
	 * 
	 */
	
	
	File DestFolder;
	String counter; // Cambiado a String
	int counter_video;
	int counter_photo;
	
	public FileMaker(String DestFolder) throws Exception{
		this.DestFolder=new File(DestFolder);
		this.counter="000001"; // Inicializado como el primer número con ceros a la izquierda
		this.counter_photo=0;
		this.counter_video=0;
		//vaciar el DestFolder
		
		/*if (isDirEmpty(Paths.get(getClass().getResource(DestFolder).toURI()))){
			System.out.println("Directorio no vacio, implemente la opcion de borrado");
		}*/
		
	}

	private static boolean isDirEmpty(final Path directory) throws IOException {
	    try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
	        return !dirStream.iterator().hasNext();
	    }
	}
	
	void CopytoDirAndRename (File file){
		
		String FinalName= file.getName();
		FinalName= this.counter + "-"+FinalName;
		this.counter = String.format("%06d", Integer.parseInt(this.counter) + 1); // Incrementar y formatear con ceros a la izquierda
		FinalName=this.DestFolder + "/" + FinalName;
		
		
		System.out.println("Fichero "+ file.getAbsolutePath() +" renombrado a " + FinalName);
		try {
		    FileUtils.copyFile(file, new File( FinalName));
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		
	}
	@Override
	public void AddImage(File Img, double Duration) throws Exception {
		System.out.println("====Inserted PHOTO:"+Img.getAbsolutePath());
		//para los datos de tama�o
		this.counter_photo++;
		CopytoDirAndRename (Img);
		
	}

	@Override
	public void AddVideo(File Video, double Duration, double init,double end, int width, int height) {
		System.out.println("====Inserted VIDEO:"+Video.getAbsolutePath());
		//para los datos de tama�o
		CopytoDirAndRename (Video);
		this.counter_video++;	
		}

	
	public void Write() throws Exception{
		 // Use a Transformer for output

	}
	@Override
	public void AddMusic(File Song, double Duration) {
		System.out.println("====Inserted Music (NO IMPLEMENTADO):"+Song.getAbsolutePath());
		//para los datos de tama�o
		
			
	}
	
	public void Print_Stats() throws Exception{
		System.out.println("Photos inserted:"+ this.counter_photo);
		System.out.println("Videos inserted:"+ this.counter_video);
		System.out.println("");
		
		 // Use a Transformer for output

	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// Tester
		FileMaker mm= new FileMaker("o:\\VideoTest\\");
	
		
		mm.AddImage(new File("O:\\Fotos de Familia\\2006\\06\\CIMG5933-2006_06_18-00_10_04.JPG"), 7);
		
		mm.AddVideo(new File("O:\\Fotos de Familia\\videos HD\\HDDCAM\\20111016\\10035146969855990062\\00050.mts"),
				24.386, 10,15, 1280, 720);
		
		
	
	
		
		
	}

	
	
	
}
