import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SS {
    static Map<Integer, String> entradas = new HashMap<>();
    static List<ParameterDB> dbCopiedLines = new ArrayList<>();
    static boolean allReceived=false;
    static String logFileName;

    public static void main(String[] args){
        Thread thread1 = new Thread(() -> {
            try {
                ServerSocketSP(args[0]);
            } catch (IOException | ClassNotFoundException | SintaxeIncorretaException | InterruptedException e){
                //System.out.println(e.getMessage());
                e.printStackTrace();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                ServerSocketSR(args[0]);
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

    public static void ServerSocketSP(String filename) throws IOException, SintaxeIncorretaException, ClassNotFoundException, InterruptedException {
        ParserConfig parserConfig = new ParserConfig(filename);
        InetAddress host = InetAddress.getByName(parserConfig.getIPPortSP().split(":")[0]);
        int port = Integer.parseInt(parserConfig.getIPPortSS().split(":")[1])+1;
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        logFileName = parserConfig.getLogfilename();
        //System.out.println(parserConfig.getWorkingDomain());

        boolean running=true;
        boolean connection=true;
        Logfile logfile = new Logfile(logFileName);
        logfile.updateLogFileEV("log-file-read", LocalDateTime.now(), "EV", "/var/dns/"+logFileName);
        logfile.updateLogFileEV("conf-file-read", LocalDateTime.now(), "EV", filename);
        LocalDateTime timeRR = LocalDateTime.now();
        int tamanho = 0;

        while(running) {
            logfile.updateLogFileST(port, "debug", 5000, LocalDateTime.now(), "ST",  host.toString());
            while(connection) {
                try {
                    socket = new Socket(host.getHostName(), port);

                    connection=false;
                } catch (ConnectException e) {
                    System.out.println("Conexão com SP não foi estabelecida. Nova tentativa dentro de 5 segundos.");
                    logfile.updateLogFileTO("conexao-SP",5000, LocalDateTime.now(), "TO", host.toString());

                }
                Thread.sleep(5000);
            }


            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(parserConfig.getWorkingDomain());//SS envia o domínio em que trabalha
            logfile.updateLogFileQR_QE(parserConfig.getWorkingDomain(), LocalDateTime.now(), "QE", socket.getInetAddress().toString());
            ois = new ObjectInputStream(socket.getInputStream());
            int i = (Integer) ois.readObject();
            logfile.updateLogFileRP_RR(String.valueOf(i),LocalDateTime.now(),"RR",socket.getInetAddress().toString());
            int n = 0;
            int soaretry = 0;
            int soarexpire =0;
            int flag =0;
            if (i < 65535) {
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("Aceito");//SS aceita receber o n de linhas
                logfile.updateLogFileRP_RR("Aceito",LocalDateTime.now(),"RP",socket.getInetAddress().toString());
                timeRR = LocalDateTime.now();
                ois = new ObjectInputStream(socket.getInputStream());
                soaretry = (Integer) ois.readObject();
                tamanho += ois.toString().getBytes().length;
                logfile.updateLogFileRP_RR(String.valueOf(soaretry),LocalDateTime.now(),"RR",socket.getInetAddress().toString());
                ois = new ObjectInputStream(socket.getInputStream());
                soarexpire = (Integer) ois.readObject();
                tamanho += ois.toString().getBytes().length;
                socket.setSoTimeout(soarexpire);
                try {
                    while (n < i) {
                        ois = new ObjectInputStream(socket.getInputStream());
                        String m = (String) ois.readObject();
                        logfile.updateLogFileRP_RR(m,LocalDateTime.now(),"RR",socket.getInetAddress().toString());
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
                    logfile.updateLogFileRP_RR("FIN",LocalDateTime.now(),"RP",socket.getInetAddress().toString());
                    flag=2;
                }
            } else {
                System.out.println("Não aceito receber essa quantidade de entradas.");
                logfile.updateLogFileSP(LocalDateTime.now(), "SP", socket.getInetAddress().toString(), "Numero de entradas demasiado grande");
                socket.close();
            }
            oos.flush();
            System.out.println("À espera "+soaretry+"ms...");
            //long start = System.currentTimeMillis();
            //while(System.currentTimeMillis()-soaretry!=start){;}
            Thread.sleep(soaretry);
            if(flag==1){
                allReceived=true;
                System.out.println("Todas as entradas foram recebidas com sucesso.");
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("FIN");
                logfile.updateLogFileRP_RR("FIN",LocalDateTime.now(),"RP",socket.getInetAddress().toString());
                running=false;
                ParserDB parser = new ParserDB();
                List<ParameterDB> l =parser.getDBLines(new ArrayList<>(entradas.values()));
                dbCopiedLines.addAll(l);

            }
            else connection=true;
        }
        LocalDateTime timeZT = LocalDateTime.now();
        Duration duracao = Duration.between(timeRR, timeZT);
        logfile.updateLogFileZT("SS", String.valueOf(port), duracao.toMillis(), tamanho, timeZT, "ZT");
        socket.close();
    }

    public static void ServerSocketSR(String filename) throws IOException, SintaxeIncorretaException, ClassNotFoundException, InterruptedException {
        ParserConfig parserConfig = new ParserConfig(filename);// filename: tokyo.conf
        String ipport = parserConfig.getIPPortSS();
        int port = Integer.parseInt(ipport.split(":")[1]);
        String address = ipport.split(":")[0];
        ServerSocket serverSocket = new ServerSocket(port);
        boolean running = true;
        byte[] buf = new byte[1024];
        Logfile logfile = new Logfile(logFileName);
        logfile.updateLogFileEV("log-file-read", LocalDateTime.now(), "EV", "/var/dns/"+logFileName);
        logfile.updateLogFileEV("conf-file-read", LocalDateTime.now(), "EV", filename);

        while (running) {
            logfile.updateLogFileST(port, "debug", 5000, LocalDateTime.now(), "ST", address);
            System.out.println("nova iteracao");
            Socket socket = serverSocket.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            DNSMsg receivedMessage = (DNSMsg) ois.readObject();
            logfile.updateLogFileQR_QE(receivedMessage.toString(), LocalDateTime.now(), "QR", address);
            System.out.println("recebi isto:"+receivedMessage);
            ParserDB parserDB = new ParserDB(dbCopiedLines);
            DNSMsg m = parserDB.respondeQuery(receivedMessage);
            System.out.println("Eis a resposta:");
            System.out.println(m.toString());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            logfile.updateLogFileRP_RR(m.toString(),LocalDateTime.now(),"RP",address);
            oos.writeObject(m);
            if(m.getHeader().getResponse_code().equals(String.valueOf(1))) {
                String ip="";
                if(m.getData().getQinfo().getName().contains("osaka")) ip=parserDB.getIPSPsub();
                System.out.println("IP a enviar "+ip);
                oos.writeObject(ip);
                logfile.updateLogFileRP_RR(ip,LocalDateTime.now(),"RP",address);
            }
        }
    }
}
