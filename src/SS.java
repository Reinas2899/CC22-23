import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

public class SS {
    static Map<Integer,String> entradas = new HashMap<>();
    public static void main(String[] args) throws IOException, SintaxeIncorretaException, ClassNotFoundException, InterruptedException {
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        ParserConfig parserConfig = new ParserConfig("SS.conf");
        System.out.println(parserConfig.getWorkingDomain());
        boolean running=true;

        while(running) {
            socket = new Socket(host.getHostName(), 4444);

            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(parserConfig.getWorkingDomain());//SS envia o domínio em que trabalha
            ois = new ObjectInputStream(socket.getInputStream());
            int i = (Integer) ois.readObject();
            int n = 0;
            int soaretry = 0;
            int soarexpire =0;
            if (i < 65535) {
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("Aceito");//SS aceita receber o n de linhas
                ois = new ObjectInputStream(socket.getInputStream());
                soaretry = (Integer) ois.readObject();
                ois = new ObjectInputStream(socket.getInputStream());
                soarexpire = (Integer) ois.readObject();
                System.out.println(soaretry);
                System.out.println(soarexpire);
                socket.setSoTimeout(soarexpire);
                try {
                    while (n < i) {
                        ois = new ObjectInputStream(socket.getInputStream());
                        String m = (String) ois.readObject();
                        String[] aux = m.split(" ", 2);
                        entradas.put(Integer.parseInt(aux[0]), aux[1]);
                        System.out.println(m);
                        n++;
                    }
                } catch (SocketTimeoutException e) {
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject("FIN");
                }
            } else {
                System.out.println("Não aceito receber essa quantidade de entradas.");
                socket.close();
            }
            socket.close();
            oos.flush();
            System.out.println("Vou esperar soaretry time");
            long start = System.currentTimeMillis();
            while(System.currentTimeMillis()-soaretry!=start){;}
            running=false;
        }
        System.out.println("Acabei de receber todas as entradas.");
    }
}
