import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SS {
    public static void main(String[] args) throws IOException, SintaxeIncorretaException, ClassNotFoundException {
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
       
        socket = new Socket(host.getHostName(), 4444);

        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject("ola");
        ois = new ObjectInputStream(socket.getInputStream());
        String m = (String) ois.readObject();
        System.out.println(m);

        socket.close();
       
    }
}
