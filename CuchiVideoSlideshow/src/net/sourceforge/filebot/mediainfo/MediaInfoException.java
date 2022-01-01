
package net.sourceforge.filebot.mediainfo;


import com.sun.jna.Platform;


public class MediaInfoException extends RuntimeException {
	
	public MediaInfoException(LinkageError e) {
		//CUCHI: si aqui te da un pete de Platform no encontrada, lo que toca hacer es añadir la native library de mediainfo
 
	}
	

	public MediaInfoException(String msg, Throwable e) {
		super(msg, e);
	}
	
}
