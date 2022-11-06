import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.lang.*;

public class SP {
    public static void main(String[] args) throws IOException, SintaxeIncorretaException, ClassNotFoundException {
        new Thread(()-> {
            try {
                DatagramSocket();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SintaxeIncorretaException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(()-> {
            try {
                ServerSocket();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();

    }
    public static void DatagramSocket() throws IOException, SintaxeIncorretaException, ClassNotFoundException {
        InetAddress address;
        int porta = 4445;
        DatagramSocket socket = new DatagramSocket(porta);
        byte[] buf = new byte[1024];
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
            logfile.updateLogFileQR_QE("", "dados a inserir", receivedNow, "QR", logfilename, "localhost");

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
                //System.out.println(m);
                parserDB.respondeQuery(m);
                DNSMsg mensagem = parserDB.respondeQuery(m);//chamar a funcao
                if(mensagem.getHeader().getFlags().equals("Q+R")) mensagem.getHeader().setFlags("R+A");
                System.out.println(mensagem);
                //buf = mensagem.getBytes(mensagem);

                String servidormensagem = mensagem.toString();

                byte[] dados = servidormensagem.getBytes();
                InetAddress address2 = InetAddress.getByName("localhost");
                DatagramPacket packet2 = new DatagramPacket(dados, dados.length, address2, port);
                socket.send(packet2);
            }

        }
        socket.close();
        LocalDateTime shutdownNow = LocalDateTime.now();
        logfile.updateLogFileSP(shutdownNow, "SP", logfilename, "localhost", "razao qualquer");

    }
    public static void ServerSocket() throws IOException, ClassNotFoundException {
        int portaSS = 4444;
        ServerSocket server = new ServerSocket(portaSS);
        boolean running = true;

        Socket socketSS = server.accept();
        ObjectInputStream ois = new ObjectInputStream(socketSS.getInputStream());
        String receivedMessage= (String) ois.readObject();
        ObjectOutputStream oos = new ObjectOutputStream(socketSS.getOutputStream());
        oos.writeObject(receivedMessage+"para ti também");
       
    }
}
