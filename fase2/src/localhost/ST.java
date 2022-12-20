import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class ST {
    //args[0] =  ST1.conf
    public static void main(String [] args) throws IOException, ClassNotFoundException, SintaxeIncorretaException {
        InetAddress host = InetAddress.getByName("localhost");//10.0.4.10
        String ficheiro_conf = args[0];
        String name = ficheiro_conf.split("\\.")[0];
        ParserST parserST = new ParserST(ficheiro_conf);
        ParserST parserST2 = new ParserST("allroot");
        ServerSocket serverSocket = new ServerSocket(parserST2.getPort(name));
        String logFilename= ficheiro_conf+".log";
        Logfile logfile = new Logfile(logFilename);
        logfile.updateLogFileEV("log-file-read", LocalDateTime.now(), "EV", logFilename);
        logfile.updateLogFileEV("conf-file-read", LocalDateTime.now(), "EV", ficheiro_conf);
        logfile.updateLogFileEV("root-file-read", LocalDateTime.now(), "EV", "allroot");

        while (true) {
            logfile.updateLogFileST(parserST2.getPort(name), "debug", 4000, LocalDateTime.now(), "ST", host.toString());
            Socket socket = serverSocket.accept(); // necessario saber em que porta em que atua


            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            DNSMsg query = (DNSMsg) ois.readObject();

            System.out.println("QUERY RECEBIDA:");
            System.out.println(query.toString());
            logfile.updateLogFileQR_QE(query.toString(), LocalDateTime.now(), "QR", host.toString());

            String domain = query.getData().getQinfo().getName();

            System.out.println("MÁQUINAS A CONTACTAR: " + parserST.getIPnPort(domain));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(parserST.getIPnPort(domain));
            logfile.updateLogFileRP_RR(parserST.getIPnPort(domain),LocalDateTime.now(),"RP",host.toString());
        }
    }
}

