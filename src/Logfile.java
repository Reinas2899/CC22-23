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

    String logFilename;
    

    public String getLogFilename() {
        return logFilename;
    }

    public void setLogFilename(String logFilename) {
        this.logFilename = logFilename;
    }

    public Logfile(String logFilename) throws FileNotFoundException {
        this.logFilename = logFilename;
        try {

            String [] dirs = logFilename.split("/");
            String pathaux = dirs[1]+"//"+dirs[2];
            String pathFinal = pathaux+"//" + dirs[3] ;
           

            Path path = Paths.get(pathaux);
            if(Files.notExists(path)){
                Files.createDirectories(path);
                File file = new File(pathaux+"//" + dirs[3]);
                pathFinal = file.getAbsolutePath();
                System.out.println(pathFinal);
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }}

    public void updateLogFileST(int porta,String modo,int timeout,LocalDateTime date,String type, String endereço){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

        FileWriter myWriter = new FileWriter(pathFinal,true);
        myWriter.write(data + " " + type + " " + endereço + " " + porta + " " + timeout + " " + modo+"\n");
        myWriter.close();
                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLogFileEV(String razao,LocalDateTime date,String type, String path){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

        FileWriter myWriter = new FileWriter(pathFinal,true);
        myWriter.write(data + " " + type + " " + razao + " " + path+"\n");
        myWriter.close();
                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLogFileSP(LocalDateTime date,String type, String endereço, String razao){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

        FileWriter myWriter = new FileWriter(pathFinal,true);
        myWriter.write(data + " " + type + " " + endereço + " " + razao + "\n");
        myWriter.close();
                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLogFileTO(String tipo,int timeout,LocalDateTime date,String type, String endereço){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

        FileWriter myWriter = new FileWriter(pathFinal,true);
        myWriter.write(data + " " + type + " " + endereço + " " + timeout + " " + tipo+"\n");
        myWriter.close();
                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLogFileFL(String erro,LocalDateTime date,String type, String endereço){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

        FileWriter myWriter = new FileWriter(pathFinal,true);
        myWriter.write(data + " " + type + " " + endereço + " " + erro +"\n");
        myWriter.close();
                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLogFileEZ(String endereçoNlocal,LocalDateTime date,String type, String endereçolocal, String papel){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

        FileWriter myWriter = new FileWriter(pathFinal,true);
        myWriter.write(data + " " + type + " " + endereçoNlocal + " " + endereçolocal + " " + papel + "\n");
        myWriter.close();
                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void updateLogFileER(String dadosopcionais,int timeout,LocalDateTime date,String type, String endereço){ 
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

        FileWriter myWriter = new FileWriter(pathFinal,true);
        myWriter.write(data + " " + type + " " + endereço + " " + dadosopcionais +"\n");
        myWriter.close();
                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLogFileZT(String papel,String endereçoOutraPonta,long duraçao, int tamanho,LocalDateTime date,String type){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

        FileWriter myWriter = new FileWriter(pathFinal,true);
        myWriter.write(data + " " + type + " " + endereçoOutraPonta + " " + papel + " " + duraçao + " " + tamanho+"\n");
        myWriter.close();
                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLogFileRP_RR(String dados,LocalDateTime date,String type, String endereço){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

        FileWriter myWriter = new FileWriter(pathFinal,true);
        myWriter.write(data + " " + type + " " + endereço + " " +  dados+"\n");
        myWriter.close();
                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLogFileQR_QE(String dados,LocalDateTime date,String type, String endereço){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

        FileWriter myWriter = new FileWriter(pathFinal,true);
        myWriter.write(data + " " + type + " " + endereço + " " + dados +"\n");
        myWriter.close();
                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

