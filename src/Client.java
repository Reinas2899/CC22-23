import java.io.*;
import java.net.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Client {
    public static void main(String[] args) throws IOException,InterruptedException {
        DatagramSocket socket= new DatagramSocket();
        InetAddress address= InetAddress.getByName("localhost");
        byte[] buf = new byte[1024];
        int connectionFailed=0;

        DNSMsg msg = readquery("3874,Q+R,0,0,0,0;example.com.,MX;");
        buf = msg.getBytes(msg);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445); // controi datagrama
        socket.send(packet); //envia datagrama pelo socket
        DatagramPacket packet2= new DatagramPacket(buf,buf.length);
        StringBuilder stringBuilder = new StringBuilder();
	Thread.sleep(1000);
        System.out.print("A estabelecer conexão com SP");
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
            byte [] data = packet2.getData();
            String a = new String(data,0,packet2.getLength());
            int npackets = Integer.parseInt(a);
            int i=0;
            while(i<npackets){
                DatagramPacket packet3= new DatagramPacket(buf,buf.length,address,port);
                socket.receive(packet3);
                byte [] data3 = new byte[1024];
                data3 = packet3.getData();
                stringBuilder.append(new String(data3,0,packet3.getLength()));
                i++;
            }
            System.out.println(stringBuilder.toString());
        } catch (SocketTimeoutException e) {
            System.out.println("Servidor SP Inativo");
            connectionFailed=1;
        }
        if(connectionFailed==1){
            byte[] buffer = new byte[1024];
            buffer=msg.getBytes(msg);
            DatagramPacket packetSS= new DatagramPacket(buffer,buffer.length,address,4444);
            socket.send(packetSS);
            DatagramPacket packet2SS = new DatagramPacket(buffer,buffer.length,address,4444);
            socket.receive(packet2SS);
            byte[] dados = new byte[1024];
            dados=packet2SS.getData();
            String a = new String(dados,0,packet2SS.getLength());
            System.out.println(a);
        }
        socket.close();
    }

    public static DNSMsg readquery(String query){
        String[] componente=new String[7];
        String [] componente2=new String[2];
        componente = query.split(",");
        componente2 = componente[5].split(";");
        Header header = new Header(componente[0],componente[1]);
        String tipo = componente[6].substring(0,componente[6].length()-1);
        Qinfo qinfo = new Qinfo(componente2[1],tipo);
        Data data = new Data(qinfo);
        return new DNSMsg(header,data);
    }
}
