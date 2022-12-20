import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ST {
    //args[0] =  ST1.conf
    public static void main(String [] args) throws IOException, ClassNotFoundException, SintaxeIncorretaException {
        InetAddress host = InetAddress.getByName("10.0.4.10");//10.0.4.10
        String ficheiro_conf = args[0];
        String name = ficheiro_conf.split("\\.")[0];
        ParserST parserST = new ParserST(ficheiro_conf);
        ParserST parserST2 = new ParserST("allroot");
        ServerSocket serverSocket = new ServerSocket(parserST2.getPort(name));

        while (true) {
            Socket socket = serverSocket.accept(); // necessario saber em que porta em que atua


            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            DNSMsg query = (DNSMsg) ois.readObject();

            System.out.println("QUERY RECEBIDA:");
            System.out.println(query.toString());

            String domain = query.getData().getQinfo().getName();

            System.out.println("MÁQUINAS A CONTACTAR: " + parserST.getIPnPort(domain));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(parserST.getIPnPort(domain));
        }
    }
}

