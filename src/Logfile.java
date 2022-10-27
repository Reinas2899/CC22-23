import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logfile {

    public Logfile(int porta, String modo, int timeout, LocalDateTime date, String type, String info, String logFilename) throws FileNotFoundException {
        boolean result= false;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        try {

            String [] dirs = logFilename.split("/");
            String pathaux = dirs[1]+"//"+dirs[2];
            String pathFinal = pathaux+"//" + dirs[3] ;
            result = true;

            Path path = Paths.get(pathaux);
            if(Files.notExists(path)){
                Files.createDirectories(path);
                File file = new File(pathaux+"//" + dirs[3]);
                result = file.createNewFile();
                pathFinal = file.getAbsolutePath();
                System.out.println(pathFinal);
            }

            if(result){
                LocalDateTime createdNow = LocalDateTime.now();
                String datacriacao = dtf.format(createdNow);

                FileWriter myWriter = new FileWriter(pathFinal,true);
                myWriter.write(datacriacao + " " + "EV" + " " +  "log-file-created" + " " + logFilename+"\n");
                myWriter.close();


            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }}

    public void updateLogFile(int porta,String modo,int timeout,LocalDateTime date,String type,String info,String logFilename){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

            FileWriter myWriter = new FileWriter(pathFinal,true);
            myWriter.write(data + " " + type + " " + porta + " " + timeout + " " + modo+"\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

