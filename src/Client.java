import java.io.IOException;
import java.net.*;
public class Client {
    public static void main(String[] args) throws IOException {
         DatagramSocket socket= new DatagramSocket();;
         InetAddress address= InetAddress.getByName("localhost");
         byte[] buf;

         String msg = "Olá servidor, eu sou o cliente.";

        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445); // controi datagrama
        socket.send(packet); //envia datagrama pelo socket
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
    }
}  
