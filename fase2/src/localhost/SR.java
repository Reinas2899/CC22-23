import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SR {
    private static Map<String, DNSMsg> cache = new HashMap<>();
    public static void main(String[] args) throws IOException, ClassNotFoundException, SintaxeIncorretaException, InterruptedException {
        //receber query
        //verifica na cache
        //caso não tenha vai ao ST

        //String conf = args[0];

        InetAddress address;
        int porta = 4445;
        byte[] buf = new byte[1024];
        DatagramSocket socket = new DatagramSocket(porta);
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            address = packet.getAddress();
            int port = packet.getPort();


            byte [] data = packet.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            DNSMsg m = (DNSMsg) is.readObject();

            //verifica se tem resposta à query na cache
            //se nao tiver nada em DD
            System.out.println(m.toString());
            String header = m.getData().getQinfo().getName()+m.getData().getQinfo().getType_value();

            System.out.println("A consultar cache...");
            if(!cache.containsKey(header)) {
                ParserST parserST = new ParserST("allroot");//0:SR.conf 1:allroot
                InetAddress host = InetAddress.getByName(parserST.getFirsSTAdress().split(":")[0]);
                System.out.println("A contactar " + host.getHostName());
                Thread.sleep(2000);
                Socket socketST = new Socket(host.getHostName(), Integer.parseInt(parserST.getFirsSTAdress().split(":")[1]));

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketST.getOutputStream());
                objectOutputStream.writeObject(m);

                ObjectInputStream objectInputStream = new ObjectInputStream(socketST.getInputStream());
                String allComponentes = (String) objectInputStream.readObject();

                String[] components = allComponentes.split(";");

                int i = 0;
                int resp_code = 2;
                DNSMsg response = null;
                Socket socketcomp = null;
                ObjectOutputStream oos = null;
                ObjectInputStream ois = null;
                while (i < 2) {
                    int failed = 0;
                    String[] params = components[i].split(",");
                    InetAddress comp = InetAddress.getByName(params[2].split(":")[0]);
                    System.out.println("a contactar IP:" + comp.getHostName() + " PORT:" + Integer.parseInt(params[2].split(":")[1]));
                    try {
                        socketcomp = new Socket(comp.getHostName(), Integer.parseInt(params[2].split(":")[1]));
                    } catch (ConnectException e) {
                        failed = 1;
                    }
                    if (failed == 0) {
                        oos = new ObjectOutputStream(socketcomp.getOutputStream());
                        oos.writeObject(m);
                        ois = new ObjectInputStream(socketcomp.getInputStream());
                        response = (DNSMsg) ois.readObject();
                        resp_code = Integer.parseInt(response.getHeader().getResponse_code());
                        if (resp_code == 0 || resp_code == 1) break;
                        i++;
                    }
                    if (failed == 1) i++;
                }

                if (resp_code == 0) {
                    byte[] dados = response.toString().getBytes();
                    cache.put(header, response);

                    Fragmentation fragmentation = new Fragmentation(dados.length, 432);
                    int numpacotes = fragmentation.numberofFragments();
                    System.out.println("A enviar " + numpacotes + " pacotes por fragmentação para o cliente...");
                    String nPackets = String.valueOf(numpacotes);
                    byte[] n = nPackets.getBytes();
                    DatagramPacket packet2 = new DatagramPacket(n, n.length, address, port);
                    socket.send(packet2);
                    int v = 0;
                    while (v < numpacotes) {
                        byte[] fragment = new byte[1024];
                        fragment = Arrays.copyOfRange(dados, 432 * v, 432 * (v + 1));
                        DatagramPacket packet3 = new DatagramPacket(fragment, fragment.length, address, port);
                        socket.send(packet3);
                        v++;
                    }
                } else if (resp_code == 1) {
                    oos.reset();
                    //oos.writeObject(port);
                    String ipport = (String) ois.readObject();
                    System.out.println("recebido:" + ipport);

                    InetAddress host3 = InetAddress.getByName(ipport.split(":")[0]);
                    Socket socketSP2 = new Socket(host3.getHostName(), Integer.parseInt(ipport.split(":")[1]));

                    objectOutputStream = new ObjectOutputStream(socketSP2.getOutputStream());
                    System.out.println(m);
                    objectOutputStream.writeObject(m);

                    objectInputStream = new ObjectInputStream(socketSP2.getInputStream()); //get query response
                    response = (DNSMsg) objectInputStream.readObject();
                    System.out.println(response);

                    byte[] dados = response.toString().getBytes();

                    Fragmentation fragmentation = new Fragmentation(dados.length, 432);
                    int numpacotes = fragmentation.numberofFragments();
                    System.out.println("A enviar " + numpacotes + " pacotes por fragmentação para o cliente...");
                    String nPackets = String.valueOf(numpacotes);
                    byte[] n = nPackets.getBytes();
                    DatagramPacket packet2 = new DatagramPacket(n, n.length, address, port);
                    socket.send(packet2);
                    int v = 0;
                    while (v < numpacotes) {
                        byte[] fragment = new byte[1024];
                        fragment = Arrays.copyOfRange(dados, 432 * v, 432 * (v + 1));
                        DatagramPacket packet3 = new DatagramPacket(fragment, fragment.length, address, port);
                        socket.send(packet3);
                        v++;
                    }
                }

            }else{
                System.out.println("Resposta encontrada na cache");
                DNSMsg resposta = cache.get(header);
                byte[] dados = resposta.toString().getBytes();

                Fragmentation fragmentation = new Fragmentation(dados.length, 432);
                int numpacotes = fragmentation.numberofFragments();
                System.out.println("A enviar " + numpacotes + " pacotes por fragmentação para o cliente...");
                String nPackets = String.valueOf(numpacotes);
                byte[] n = nPackets.getBytes();
                DatagramPacket packet2 = new DatagramPacket(n, n.length, address, port);
                socket.send(packet2);
                int v = 0;
                while (v < numpacotes) {
                    byte[] fragment = new byte[1024];
                    fragment = Arrays.copyOfRange(dados, 432 * v, 432 * (v + 1));
                    DatagramPacket packet3 = new DatagramPacket(fragment, fragment.length, address, port);
                    socket.send(packet3);
                    v++;
                }
            }
        } //fim do while
    }

}
