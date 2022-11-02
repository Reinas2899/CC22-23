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
        while (true) {
            socket = new Socket(host.getHostName(), 4444);
            socket.setSoTimeout(5000);
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("joao.example.com");//qual é o dominio?
                ois = new ObjectInputStream(socket.getInputStream());
                String m = (String) ois.readObject();
                if(Integer.parseInt(m)<65535) System.out.println(m);
            } catch (SocketTimeoutException e ) {
                System.out.println("Servidor Primário inativo.");
                socket.close();
            }
        }
    }
}
