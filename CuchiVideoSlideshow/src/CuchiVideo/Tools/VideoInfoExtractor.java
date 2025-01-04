package CuchiVideo.Tools;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IStream;

public class VideoInfoExtractor {

    public static void main(String[] args) {
        String videoFilePath = "C:\\Users\\hernandezcfran\\OneDrive - Ayuntamiento de Madrid\\Escritorio\\Fotos Ordenar\\out\\2023\\01\\20230101015620_01_20230101_015337.mp4";  // Reemplaza con la ruta de tu archivo de video

        // Configura Xuggle
        com.xuggle.xuggler.IContainer container = IContainer.make();

        if (container.open(videoFilePath, IContainer.Type.READ, null) >= 0) {
            // Obtén la duración del video en microsegundos
            long duration = container.getDuration();

            // Obtén la información del primer flujo de video
            IStream videoStream = container.getStream(0);
            int width = videoStream.getStreamCoder().getWidth();
            int height = videoStream.getStreamCoder().getHeight();

            System.out.println("Duración: " + duration / 1000000 + " segundos");
            System.out.println("Ancho: " + width + " píxeles");
            System.out.println("Altura: " + height + " píxeles");
        } else {
            System.err.println("Error al abrir el archivo de video");
        }
    }
}
