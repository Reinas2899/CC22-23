import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ST {
    //args[0] =  ST.conf
    public static void main(String [] args) throws IOException, ClassNotFoundException, SintaxeIncorretaException {
        InetAddress host = InetAddress.getByName("10.0.4.10");
        Socket socket = new Socket(host.getHostName(), 80); // necessario saber em que porta em que atua


        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        DNSMsg query = (DNSMsg) objectInputStream.readObject();

        String domain = query.getData().getQinfo().getName();
        String ficheiro_conf= args[0];
        ParserST parserST = new ParserST(ficheiro_conf);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(parserST.getIPnPort(domain));
    }
}

