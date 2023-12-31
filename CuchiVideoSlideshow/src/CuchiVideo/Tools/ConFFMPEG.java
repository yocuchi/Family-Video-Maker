package CuchiVideo.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class ConFFMPEG {

    public static void main(String[] args) {
        String videoFilePath = "C:\\Users\\hernandezcfran\\OneDrive - Ayuntamiento de Madrid\\Escritorio\\Fotos Ordenar\\out\\2023\\06\\20230626062500_Drone Campamento y mas_Bebop_2_2023-06-26T062528+0200_803355.mp4"; // Ruta de tu archivo de video

        try {
            // Comando FFmpeg para obtener información del video
            String command = "ffprobe -show_entries stream=width,height,duration,bit_rate  -v error -i  \"" + videoFilePath + "\"";
            System.out.println(command);
            // Ejecutar el comando y obtener el flujo de salida
            Process process = Runtime.getRuntime().exec(command);
            //BufferedReader reader = new BufferedReader(new InputStreamReader(process.get.getErrorStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            //BufferedReader reader2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            // Leer las líneas de la salida
            String line;
            long [] retorno = new long [4]; 	
            /* Devuelve un array de long con
            * 0-duracion
            * 1-bitrate
            * 2-widht
            * 3-height
            */
            while ((line = reader.readLine()) != null) {
                System.out.println("linea:"+line);
                // Buscar información específica en la salida de FFmpeg
                if (line.startsWith("width=")) {
                    retorno[2] = Long.parseLong(line.substring(6));
                } else if (line.startsWith("height=")) {
                    retorno[3]  = Long.parseLong(line.substring(7));
                } else if (line.startsWith("duration=")) {
                    //System.out.println(line.substring(9));
                    retorno[0]  = Math.round(Double.parseDouble(line.substring(9)));
                }
                else if (line.startsWith("bit_rate=")) {
                    try{
                    retorno[1]  = Long.parseLong(line.substring(9));
                    } catch(Exception e){
                        //e.printStackTrace();
                    }
                }
            }
           /*  while ((line = reader2.readLine()) != null) {
                System.out.println("R2"+line);
            } 
            */
            // Esperar a que termine el proceso
            int exitCode = process.waitFor();
            System.out.println(Arrays.toString(retorno));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
