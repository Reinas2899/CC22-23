import java.net.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;


public class Client {
    public static void main(String[] args) throws IOException,InterruptedException {

        int messageID = 1;
        while (true) {
            System.out.print("\033[32mCliente> ");
            Scanner scanner = new Scanner(System.in);

            String type = scanner.nextLine();
            if (type.equals("")) continue;
            String argumentos[] = type.split(" ");
            if (argumentos.length != 4) continue;
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(argumentos[0]);//argumento[0]
            byte[] buf = new byte[1024];
            int connectionFailed = 0;

            String query = messageID + "," + argumentos[3] + ",0,0,0,0;" + argumentos[1] + "," + argumentos[2] + ";";
            messageID++;
            System.out.println(query);


            DNSMsg msg = null;
            try {
                msg = readquery(query);
            } catch (SintaxeIncorretaException s) {
                System.out.println(s.getMessage());
            }
            try {
                buf = msg.getBytes(msg);
            } catch (NullPointerException n) {
                return;
            }
            String[] endereco = address.toString().split("/");

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445); // controi datagrama

            socket.send(packet); //envia datagrama pelo socket
            DatagramPacket packet2 = new DatagramPacket(buf, buf.length);
            StringBuilder stringBuilder = new StringBuilder();
            Thread.sleep(1000);
            System.out.print("A estabelecer conexão com SR");
            Thread.sleep(1000);
            System.out.print(".");
            Thread.sleep(1000);
            System.out.print(".");
            Thread.sleep(1000);
            System.out.print(".");
            Thread.sleep(1000);
            System.out.print("\n");
            socket.setSoTimeout(5000);


            try {
                socket.receive(packet2);
                address = packet2.getAddress();
                int port = packet2.getPort();
                byte[] data = packet2.getData();
                String a = new String(data, 0, packet2.getLength());
                int npackets = Integer.parseInt(a);
                endereco = address.toString().split("/");
		InetAddress address2 = InetAddress.getByName(address.toString().split("/")[1]);
                int i = 0;
                while (i < npackets) {
                    DatagramPacket packet3 = new DatagramPacket(buf, buf.length, address2, port);
                    socket.receive(packet3);
                    byte[] data3 = new byte[1024];
                    data3 = packet3.getData();
                    stringBuilder.append(new String(data3, 0, packet3.getLength()));
                    i++;
                }
                System.out.println(stringBuilder.toString());
            } catch (SocketTimeoutException e) {
                System.out.println("Conexão com SR falhou.");
            }
            socket.close();
        }
    }


    public static DNSMsg readquery(String query) throws SintaxeIncorretaException{
        String[] componente=new String[7];
        String [] componente2=new String[2];
        componente = query.split(",");
        componente2 = componente[5].split(";");
        if(componente.length!=7 || componente2.length!=2) throw new SintaxeIncorretaException("Campos a mais na query.-Sintaxe Incorreta.");
        Header header = new Header(componente[0],componente[1]);
        String tipo = componente[6].substring(0,componente[6].length()-1);
        Qinfo qinfo = new Qinfo(componente2[1],tipo);
        Data data = new Data(qinfo);
        return new DNSMsg(header,data);
    }

}
