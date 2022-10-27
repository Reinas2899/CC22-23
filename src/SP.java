import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;


public class SP {
    public static void main(String[] args) throws IOException, SintaxeIncorretaException, ClassNotFoundException {
        InetAddress address;
        int porta = 4445;
        DatagramSocket socket = new DatagramSocket(porta);
        byte[] buf = new byte[1000];
        boolean running = true;
        LocalDateTime runningNow = LocalDateTime.now();
        String modo = "debug";
        int timeout = 2000;
        ParserConfig parserConfig = new ParserConfig("SP.conf");//parse do conf file
        ParserDB parserDB = new ParserDB("example-com.db");
        String logfilename= parserConfig.getLogfilename();
        Logfile logfile = new Logfile(porta,modo,timeout,runningNow,"ST","",logfilename);
        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);//prepara o datagrama
            socket.receive(packet); //fica à espera de receber

            LocalDateTime receivedNow = LocalDateTime.now();
            logfile.updateLogFile(porta,modo,timeout,receivedNow,"QR","",logfilename);

            address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);


            byte [] data = packet.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            DNSMsg m = (DNSMsg) is.readObject();
            if(m==null){ //perguntar se esta é a linha anterior correspondente à descodificacao
                LocalDateTime errorconvertNow = LocalDateTime.now();
                //updateLogFile(porta,modo,timeout,errorconvertNow,"ER","",parserConfig.logFilename());
            }else {
                System.out.println(m);
                parserDB.respondeQuery(m);
                String a = parserDB.respondeQuery(m);//chamar a funcao
                buf = a.getBytes();
                InetAddress address2 = InetAddress.getByName("localhost");
                DatagramPacket packet2 = new DatagramPacket(buf, buf.length, address2, port);
                socket.send(packet2);
            }
        }
        socket.close();
        LocalDateTime shutdownNow = LocalDateTime.now();
        //updateLogFile(porta,modo,timeout,shutdownNow,"SP","",parserConfig.logFilename());


    }
}

