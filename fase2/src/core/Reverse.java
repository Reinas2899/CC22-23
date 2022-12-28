import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Reverse {
    public static void main(String [] args) throws IOException, SintaxeIncorretaException, ClassNotFoundException {
        ParserRev parserRev = new ParserRev("reverse.db");
        ServerSocket serverSocket = new ServerSocket(4477);
        boolean running = true;
        byte[] buf = new byte[1024];

        while (running) {
            System.out.println("nova iteracao");
            Socket socket = serverSocket.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            DNSMsg receivedMessage = (DNSMsg) ois.readObject();
            System.out.println("recebi isto:"+receivedMessage);
            DNSMsg m = parserRev.arpa(receivedMessage);
            System.out.println("Eis a resposta:");
            System.out.println(m.toString());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(m);
        }
    }
}
