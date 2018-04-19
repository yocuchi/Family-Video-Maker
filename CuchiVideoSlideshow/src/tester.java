import java.io.File;
import java.io.IOException;

import lnkparser.LnkParser;


public class tester {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		   //Got in milliseconds
		/*String s="85404.807111";
		Double.parseDouble(s);
		 System.out.println(""+(int)Double.parseDouble(s));
		 */
		
		//get a file link
		
		LnkParser l = new LnkParser(new File ("D:\\FRANCIS.lnk"));
		
		System.out.println("LIN A "+l.getRealFilename());
		
		 String path = "."; 
		 
		  String files;
		  File folder = new File(l.getRealFilename());
		  File[] listOfFiles = folder.listFiles(); 
		 
		  for (int i = 0; i < listOfFiles.length; i++) 
		  {
		 
		   if (listOfFiles[i].isFile()) 
		   {
		   files = listOfFiles[i].getName();
		   System.out.println(files);
		      }
		  }
		}
	}


