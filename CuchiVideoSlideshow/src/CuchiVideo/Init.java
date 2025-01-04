package CuchiVideo;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import CuchiVideo.Interfaces.FileMaker;
import CuchiVideo.Tools.VideoTools;
import CuchiVideo.logica.SelectMediaAlgorithm;
import lnkparser.LnkParser;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

public class Init {

    /**
     * @param args
     *
     * 1 Carpeta y subcarpetas para buscar 2 Duracion del video (segundos) 3
     * Duracion de los trozos de fotos en segundos puede ser 2.5 4 Duracion de
     * los trozos de video en segundos puede ser 4.5 5 Fichero de Destino
     * (extension wlmp) 6 Carpeta con los audios 7 Ratio de fotos video (si no
     * se pone no se aplica, es para las nuevas versiones que no generan video)
     * por ejemplo 3 fotos por video
     * @throws MagicException
     * @throws MagicMatchNotFoundException
     * @throws MagicParseException
     *
     * Un ejemplo de params
     *
     *
     * "D:\Video Reyes 2021\input" 2500 2.5 4.5 "e:\Video Reyes 2021\seleccion"
     * "C:\Users\Default\Music" 3
     *
     */
    static Connection c = null; // Conexión a la base de datos
    static Statement stmt = null; // Declaración para ejecutar consultas SQL
    static PreparedStatement pstmt = null; // Declaración preparada para consultas SQL
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Formato de fecha
    public static int NumFotos; // Número de fotos procesadas
    public static int NumVideos; // Número de videos procesados
    public static double DuracionTotalVideos; // Duración total de los videos procesados

    public static void main(String[] args) throws Exception {

        if (args.length == 0) { // Verifica si no se han proporcionado argumentos
            System.out.println("NO HAY PARAMETROS, SE HAN COGIDO LOS VALORES POR DEFECTO.");
            args = new String[]{
                "E:\\2024", // Carpeta de entrada por defecto
                "3600", // Duraci�n del video por defecto
                "2.5", // Duraci�n de fotos por defecto
                "4.5", // Duraci�n de video por defecto
                "C:\\Video R2024\\", // Fichero de destino por defecto
                "", // Carpeta de audios por defecto
                "3" // Ratio de fotos por video por defecto
            };
        }
        // Método principal que inicia la ejecución del programa
        if (args.length != 7) { // Verifica que se reciban 7 argumentos
            System.out.println("La cagaste. Mal argumentos. Número de argumentos: " + args.length);
            System.out.println("Ejemplo de argumentos esperados:");
            System.out.println("1. Carpeta de entrada: D:\\Video Reyes 2021\\input");
            System.out.println("2. Duración del video (segundos): 2500");
            System.out.println("3. Duración de fotos (segundos): 2.5");
            System.out.println("4. Duración de video (segundos): 4.5");
            System.out.println("5. Fichero de destino (extensión wlmp): e:\\Video Reyes 2021\\seleccion");
            System.out.println("6. Carpeta con los audios: C:\\Users\\Default\\Music");
            System.out.println("Si la carpeta de audios está vacía, no se elegirán audios.");
            System.out.println("7. Ratio de fotos por video: 3");
            System.out.println("Ejemplo de comando: java Init \"D:\\Video Reyes 2021\\input\" 2500 2.5 4.5 \"e:\\Video Reyes 2021\\seleccion\" \"C:\\Users\\Default\\Music\" 3");
            System.exit(1); // Termina el programa si los argumentos son incorrectos
        }

        String strFile = "test.db"; // Nombre del archivo de la base de datos
        boolean escanea = true; // Bandera para determinar si se escanean los archivos

		
        File myFile = new File(strFile); // Crea un objeto File para el archivo de la base de datos

        if (myFile.exists() && escanea) { // Si el archivo existe y escanea es verdadero
            myFile.delete(); // Elimina el archivo
        }

        System.out.println(".: CUCHI PROGRAMA PARA HACER VIDEOS FELICES :.");
        System.out.println("Parametros del video a crear:");
        System.out.println("Carpeta Origen:\t" + args[0]);
        System.out.println("Carpeta Audio:\t" + args[5]);
        System.out.println("Salida:\t" + args[4]);
        System.out.println("Duracion:\t" + args[1] + "seg");
        System.out.println("Duracion Foto:\t" + args[2] + "seg");
        System.out.println("Duracion Video:\t" + args[3] + "seg");

        SetUp(strFile); // Configura la conexión a la base de datos

        if (escanea) { // Si escanea es verdadero
            System.out.println("Comenzamos a escanear. i=Image y V=video");
            CreaBD(strFile); // Crea la base de datos SQLite

            ProcesaFicheros(args[0]); // Procesa los archivos en el directorio especificado
        }

        InformeFicherosBd(); // Informa sobre los archivos procesados en la base de datos

        // Limpia el campo seleccion
        stmt.executeUpdate("UPDATE IMAGES SET SELECCIONADA=0");
        stmt.executeUpdate("UPDATE VIDEOS SET SELECCIONADA=0");

        // Inserta ficheros en el sistema
        FileMaker FM = new FileMaker(args[4]); // Crea un objeto FileMaker para el archivo de salida
        SelectMediaAlgorithm.RandomSelect(c, FM, args); // Selecciona medios aleatorios
        FM.Print_Stats(); // Imprime estadísticas de los archivos procesados

        System.out.println("FINAL FELIZ!!");
        stmt.close(); // Cierra la declaración
        c.close(); // Cierra la conexión a la base de datos
    }

    private static void InformeFicherosBd() throws SQLException {
        // Esta función informa sobre los archivos que han sido procesados en la base de datos,
        // incluyendo el conteo de imágenes y videos, así como sus tamaños y otras métricas relevantes.

        System.out.println("=========\n FIN DE ESCANEO");

        ResultSet rs = stmt.executeQuery("SELECT count(*),avg(SIZE) FROM IMAGES;"); // Consulta para contar imágenes y obtener tamaño promedio
        rs.next();
        System.out.println("Procesadas correctamente " + rs.getString(1) + "imagenes, con un tama�o medio de "
                + rs.getString(2));
        NumFotos = Integer.parseInt(rs.getString(1)); // Almacena el número de fotos procesadas

        // Consulta para contar videos y obtener tamaño, duración y calidad promedio
        rs = stmt.executeQuery("SELECT count(*),avg(SIZE),avg(LENGHT),avg(QUALITY) FROM VIDEOS;");
        rs.next();

        System.out.println("Procesados correctamente " + rs.getString(1) + " videos, con un tama�o medio de "
                + rs.getString(2) + ", con una duracion media de " + rs.getString(3) + ", y una calidad media de " + rs.getString(4));

        NumVideos = Integer.parseInt(rs.getString(1)); // Almacena el número de videos procesados
        DuracionTotalVideos = rs.getDouble(1) * rs.getDouble(3); // Calcula la duración total de los videos

        rs = stmt.executeQuery("SELECT count(*) FROM OUTS;"); // Consulta para contar archivos no procesados
        rs.next();

        System.out.println("Se han quedado fuera " + rs.getString(1) + " ficheros");
    }

    private static void ProcesaFicheros(String Directorio) throws Exception {
        // Procesa los archivos en el directorio especificado
        listf(Directorio); // Llama a la función para listar archivos
    }

    public static void listf(String directoryName) throws Exception {
        // Lista todos los archivos en el directorio y sus subdirectorios
        File directory = new File(directoryName); // Crea un objeto File para el directorio

        // Obtiene todos los archivos de un directorio
        File[] fList = directory.listFiles();
        int salto = 0; // Contador para nuevas líneas
        for (File file : fList) {
            // Verifica si el archivo es un enlace
            String filename = file.getName();
            if (filename.substring(filename.lastIndexOf(".") + 1, filename.length()).equalsIgnoreCase("lnk")) {
                // Si es un enlace, obtiene el archivo real
                LnkParser l = new LnkParser(file);
                file = new File(l.getRealFilename());
            }

            if (file.isFile() && file.length() >= 200 * 1024) { // Si es un archivo y su tamaño es mayor a 200 KB
                try {
                    InsertFile(file); // Inserta el archivo en la base de datos
                } catch (Exception e) {
                    e.printStackTrace(); // Imprime la traza de la excepción
                }
                salto++; // Incrementa el contador
                if (salto % 30 == 0) { // Imprime nueva línea cada 30 archivos
                    System.out.println();
                }
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath()); // Llama recursivamente a la función si es un directorio
            }
        }
    }

    private static void InsertFile(File file) throws Exception {
        // Inserta un archivo en la base de datos
        String mimeType = getMime(file); // Obtiene el tipo MIME del archivo

        if (mimeType.equalsIgnoreCase("image")) { // Si el archivo es una imagen
            long Size = file.length(); // Obtiene el tama�o del archivo
            String Date = ImageFechaFoto(file); // Obtiene la fecha de la imagen

            if (Date.equalsIgnoreCase("")) {
                throw new Exception("FECHA VACIA!!"); // Lanza excepci�n si la fecha est� vac�a
            }

            pstmt = c.prepareStatement("INSERT INTO IMAGES (FILE,SIZE,DATE,QUALITY) values (?,?,?, 0)"); // Prepara la consulta
            pstmt.setString(1, file.getAbsolutePath()); // Establece el archivo
            pstmt.setLong(2, Size); // Establece el tama�o
            pstmt.setString(3, Date); // Establece la fecha
            pstmt.executeUpdate(); // Ejecuta la consulta
            
            System.out.print("i"); // Escribe 'i' por haber insertado una imagen
        }

        // Si el archivo es un video
        if (mimeType.equalsIgnoreCase("video")) {
            long Size = file.length(); // Obtiene el tamaño del archivo
            String Date = dateFormat.format(file.lastModified()); // Obtiene la fecha de modificación

            long[] temp = VideoTools.GetVideoLenghtAndRate(file); // Obtiene la duración y calidad del video

            pstmt = c.prepareStatement("INSERT INTO VIDEOS  (FILE,SIZE,DATE,LENGHT,QUALITY,WIDTH,HEIGHT) values (?,?,?, ?,?,?,?)"); // Prepara la consulta
            pstmt.setString(1, file.getAbsolutePath()); // Establece el archivo
            pstmt.setLong(2, Size); // Establece el tamaño
            pstmt.setString(3, Date); // Establece la fecha
            pstmt.setLong(4, temp[0]); // Establece la duración
            pstmt.setLong(5, temp[1]); // Establece la calidad
            pstmt.setLong(6, temp[2]); // Establece el ancho
            pstmt.setLong(7, temp[3]); // Establece la altura

            pstmt.executeUpdate(); // Ejecuta la consulta
			System.out.print("v"); // Escribe 'i' por haber insertado una imagen
        }
    }

    private static String ImageFechaFoto(File file) throws ImageProcessingException, IOException {
        // Obtiene la fecha de la foto a partir de los metadatos EXIF
        Metadata metadata = ImageMetadataReader.readMetadata(file); // Lee los metadatos de la imagen

        Directory directory = metadata.getDirectory(ExifSubIFDDirectory.class); // Obtiene el directorio EXIF
        if (directory != null) {
            Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL); // Obtiene la fecha original
            if (date != null) {
                return dateFormat.format(date); // Retorna la fecha formateada
            }
        }

        // Si no se encuentra la fecha EXIF, se utiliza la fecha de modificación
        return dateFormat.format(file.lastModified()); // Retorna la fecha de modificación formateada
    }

    private static String getMime(File file) throws SQLException {
        // Obtiene el tipo MIME del archivo basado en su extensión
        String ext = FilenameUtils.getExtension(file.getAbsolutePath());
        if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")) {
            return "image"; // Retorna "image" si es una imagen
        } else if (ext.equalsIgnoreCase("mov") || ext.equalsIgnoreCase("avi")
                || ext.equalsIgnoreCase("mts") || ext.equalsIgnoreCase("wmv")
                || ext.equalsIgnoreCase("mp4") || ext.equalsIgnoreCase("mpg")) {
            return "video"; // Retorna "video" si es un video
        } else {
            pstmt = c.prepareStatement("INSERT INTO OUTS (FILE,SIZE) values (?, 0)"); // Prepara la consulta para archivos no procesados
            pstmt.setString(1, file.getAbsolutePath()); // Establece el archivo
            pstmt.executeUpdate(); // Ejecuta la consulta
            return ""; // Retorna vacío para archivos no reconocidos
        }
    }

    public static void SetUp(String strFile) throws Exception {
        // Configura la conexión a la base de datos SQLite
        Class.forName("org.sqlite.JDBC"); // Carga el driver JDBC
        c = DriverManager.getConnection("jdbc:sqlite:" + strFile); // Establece la conexión
        stmt = c.createStatement(); // Crea una declaración para ejecutar consultas
    }

    public static void CreaBD(String strFile) {
        // Crea las tablas necesarias en la base de datos
        try {
            String sql = "CREATE TABLE IMAGES "
                    + "(FILE           TEXT    NOT NULL, "
                    + " SIZE            INT     NOT NULL, "
                    + " DATE        DATE         ,"
                    + " QUALITY     VARCHAR(5)   ,"
                    + " SELECCIONADA     INT   "
                    + ")";
            stmt.executeUpdate(sql); // Ejecuta la consulta para crear la tabla de imágenes

            sql = "CREATE VIEW IMAGES_ORDERED AS "
                    + " SELECT FILE,SIZE,DATE,QUALITY,SELECCIONADA"
                    + " FROM IMAGES ORDER BY DATE ASC";
            stmt.executeUpdate(sql); // Crea una vista ordenada de las imágenes

            sql = "CREATE TABLE VIDEOS "
                    + "(FILE           TEXT    NOT NULL, "
                    + " SIZE            INT     NOT NULL, "
                    + " DATE        DATE         ,"
                    + " LENGHT        INT         ,"
                    + " QUALITY     VARCHAR(5)   ,"
                    + " WIDTH     INT   ,"
                    + " HEIGHT     INT   ,"
                    + " SELECCIONADA     INT   "
                    + ")";
            stmt.executeUpdate(sql); // Ejecuta la consulta para crear la tabla de videos
            sql = "CREATE VIEW VIDEOS_ORDERED AS"
                    + " SELECT FILE,SIZE,DATE,LENGHT,QUALITY,WIDTH,HEIGHT,SELECCIONADA"
                    + " FROM VIDEOS ORDER BY DATE ASC";
            stmt.executeUpdate(sql); // Crea una vista ordenada de los videos

            sql = "CREATE TABLE OUTS "
                    + "(FILE           TEXT    NOT NULL, "
                    + " SIZE            INT     NOT NULL "
                    + ")";
            stmt.executeUpdate(sql); // Ejecuta la consulta para crear la tabla de archivos no procesados

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage()); // Imprime el error
            System.exit(0); // Termina el programa en caso de error
        }
        System.out.println("Table created successfully"); // Mensaje de éxito
    }

}
