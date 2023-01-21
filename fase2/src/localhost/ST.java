import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class ST {
    //args[0] =  tokyoST1.conf
    public static void main(String [] args) throws IOException, ClassNotFoundException, SintaxeIncorretaException {
        InetAddress host = InetAddress.getByName("localhost");//10.0.4.10
        String ficheiro_conf = args[0];
        String name = ficheiro_conf.split("\\.")[0];
        ParserConfig pconf = new ParserConfig(ficheiro_conf);
        String root = pconf.getroot();
        System.out.println(root);
        ParserST parserST = new ParserST(ficheiro_conf);
        ParserST parserST2 = new ParserST(root);
        System.out.println(name);
        String foundString = "";

        if (name.contains("ST1")) {
            int index = name.indexOf("ST1");
            foundString = name.substring(index, index + "ST1".length());
            System.out.println(foundString);
        }

        if (name.contains("ST2")) {
            int index = name.indexOf("ST2");
            foundString = name.substring(index, index + "ST2".length());
            System.out.println(foundString);
        }
        ServerSocket serverSocket = new ServerSocket(parserST2.getPort(foundString));
        String logFilename= pconf.getLogfilename();
        pconf.alllogFilename();
        String allLog = pconf.getAllLogfile();
        Logfile logfile = new Logfile(logFilename);
        Logfile alllogfile = new Logfile(allLog);
        logfile.updateLogFileEV("log-file-read", LocalDateTime.now(), "EV", logFilename);
        logfile.updateLogFileEV("conf-file-read", LocalDateTime.now(), "EV", ficheiro_conf);
        logfile.updateLogFileEV("root-file-read", LocalDateTime.now(), "EV", root);

        while (true) {
            logfile.updateLogFileST(parserST2.getPort(name), "debug", 4000, LocalDateTime.now(), "ST", host.toString());
            alllogfile.updateLogFileST(parserST2.getPort(name), "debug", 4000, LocalDateTime.now(), "ST", host.toString());
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

