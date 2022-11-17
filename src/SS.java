import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SS {
    static Map<Integer, String> entradas = new HashMap<>();
    static boolean allReceived=false;

    public static void main(String[] args){
        Thread thread1 = new Thread(() -> {
            try {
                ServerSocket();
            } catch (IOException | ClassNotFoundException | SintaxeIncorretaException | InterruptedException e){
                //System.out.println(e.getMessage());
                e.printStackTrace();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                DatagramSocket();
            } catch (IOException | ClassNotFoundException | SintaxeIncorretaException | InterruptedException e){
                System.out.println(e.getMessage());
            }
        });
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        thread1.start();
        thread2.start();

    }

    public static void ServerSocket() throws IOException, SintaxeIncorretaException, ClassNotFoundException, InterruptedException {
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        ParserConfig parserConfig = new ParserConfig("SS.conf");
        //System.out.println(parserConfig.getWorkingDomain());
        boolean running=true;
        boolean connection=true;
        Logfile logfile = new Logfile("/var/dns/SS.log");
        logfile.updateLogFileEV("log-file-created", LocalDateTime.now(), "EV", "/var/dns/SS.log");
        logfile.updateLogFileEV("conf-file-read", LocalDateTime.now(), "EV", "SS.conf");
        LocalDateTime timeRR = LocalDateTime.now();
        int tamanho = 0;

        while(running) {
            logfile.updateLogFileST(4444, "debug", 5000, LocalDateTime.now(), "ST",  host.toString());
            while(connection) {
                try {
                    socket = new Socket(host.getHostName(), 4444);
                    
                    connection=false;
                } catch (ConnectException e) {
                    System.out.println("Conexão com SP não foi estabelecida. Nova tentativa dentro de 5 segundos.");
                    logfile.updateLogFileTO("conexão-SP",5000, LocalDateTime.now(), "TO", host.toString());
                    
                }
                Thread.sleep(5000);
            }


            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(parserConfig.getWorkingDomain());//SS envia o domínio em que trabalha
            ois = new ObjectInputStream(socket.getInputStream());
            int i = (Integer) ois.readObject();
            int n = 0;
            int soaretry = 0;
            int soarexpire =0;
            int flag =0;
            if (i < 65535) {
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("Aceito");//SS aceita receber o n de linhas
                timeRR = LocalDateTime.now();
                ois = new ObjectInputStream(socket.getInputStream());
                soaretry = (Integer) ois.readObject();
                tamanho += ois.toString().getBytes().length;
                ois = new ObjectInputStream(socket.getInputStream());
                soarexpire = (Integer) ois.readObject();
                tamanho += ois.toString().getBytes().length;
                socket.setSoTimeout(soarexpire);
                try {
                    while (n < i) {
                        ois = new ObjectInputStream(socket.getInputStream());
                        String m = (String) ois.readObject();
                        String[] aux = m.split(" ", 2);
                        if(entradas.containsKey(Integer.parseInt(aux[0]))) {
                            entradas.replace(Integer.parseInt(aux[0]), aux[1]);
                        }else entradas.put(Integer.parseInt(aux[0]),aux[1]);
                        System.out.println(m);
                        n++;
                    }
                    if(n==i) flag=1;
                } catch (SocketTimeoutException e) {
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject("FIN");
                    flag=2;
                }
            } else {
                System.out.println("Não aceito receber essa quantidade de entradas.");
                logfile.updateLogFileSP(LocalDateTime.now(), "SP", socket.getInetAddress().toString(), "Numero de entradas demasiado grande");
                socket.close();
            }
            oos.flush();
            System.out.println("Vou esperar soaretry time");
            //long start = System.currentTimeMillis();
            //while(System.currentTimeMillis()-soaretry!=start){;}
            Thread.sleep(soaretry);
            if(flag==1){
                allReceived=true;
                System.out.println("Recebi todas-server");
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("FIN");
                running=false;
            }
        }
        LocalDateTime timeZT = LocalDateTime.now();
        Duration duracao = Duration.between(timeRR, timeZT); 
        logfile.updateLogFileZT("SS", "", duracao.toMillis(), tamanho, timeZT, "ZT");
        socket.close();
    }

    public static void DatagramSocket() throws IOException, SintaxeIncorretaException, ClassNotFoundException, InterruptedException{
        InetAddress address;
        int porta = 4444;
        DatagramSocket socket = new DatagramSocket(porta);
        byte[] buf = new byte[1024];
        boolean running = true;
        Logfile logfile = new Logfile("/var/dns/SS.log");
        while (running){
            DatagramPacket packet = new DatagramPacket(buf, buf.length);//prepara o datagrama
            socket.receive(packet); //fica à espera de receber
            address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);

            byte [] data = packet.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            DNSMsg m = (DNSMsg) is.readObject();
            System.out.println(m.toString());
            logfile.updateLogFileQR_QE(m.toString(), LocalDateTime.now(), "QR", address.toString());
            if(allReceived){
                byte[] dados = "sortudo, eu tenho copia".getBytes();
                DatagramPacket packet2 = new DatagramPacket(dados, dados.length, address, port);
                logfile.updateLogFileRP_RR(new String(dados,0,dados.length), LocalDateTime.now(), "RP", address.toString());
                socket.send(packet2);
            }else{
                byte[] dados = "pouca sorte, nao tenho copia".getBytes();
                DatagramPacket packet2 = new DatagramPacket(dados, dados.length, address, port);
                logfile.updateLogFileRP_RR(new String(dados,0,dados.length), LocalDateTime.now(), "RP", address.toString());
                socket.send(packet2);
            }
            //running=false;

        }



    }
}
