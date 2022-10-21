import java.io.IOException;
import java.net.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Client {

    
    //private DNSMsg dns_Msg;
    public static void main(String[] args) throws IOException {
/*         DatagramSocket socket= new DatagramSocket();;
         InetAddress address= InetAddress.getByName("localhost");
         byte[] buf;
     COLOCAR DNSMSG
         String msg = "Olá servidor, eu sou o cliente.";

        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445); // controi datagrama
        socket.send(packet); *///envia datagrama pelo socket
        try {

            System.out.println(parser("dnsquery.txt").toString());
        } catch (SintaxeIncorretaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }


    

    public static DNSMsg parser (String filename) throws FileNotFoundException, SintaxeIncorretaException {
        String[] componente;
        HashMap<String,String> query_values = new HashMap<>();
        List<String> linhas = lerFicheiro(filename);
        int line = 0;
        for (String linha : linhas) {
            componente = linha.split(", ");
            if (!linha.isEmpty() && componente[0].charAt(0) != '#') {

                dividevalor(componente, query_values);
            } else if(componente[0].charAt(0) != '#' && componente.length>3) throw new SintaxeIncorretaException("Sintaxe do ficheiro está incorreta.");
            line++;
        }
        return constroiMsg(query_values);

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



    public static void dividevalor(String[] componentes, HashMap<String,String> query_values){
        
        String[] dividido = new String[2];
        
        for (String string : componentes) {
            dividido = string.split(" = ");
            query_values.put(dividido[0], dividido[1]);
        }
    }

    public static DNSMsg constroiMsg(HashMap<String,String> query_values){
     Header header = new Header(query_values.get("MESSAGE-ID"), 
        query_values.get("FLAGS"), 
        query_values.get("RESPONSE-CODE"), 
        query_values.get("N-VALUES"), 
        query_values.get("N-AUTHORITIES"), 
        query_values.get("N-EXTRA-VALUES"));
    Qinfo qinfo = new Qinfo(query_values.get("QUERY-INFO.NAME"), query_values.get("QUERY-INFO.TYPE"));
    Data data = new Data(qinfo, query_values.get("RESPONSE-VALUES"), query_values.get("AUTHORITIES-VALUES"), query_values.get("EXTRA-VALUES"));

    DNSMsg dns_Msg = new DNSMsg(header, data);
    return dns_Msg;

    } 
     
}  
