package CuchiVideo.Interfaces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

public class MovieMaker2014 implements VideoOutputInterface {

	File DestFile;
	public Document doc;
	DocumentBuilder builder;
	DocumentBuilderFactory domFactory;
	public int MediaItemId,ExtentId;
	
	public MovieMaker2014(String DestFile) throws Exception{
		this.DestFile=new File(DestFile);
		
		
		//System.out.println(this.getClass().getResource("").getPath());
		FileUtils.copyURLToFile(this.getClass().getResource("CuchiFilm.wlmp"), this.DestFile);
		
		domFactory = DocumentBuilderFactory.newInstance(); 
		domFactory.setIgnoringComments(true);
		builder = domFactory.newDocumentBuilder(); 
		doc = builder.parse(this.DestFile);
		MediaItemId=0;
		ExtentId=4;
	}

	@Override
	public void AddImage(File Img, double Duration) throws Exception {
		System.out.println("====Inserted FILE:"+Img.getAbsolutePath());
		//para los datos de tamaño
		
		BufferedImage bimg = ImageIO.read(Img);
		int width          = bimg.getWidth();
		int height         = bimg.getHeight();
		
		//creo un media Item
		//mediaItems
		Node MediaItems = doc.getElementsByTagName("MediaItems").item(0);
		//mediaItem		
		this.MediaItemId++;
		Element MediaItem = doc.createElement("MediaItem");
		MediaItem.setAttribute("id", this.MediaItemId+"");
		MediaItem.setAttribute("filePath", Img.getAbsolutePath());
		MediaItem.setAttribute("mediaItemType", "2");//foto
		MediaItem.setAttribute("arWidth", width+"");
		MediaItem.setAttribute("arHeight", height+"");
		MediaItem.setAttribute("duration", "0");
		MediaItem.setAttribute("songTitle", "");
		MediaItem.setAttribute("songArtist", "");
		MediaItem.setAttribute("songAlbum", "");
		MediaItem.setAttribute("songCopyrightUrl", "");
		MediaItem.setAttribute("songArtistUrl", "");
		MediaItem.setAttribute("songAudioFileUrl", "");
		MediaItem.setAttribute("stabilizationMode", "0");
		
		MediaItems.appendChild(MediaItem); 
		
		//imageCLIP dentro de extents
		//extends
		Node Extents = doc.getElementsByTagName("Extents").item(0);
		//ImageClip
		this.ExtentId++;
		Element ImageClip = doc.createElement("ImageClip");
		ImageClip.setAttribute("extentID", this.ExtentId+"");
		ImageClip.setAttribute("gapBefore","0");
		ImageClip.setAttribute("mediaItemID", this.MediaItemId+"");
		ImageClip.setAttribute("duration", Duration+"");//en segundos
		Element Effects = doc.createElement("Effects");
		Element Transitions = doc.createElement("Transitions");
		Element BoundProperties = doc.createElement("BoundProperties");
		Element BoundPropertyInt = doc.createElement("BoundPropertyInt");
		BoundPropertyInt.setAttribute("Name", "rotateStepNinety");
		BoundPropertyInt.setAttribute("Value", "0");
		BoundProperties.appendChild(BoundPropertyInt);
		ImageClip.appendChild(Effects);
		ImageClip.appendChild(Transitions);
		ImageClip.appendChild(BoundProperties);
		//inserto al principio de Extents
		Extents.insertBefore(ImageClip, Extents.getFirstChild());
		
		
		
		//añadirlo en El Extent Refs de Extent Selector
		//para buscar bien el XPATH http://stackoverflow.com/questions/4524586/retrieve-value-of-xml-node-and-node-attribute-using-xpath-in-jaxp
		Node ExtentSelector1ExtentRefs = doc.getElementsByTagName("ExtentRefs").item(0);
		Element ExtentRef = doc.createElement("ExtentRef");
		ExtentRef.setAttribute("id", this.ExtentId+"");
		ExtentSelector1ExtentRefs.appendChild(ExtentRef);
	}

	@Override
	public void AddVideo(File Video, double Duration, double init,double end, int width, int height) {
		System.out.println("====Inserted VIDEO:"+Video.getAbsolutePath());
		//para los datos de tamaño
		
	
		
		//creo un media Item
		//mediaItems
		Node MediaItems = doc.getElementsByTagName("MediaItems").item(0);
		//mediaItem		
		this.MediaItemId++;
		Element MediaItem = doc.createElement("MediaItem");
		MediaItem.setAttribute("id", this.MediaItemId+"");
		MediaItem.setAttribute("filePath", Video.getAbsolutePath());
		MediaItem.setAttribute("mediaItemType", "1");//Video
		MediaItem.setAttribute("arWidth", width+"");
		MediaItem.setAttribute("arHeight", height+"");
		MediaItem.setAttribute("duration", Duration+"");
		MediaItem.setAttribute("songTitle", "");
		MediaItem.setAttribute("songArtist", "");
		MediaItem.setAttribute("songAlbum", "");
		MediaItem.setAttribute("songCopyrightUrl", "");
		MediaItem.setAttribute("songArtistUrl", "");
		MediaItem.setAttribute("songAudioFileUrl", "");
		MediaItem.setAttribute("stabilizationMode", "0");
		
		MediaItems.appendChild(MediaItem); 
		
		//imageCLIP dentro de extents
		//extends
		Node Extents = doc.getElementsByTagName("Extents").item(0);
		//ImageClip
		this.ExtentId++;
		Element VideoClip = doc.createElement("VideoClip");
		VideoClip.setAttribute("extentID", this.ExtentId+"");
		VideoClip.setAttribute("gapBefore","0");
		VideoClip.setAttribute("mediaItemID", this.MediaItemId+"");
		VideoClip.setAttribute("inTime",init+"");//en segundos entrada del video
		VideoClip.setAttribute("outTime", end+"");//en segundos salida del video
		VideoClip.setAttribute("speed", 1+"");
		VideoClip.setAttribute("stabilizationMode", 0+"");
		Element Effects = doc.createElement("Effects");
		Element Transitions = doc.createElement("Transitions");
		Element BoundProperties = doc.createElement("BoundProperties");
		
		Element BoundPropertyInt = doc.createElement("BoundPropertyInt");
		BoundPropertyInt.setAttribute("Name", "rotateStepNinety");
		BoundPropertyInt.setAttribute("Value", "0");
		
		Element BoundPropertyBool= doc.createElement("BoundPropertyBool");
		BoundPropertyBool.setAttribute("Name", "Mute");
		BoundPropertyBool.setAttribute("Value", "false");
		
		Element BoundPropertyFloat= doc.createElement("BoundPropertyFloat");
		BoundPropertyFloat.setAttribute("Name", "Volume");
		BoundPropertyFloat.setAttribute("Value", "1");
		
		BoundProperties.appendChild(BoundPropertyInt);
		BoundProperties.appendChild(BoundPropertyBool);
		BoundProperties.appendChild(BoundPropertyFloat);
		
		VideoClip.appendChild(Effects);
		VideoClip.appendChild(Transitions);
		VideoClip.appendChild(BoundProperties);
		//inserto al principio de Extents
		Extents.insertBefore(VideoClip, Extents.getFirstChild());
		
		
		
		//añadirlo en El Extent Refs de Extent Selector
		//para buscar bien el XPATH http://stackoverflow.com/questions/4524586/retrieve-value-of-xml-node-and-node-attribute-using-xpath-in-jaxp
		Node ExtentSelector1ExtentRefs = doc.getElementsByTagName("ExtentRefs").item(0);
		Element ExtentRef = doc.createElement("ExtentRef");
		ExtentRef.setAttribute("id", this.ExtentId+"");
		ExtentSelector1ExtentRefs.appendChild(ExtentRef);

	}

	
	public void Write() throws Exception{
		 // Use a Transformer for output
	    TransformerFactory tFactory =
	    TransformerFactory.newInstance();
	    Transformer transformer = 
	    tFactory.newTransformer();

	    DOMSource source = new DOMSource(this.doc);
	    StreamResult result = new StreamResult(this.DestFile);
	    transformer.transform(source, result);
		
	}
	@Override
	public void AddMusic(File Song, double Duration) {
		System.out.println("====Inserted Music:"+Song.getAbsolutePath());
		//para los datos de tamaño
		
	
		Node MediaItems = doc.getElementsByTagName("MediaItems").item(0);
		
		//creo un etElementsByTagName("MediaItems").item(0);
		//mediaItem		
		this.MediaItemId++;
		Element MediaItem = doc.createElement("MediaItem");
		MediaItem.setAttribute("id", this.MediaItemId+"");
		MediaItem.setAttribute("filePath", Song.getAbsolutePath());
		MediaItem.setAttribute("mediaItemType", "3");//Audio
		MediaItem.setAttribute("arWidth",0+"");
		MediaItem.setAttribute("arHeight", 0+"");
		MediaItem.setAttribute("duration", Duration+"");
		MediaItem.setAttribute("songTitle", Song.getName());
		MediaItem.setAttribute("songArtist", "");
		MediaItem.setAttribute("songAlbum", "");
		MediaItem.setAttribute("songCopyrightUrl", "");
		MediaItem.setAttribute("songArtistUrl", "");
		MediaItem.setAttribute("songAudioFileUrl", "");
		MediaItem.setAttribute("stabilizationMode", "0");
		
		MediaItems.appendChild(MediaItem); 
		
		//AudioClip dentro de extents
		//extends
		Node Extents = doc.getElementsByTagName("Extents").item(0);
		//AudioClip
		this.ExtentId++;
		Element AudioClip = doc.createElement("AudioClip");
		AudioClip.setAttribute("extentID", this.ExtentId+"");
		AudioClip.setAttribute("gapBefore","0");
		AudioClip.setAttribute("mediaItemID", this.MediaItemId+"");
		AudioClip.setAttribute("inTime",0+"");//en segundos entrada del video
		AudioClip.setAttribute("outTime", 0+"");//en segundos salida del video
		AudioClip.setAttribute("speed", 1+"");
		
		Element Effects = doc.createElement("Effects");
		Element Transitions = doc.createElement("Transitions");
		Element BoundProperties = doc.createElement("BoundProperties");
		
		
		Element BoundPropertyBool= doc.createElement("BoundPropertyBool");
		BoundPropertyBool.setAttribute("Name", "Mute");
		BoundPropertyBool.setAttribute("Value", "false");
		
		Element BoundPropertyFloat= doc.createElement("BoundPropertyFloat");
		BoundPropertyFloat.setAttribute("Name", "Volume");
		BoundPropertyFloat.setAttribute("Value", "1");
		
		BoundProperties.appendChild(BoundPropertyBool);
		BoundProperties.appendChild(BoundPropertyFloat);
		
		AudioClip.appendChild(Effects);
		AudioClip.appendChild(Transitions);
		AudioClip.appendChild(BoundProperties);
		//inserto al principio de Extents
		Extents.insertBefore(AudioClip, Extents.getFirstChild());
		
		
		
		//añadirlo en El Extent Refs de Extent Selector
		//para buscar bien el XPATH http://stackoverflow.com/questions/4524586/retrieve-value-of-xml-node-and-node-attribute-using-xpath-in-jaxp
		//si bien este pájaro va en el extendID2
		Node ExtentSelector1ExtentRefs = doc.getElementsByTagName("ExtentRefs").item(1);
		Element ExtentRef = doc.createElement("ExtentRef");
		ExtentRef.setAttribute("id", this.ExtentId+"");
		ExtentSelector1ExtentRefs.appendChild(ExtentRef);

	
		
	}
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		MovieMaker2014 mm= new MovieMaker2014("d:\\out.wlmp");
		mm.MediaItemId=1;
		
		mm.AddImage(new File("D:\\Fotos de Familia\\2006\\06\\CIMG5933-2006_06_18-00_10_04.JPG"), 7);
		
		mm.AddVideo(new File("D:\\Fotos de Familia\\videos HD\\HDDCAM\\20111016\\10035146969855990062\\00050.mts"),
				24.386, 10,15, 1280, 720);
		
		
		/* mm.AddImage(new File("C:\\Users\\Public\\Pictures\\Sample Pictures\\Tulips.jpg"), 7);
		
		mm.AddVideo(new File("C:\\Users\\FRANCIS\\Desktop\\01\\VID_20130109_120740.mp4"),
				24.386, 10,15, 1280, 720);
		mm.AddVideo(new File("C:\\Users\\FRANCIS\\Desktop\\01\\VID_20130112_001556.mp4"),
				24.386, 10,15, 1280, 720);*/
		mm.AddMusic(new File("E:\\Documents and Settings\\Cuchi\\Documentos de Cuchi\\Mi música\\musica anades\\01 Recordarte hoy.mp3"), (double)02.0);
		mm.AddMusic(new File("E:\\Documents and Settings\\Cuchi\\Documentos de Cuchi\\Mi música\\musica anades\\01Micheal Learns To Rock - 25 Minutes.mp3"), (double)03.0);
		
		
		mm.Write();
		
		
	}

	
	
	
}
