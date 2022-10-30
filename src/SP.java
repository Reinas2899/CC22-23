import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;


public class SP {
    public static void main(String[] args) throws IOException, SintaxeIncorretaException, ClassNotFoundException {
        InetAddress address;
        int porta = 4445;
        int portaSS = 4444;
        ServerSocket server = new ServerSocket(portaSS);


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
                System.out.println(a);
                buf = a.getBytes();
                InetAddress address2 = InetAddress.getByName("localhost");
                DatagramPacket packet2 = new DatagramPacket(buf, buf.length, address2, port);
                socket.send(packet2);
            }
            /* LÊ O SOCKET DO SS
            Socket socketSS = server.accept();
            ObjectInputStream ois = new ObjectInputStream(socketSS.getInputStream());
            if(((String) ois.readObject()).contains(parserDB.serverDomain())){
                ObjectOutputStream oos = new ObjectOutputStream(socketSS.getOutputStream());
                int NofLines = parserDB.getNumberofLines();
                oos.writeObject(NofLines);
            }*/
        }
        socket.close();
        LocalDateTime shutdownNow = LocalDateTime.now();
        //updateLogFile(porta,modo,timeout,shutdownNow,"SP","",parserConfig.logFilename());


    }
}

