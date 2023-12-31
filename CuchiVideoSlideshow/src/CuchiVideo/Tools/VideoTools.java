package CuchiVideo.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;

import net.sourceforge.filebot.mediainfo.MediaInfo;


public class VideoTools {

	/**
	 * @param args
	 * 
	 * 
	 * Devuelve un array de long con
	 * 0-duracion
	 * 1-bitrate
	 * 2-widht
	 * 3-height
	 * 
	 */
    public static long[] GetVideoLenghtAndRate(File file) throws Exception {
   //Hay que tener el FFMPEG en el PATH, realmente ffprobe
    	System.out.println("FIle:"+ file.getAbsolutePath());
   long [] retorno = new long [4]; 	
   String command = "ffprobe -show_entries stream=width,height,duration,bit_rate -select_streams v:0 -i  \"" + file.getAbsolutePath() + "\"";
            //System.out.println(command);
            
            // Ejecutar el comando y obtener el flujo de salida
            Process process = Runtime.getRuntime().exec(command);
            //BufferedReader reader = new BufferedReader(new InputStreamReader(process.get.getErrorStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Leer las líneas de la salida
            String line;
            
            /* Devuelve un array de long con
            * 0-duracion
            * 1-bitrate
            * 2-widht
            * 3-height
            */
            while ((line = reader.readLine()) != null) {
               // System.out.println("linea:"+line);
                // Buscar información específica en la salida de FFmpeg
                if (line.startsWith("width=")) {
                    retorno[2] = Long.parseLong(line.substring(6));
                } else if (line.startsWith("height=")) {
                    retorno[3]  = Long.parseLong(line.substring(7));
                } else if (line.startsWith("duration=")) {
                    System.out.println(line.substring(9));
                    retorno[0]  = Math.round(Double.parseDouble(line.substring(9)));
                }
                else if (line.startsWith("bit_rate=")) {
                    retorno[1]  = Long.parseLong(line.substring(9));
                }
            }

            // Esperar a que termine el proceso
            int exitCode = process.waitFor();
            System.out.println(Arrays.toString(retorno));

  return retorno;
  }

    }

