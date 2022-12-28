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
        InetAddress host = InetAddress.getByName("10.0.4.10");//10.0.4.10
        String ficheiro_conf = args[0];
        String name = ficheiro_conf.split("\\.")[0];
        ParserST parserST = new ParserST(ficheiro_conf);
        ParserST parserST2 = new ParserST("allroot.db");
        ServerSocket serverSocket = new ServerSocket(parserST2.getPort(name));
        Logfile logfile = new Logfile("/var/dns/ST1.log"); 
        Logfile alllogfile = new Logfile("/var/dns/all.log");
        logfile.updateLogFileEV("conf-file-read", LocalDateTime.now(), "EV", ficheiro_conf);
        logfile.updateLogFileEV("root-file-read", LocalDateTime.now(), "EV", "allroot.db");
        while (true) {
	    logfile.updateLogFileST(parserST2.getPort(name), "debug", 4000, LocalDateTime.now(), "ST", host.getHostAddress());
            alllogfile.updateLogFileST(parserST2.getPort(name), "debug", 4000, LocalDateTime.now(), "ST", host.getHostAddress());
            Socket socket = serverSocket.accept(); // necessario saber em que porta em que atua


            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            DNSMsg query = (DNSMsg) ois.readObject();

            System.out.println("QUERY RECEBIDA:");
            System.out.println(query.toString());
	    logfile.updateLogFileQR_QE(query.toString(), LocalDateTime.now(), "QR", host.getHostAddress());

            String domain = query.getData().getQinfo().getName();

            System.out.println("MÁQUINAS A CONTACTAR: " + parserST.getIPnPort(domain));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(parserST.getIPnPort(domain));
	    logfile.updateLogFileRP_RR(parserST.getIPnPort(domain),LocalDateTime.now(),"RP",host.getHostAddress());
        }
    }
}
