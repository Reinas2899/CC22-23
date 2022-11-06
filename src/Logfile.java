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

    public void updateLogFileST(int porta,String modo,int timeout,LocalDateTime date,String type,String logFilename, String endereço){
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

    public void updateLogFileSP(LocalDateTime date,String type,String logFilename, String endereço, String razao){
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

    public void updateLogFileTO(String tipo,int timeout,LocalDateTime date,String type,String logFilename, String endereço){
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

    public void updateLogFileFL(String erro,LocalDateTime date,String type,String logFilename, String endereço){
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

    public void updateLogFileEZ(String endereçoNlocal,LocalDateTime date,String type,String logFilename, String endereçolocal, String papel){
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


    public void updateLogFileER(String dadosopcionais,int timeout,LocalDateTime date,String type,String logFilename, String endereço){ 
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

    public void updateLogFileEV(String info,int timeout,LocalDateTime date,String type,String logFilename, String endereço){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

        FileWriter myWriter = new FileWriter(pathFinal,true);
        myWriter.write(data + " " + type + " " + endereço + " " + info+"\n");
        myWriter.close();
                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLogFileZT(String papel,String endereçoOutraPonta,String duraçao, String tamanho,LocalDateTime date,String type,String logFilename){
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

    public void updateLogFileRP_RR(String dados,LocalDateTime date,String type,String logFilename, String endereço, String sintaxe){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

        FileWriter myWriter = new FileWriter(pathFinal,true);
        myWriter.write(data + " " + type + " " + endereço + " " +  dados+ " " + sintaxe +"\n");
        myWriter.close();
                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLogFileQR_QE(String sintaxe,String dados,LocalDateTime date,String type,String logFilename, String endereço){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        String data = dtf.format(date);
        String [] dirs = logFilename.split("/");
        String pathaux = dirs[1]+"//"+dirs[2];
        String pathFinal = pathaux+"//" + dirs[3] ;
        try {

        FileWriter myWriter = new FileWriter(pathFinal,true);
        myWriter.write(data + " " + type + " " + endereço + " " + dados + " " + sintaxe +"\n");
        myWriter.close();
                   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

