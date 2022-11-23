import java.util.Arrays;
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
                ServerSocket(args[0]);
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

    public static void ServerSocket(String filename) throws IOException, SintaxeIncorretaException, ClassNotFoundException, InterruptedException {
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        ParserConfig parserConfig = new ParserConfig(filename);
        logFileName = parserConfig.getLogfilename();
        //System.out.println(parserConfig.getWorkingDomain());

        boolean running=true;
        boolean connection=true;
        Logfile logfile = new Logfile(logFileName);
        logfile.updateLogFileEV("log-file-read", LocalDateTime.now(), "EV", "/var/dns/tokyoSS.log");
        logfile.updateLogFileEV("conf-file-read", LocalDateTime.now(), "EV", "tokyoSS.conf");
        LocalDateTime timeRR = LocalDateTime.now();
        int tamanho = 0;

        while(running) {
            logfile.updateLogFileST(4444, "debug", 5000, LocalDateTime.now(), "ST",  "127.0.0.1");
            while(connection) {
                try {
                    socket = new Socket(host.getHostName(), 4444);
                    
                    connection=false;
                } catch (ConnectException e) {
                    System.out.println("Conexão com SP não foi estabelecida. Nova tentativa dentro de 5 segundos.");
                    logfile.updateLogFileTO("conexao-SP",5000, LocalDateTime.now(), "TO", "127.0.0.1");
                    
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
            System.out.println("À espera "+soaretry+"ms...");
            //long start = System.currentTimeMillis();
            //while(System.currentTimeMillis()-soaretry!=start){;}
            Thread.sleep(soaretry);
            if(flag==1){
                allReceived=true;
                System.out.println("Todas as entradas foram recebidas com sucesso.");
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("FIN");
                running=false;
                ParserDB parser = new ParserDB();
                List<ParameterDB> l =parser.getDBLines(new ArrayList<>(entradas.values()));
                dbCopiedLines.addAll(l);

            }
        }
        LocalDateTime timeZT = LocalDateTime.now();
        Duration duracao = Duration.between(timeRR, timeZT); 
        logfile.updateLogFileZT("SS", "4444", duracao.toMillis(), tamanho, timeZT, "ZT");
        socket.close();
    }

    public static void DatagramSocket() throws IOException, SintaxeIncorretaException, ClassNotFoundException, InterruptedException{
        InetAddress address;
        int porta = 4444;
        DatagramSocket socket = new DatagramSocket(porta);
        byte[] buf = new byte[1024];
        boolean running = true;
        Logfile logfile = new Logfile("/var/dns/tokyoSS.log");
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
            logfile.updateLogFileQR_QE(m.toString().replace("\n",""), LocalDateTime.now(), "QR", address.toString());
            if(m==null){ //perguntar se esta é a linha anterior correspondente à descodificacao
                throw new SintaxeIncorretaException("Nao foi possivel descodificar -Sintaxe Incorreta da query.");
            }else if(!verificaSintaxe(m)) {
                throw new SintaxeIncorretaException("Query desconhecida -Sintaxe Incorreta da query.");
            }
            if(allReceived){
                ParserDB parserDB = new ParserDB(dbCopiedLines);

                DNSMsg dnsMsg =  parserDB.respondeQuery(m);
                String dnsMsgString = dnsMsg.toString();
                //System.out.println(dnsMsgString);

		 byte[] dados = dnsMsgString.getBytes();
		 byte[] cipher = null;
                int pac;
                Fragmentation fragmentation = new Fragmentation(dados.length,432);
                int numpacotes = fragmentation.numberofFragments();
                String nPackets = String.valueOf(numpacotes);
                System.out.println("A enviar "+nPackets+" pacotes por fragmentação para o cliente...");
                byte[] n = nPackets.getBytes();
                InetAddress end = InetAddress.getByName("localhost");
                DatagramPacket pacote = new DatagramPacket(n, n.length, end, port);
                socket.send(pacote);
                for(pac=0;pac<numpacotes;pac++){
		        cipher = Arrays.copyOfRange(dados,pac*432,432*(pac+1));
		        DatagramPacket packet2 = new DatagramPacket(cipher, cipher.length, address, port);
		        socket.send(packet2);
                }
            }else{
                byte[] dados = "De momento não é possível responder à query.Tente mais tarde".getBytes();
                DatagramPacket packet2 = new DatagramPacket(dados, dados.length, address, port);
                logfile.updateLogFileRP_RR(new String(dados,0,dados.length), LocalDateTime.now(), "RP", address.toString());
                socket.send(packet2);
            }
            //running=false;

        }
    }
    
    public static boolean verificaSintaxe(DNSMsg dnsMsg){
        boolean r=true;
        ExceptionHandler exceptionHandler=new ExceptionHandler();
        if(!exceptionHandler.isNumeric(dnsMsg.getHeader().getMessageID())) r= false;
        if(!exceptionHandler.isNumeric(dnsMsg.getHeader().getN_authorities())) r= false;
        if(!exceptionHandler.isNumeric(dnsMsg.getHeader().getN_extravalues())) r= false;
        if(!exceptionHandler.isNumeric(dnsMsg.getHeader().getN_values())) r= false;
        if(!exceptionHandler.typeExists(dnsMsg.getData().getQinfo().getType_value())) r= false;
        return r;
    }
}
