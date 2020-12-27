package CuchiVideo.Interfaces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import CuchiVideo.VideoOutputInterface;

public class FileMaker implements VideoOutputInterface {
	/*
	 * Clase que genera videos y los saca en una carpeta, poniendo por orden de nombre las imagenes y losvideos.
	 * 
	 * 
	 * 
	 */
	
	
	File DestFolder;
	int counter;
	
	public FileMaker(String DestFolder) throws Exception{
		this.DestFolder=new File(DestFolder);
		this.counter=0;
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
		FinalName= this.counter + FinalName;
		this.counter++;
		
		System.out.println("Fichero "+ file.getAbsolutePath() +" renombrado a " + FinalName);
		try {
		    FileUtils.copyFile(file, new File( this.DestFolder + "/" + FinalName));
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		
	}
	@Override
	public void AddImage(File Img, double Duration) throws Exception {
		System.out.println("====Inserted FILE:"+Img.getAbsolutePath());
		//para los datos de tamaño
		CopytoDirAndRename (Img);
		
	}

	@Override
	public void AddVideo(File Video, double Duration, double init,double end, int width, int height) {
		System.out.println("====Inserted VIDEO:"+Video.getAbsolutePath());
		//para los datos de tamaño
		CopytoDirAndRename (Video);
			
		}

	
	public void Write() throws Exception{
		 // Use a Transformer for output

	}
	@Override
	public void AddMusic(File Song, double Duration) {
		System.out.println("====Inserted Music (NO IMPLEMENTADO):"+Song.getAbsolutePath());
		//para los datos de tamaño
		
			
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
