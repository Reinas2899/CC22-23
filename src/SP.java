import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SP {
    private static List<LineParameter> lineParameters = new ArrayList<>();
    private static List<ParameterDB> linesParametersDB = new ArrayList<>();

    public static void main(String[] args) throws IOException, SintaxeIncorretaException, ClassNotFoundException {
        InetAddress address;
        int porta = 4445;
        DatagramSocket socket = new DatagramSocket(porta);
        byte[] buf = new byte[1000];
        boolean running = true;
        LocalDateTime runningNow = LocalDateTime.now();
        String modo = "debug";
        int timeout = 2000;
        updateLogFile(porta,modo,timeout,runningNow,"ST","");
        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);//prepara o datagrama
            socket.receive(packet); //fica à espera de receber

            LocalDateTime receivedNow = LocalDateTime.now();
            updateLogFile(porta,modo,timeout,receivedNow,"QR","");

            //path completo do ficheiro de configuracao
            //parser("/home/joao/IdeaProjects/parsefile/src/main/java/sp.conf");
            //dbContent("/home/joao/IdeaProjects/parsefile/src/main/java/example-com.db");

            address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);


            byte [] data = packet.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            DNSMsg m = (DNSMsg) is.readObject();
            if(m==null){ //perguntar se esta é a linha anterior correspondente à descodificacao
                LocalDateTime errorconvertNow = LocalDateTime.now();
                updateLogFile(porta,modo,timeout,errorconvertNow,"ER","");
            }
            System.out.println(m);
        }
        socket.close();
        LocalDateTime shutdownNow = LocalDateTime.now();
        updateLogFile(porta,modo,timeout,shutdownNow,"SP","");


    }


    /**
     * Autor: João Castro
     * Modificado: 20/out/2022
     * Descrição:
     * */
    public static void parser (String filename) throws FileNotFoundException, SintaxeIncorretaException {
        String[] componente;
        List<String> linhas = lerFicheiro(filename);
        int line = 0;
        for (String linha : linhas) {
            componente = linha.split(" ");
            if (!linha.isEmpty() && componente[0].charAt(0) != '#' && componente.length==3) {
                LineParameter l = new LineParameter(componente[0], componente[1], componente[2]);
                lineParameters.add(l);
            } else if(componente[0].charAt(0) != '#' && componente.length>3) throw new SintaxeIncorretaException("Sintaxe do ficheiro está incorreta."+line);
            line++;
        }
    }

    public static void dbContent(String filename) throws FileNotFoundException,SintaxeIncorretaException {
        //linesParametersDB.clear();
        String[] componente;
        ParameterDB p = null;
        List<String> linhas = lerFicheiro(filename);
        int line = 0;
        for (String linha : linhas) {
            componente = linha.split(" ");
            if(!linha.isEmpty() && componente[0].charAt(0)!='#' && componente.length==4) {
                linesParametersDB.add(new ParameterDB(componente[0], componente[1], componente[2], componente[3], null));
            }
            if(!linha.isEmpty() && componente[0].charAt(0)!='#' && componente.length==5){
                linesParametersDB.add(new ParameterDB(componente[0], componente[1], componente[2], componente[3], componente[4]));
            }
            else if(componente[0].charAt(0) != '#' && componente.length>5) throw new SintaxeIncorretaException("Sintaxe do ficheiro está incorreta."+line);
            line++;
        }
    }

    /**
     * Autor: João Castro
     * Modificado: 20/out/2022
     * Descrição:
     * */
     public static List<String> lerFicheiro (String nomeFich) throws FileNotFoundException {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(nomeFich), StandardCharsets.UTF_8);
        } catch (IOException exc) {
            lines = new ArrayList<>();
        }
        if (lines.isEmpty()) throw new FileNotFoundException("Ficheiro não encontrado");
        return lines;
    }

    public static void updateLogFile(int porta,String modo,int timeout,LocalDateTime date,String type,String info){
        boolean result= false;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy.HH:mm:ss:SSS");
        try {
            File myObj = new File("SP.log");
            result = myObj.createNewFile();
            if(result){
                LocalDateTime createdNow = LocalDateTime.now();
                String datacriacao = dtf.format(createdNow);
                FileWriter myWriter = new FileWriter("SP.log",true);
                myWriter.write(datacriacao + " " + "EV" + " " +  "log-file-created" + " " + "SP.log"+"\n");
                myWriter.close();

            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        String data = dtf.format(date);
        try {
                FileWriter myWriter = new FileWriter("SP.log",true);
                myWriter.write(data + " " + type + " " + porta + " " + timeout + " " + modo+"\n");
                myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int countAuthoritatives(){
         int c=0;
         for(ParameterDB p : linesParametersDB){
             if(p.getTipo().equals(ParameterDB.Tipo.NS)) c++;
         }
         return c;
    }

    public int countExtravalues(){
        int c=0;
        for(ParameterDB p : linesParametersDB){
            if(p.getTipo().equals(ParameterDB.Tipo.A)) c++;
        }
        return c;
    }

}

