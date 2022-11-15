    import java.io.*;
    import java.net.*;
    import java.io.IOException;
    import java.net.DatagramPacket;
    import java.net.DatagramSocket;
    import java.net.InetAddress;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;

    public class Client {
        public static void main(String[] args) throws IOException {
                DatagramSocket socket= new DatagramSocket();
                InetAddress address= InetAddress.getByName("localhost");
                byte[] buf = new byte[1024];

                DNSMsg msg = readquery("3874,Q+R,0,0,0,0;example.com.,CNAME;");
                buf = msg.getBytes(msg);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445); // controi datagrama
                socket.send(packet); //envia datagrama pelo socket
                DatagramPacket packet2= new DatagramPacket(buf,buf.length); 
                socket.setSoTimeout(5000);
                
            
                try {
                    socket.receive(packet2);
                    address = packet2.getAddress();
                    int port = packet2.getPort();
                    byte [] data = packet2.getData();
                    String a = new String(data,0,packet2.getLength());
                    System.out.println(a);
                } catch (SocketTimeoutException e) {
                    System.out.println("Servidor Inativo");
                    socket.close();
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
