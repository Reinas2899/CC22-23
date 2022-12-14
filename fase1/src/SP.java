import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.lang.*;
import java.util.Arrays;


public class SP {
    
    public static void main(String[] args) throws IOException, SintaxeIncorretaException, ClassNotFoundException {
        new Thread(()-> {
            try {

                DatagramSocket(args[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SintaxeIncorretaException e) {
                System.out.println(e.getMessage());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(()-> {
            try {
                ServerSocket(args[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SintaxeIncorretaException e) {
                System.out.println(e.getMessage());
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
 public static void DatagramSocket(String filename) throws IOException, SintaxeIncorretaException, ClassNotFoundException,InterruptedException {
        InetAddress address;
        int porta = 4445;
        DatagramSocket socket = new DatagramSocket(porta);
        byte[] buf = new byte[1024];
        boolean running = true;
        LocalDateTime runningNow = LocalDateTime.now();
        String modo = "debug";
        int timeout = 2000;
        String packetAdress = "";
        String [] endereco = packetAdress.split("/");
        ParserConfig parserConfig = new ParserConfig(filename);// filename: tokyo.conf 
        ParserDB parserDB = new ParserDB(parserConfig.getDbfile());
        String logFilename= parserConfig.getLogfilename(); 
        Logfile logfile = new Logfile(logFilename);
        logfile.updateLogFileEV("log-file-read", runningNow, "EV", logFilename);
        logfile.updateLogFileEV("conf-file-read", LocalDateTime.now(), "EV", "SP.conf");
        logfile.updateLogFileEV("db-file-read", LocalDateTime.now(), "EV", parserConfig.getDbfile());
        
        
        while (running) {
            
            DatagramPacket packet = new DatagramPacket(buf, buf.length);//prepara o datagrama
            socket.receive(packet); //fica à espera de receber
            packetAdress = packet.getAddress().toString();
            endereco = packetAdress.split("/");
            logfile.updateLogFileST(porta, modo, timeout, LocalDateTime.now(), "ST", endereco[1]);

            LocalDateTime receivedNow = LocalDateTime.now();
            

            address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            endereco = address.toString().split("/");


            byte [] data = packet.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            DNSMsg m = (DNSMsg) is.readObject();
            Thread.sleep(4000);
	    if(m==null){
                LocalDateTime errorconvertNow = LocalDateTime.now();
                logfile.updateLogFileER("Não foi possível a descodificação da query.-Sintaxe Incorreta da query", timeout, errorconvertNow, "ER", endereco[1]);
                throw new SintaxeIncorretaException("Sintaxe Incorreta da query.");
            }else if(!verificaSintaxe(m)) {
                throw new SintaxeIncorretaException("Sintaxe Incorreta da query.");
            }else {
                logfile.updateLogFileQR_QE(m.toString().replace("\n"," "), receivedNow, "QR", endereco[1]);
                //System.out.println(m);
                //parserDB.respondeQuery(m);
                DNSMsg mensagem = parserDB.respondeQuery(m);//chamar a funcao



		String servidormensagem=mensagem.toString();
                byte[] dados = servidormensagem.getBytes();
                
                Fragmentation fragmentation = new Fragmentation(dados.length,432);
                int numpacotes = fragmentation.numberofFragments();
                Thread.sleep(4000);
                System.out.println("A enviar "+numpacotes+" pacotes por fragmentação para o cliente...");
                String nPackets = String.valueOf(numpacotes);
                byte[] n = nPackets.getBytes();
                InetAddress address2 = InetAddress.getByName("localhost");
                DatagramPacket packet2 = new DatagramPacket(n, n.length, address2, port);
                socket.send(packet2);
                int v =0;
                while(v<numpacotes){
                    byte[] fragment = new byte[1024];
                    fragment = Arrays.copyOfRange(dados,432*v,432*(v+1));
                    DatagramPacket packet3 = new DatagramPacket(fragment, fragment.length, address2, port);
                    socket.send(packet3);
                    v++;
                    LocalDateTime sentNow = LocalDateTime.now();
                    String resposta = new String(fragment,0,fragment.length);
                    String [] componente = resposta.split(";");
                    endereco = address2.toString().split("/");

                    logfile.updateLogFileRP_RR(componente[0]+";", sentNow, "RP", endereco[1]);
                }
                                             
            }

        }
        socket.close();
        LocalDateTime shutdownNow = LocalDateTime.now();
        logfile.updateLogFileSP(shutdownNow, "SP", endereco[1], "Servidor Encerrou");

    }
    
    public static void ServerSocket(String filename) throws IOException, ClassNotFoundException, SintaxeIncorretaException, InterruptedException,EOFException {
        int portaSS = 4444;
        ServerSocket server = new ServerSocket(portaSS);
        boolean running = true;
        ParserConfig parserConfig = new ParserConfig(filename);//parse do conf file
        ParserDB parserDB = new ParserDB(parserConfig.getDbfile());
        int soaretryTime = parserDB.getSOARETRY();
        int soarexpireTime = parserDB.getSOAEXPIRE();
        String domain = parserConfig.getWorkingDomain();
        String logFilename= parserConfig.getLogfilename(); 
        Logfile logfile = new Logfile(logFilename);
	int i = 1;
	int n;
	int flag=0;
    	int tamanho = 0;
    	LocalDateTime timeRR = LocalDateTime.now() ;
        while (running) {
            //System.out.println("here we go again");
            Socket socketSS = server.accept();
            ObjectInputStream ois = new ObjectInputStream(socketSS.getInputStream());
            String receivedMessage = (String) ois.readObject();
            if (parserDB.isAutoritiveDomain(receivedMessage)) { // O SS é autoritativo do domínio
                ObjectOutputStream oos = new ObjectOutputStream(socketSS.getOutputStream());
                oos.writeObject(parserDB.getNumberofLines());//envia para o SS o numero de linhas a enviar
                ois = new ObjectInputStream(socketSS.getInputStream());
                receivedMessage = (String) ois.readObject();
                timeRR = LocalDateTime.now();
                logfile.updateLogFileRP_RR(receivedMessage, timeRR, "RR", socketSS.getInetAddress().toString());
                if (receivedMessage.equals("Aceito")) {
                    oos = new ObjectOutputStream(socketSS.getOutputStream());
                    oos.writeObject(soaretryTime);
                    oos.flush();
                    oos = new ObjectOutputStream(socketSS.getOutputStream());
                    oos.writeObject(soarexpireTime);
                    

                    oos.flush();
                    final SocketConnected socketConnected = new SocketConnected();
		    Thread thread = new Thread(){
                        public void run() {
                            try {
                                ObjectInputStream objectInputStream = new ObjectInputStream(socketSS.getInputStream());
                                String mensagem = (String) objectInputStream.readObject();
                                if (mensagem.equals("FIN")) socketConnected.setIsClosed(true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                    try{
			for (n=0;n<parserDB.getFileLines().size();n++) {
                            if(socketConnected.isClosed){
				    flag=2;
				    break;
			    }
                           oos = new ObjectOutputStream(socketSS.getOutputStream());
			    oos.writeObject(n+1 + " " + parserDB.getFileLines().get(n));
	                   tamanho += oos.toString().getBytes().length;
			    oos.flush();
                            //if(n==4) Thread.sleep(8000);
                        }
                    }catch(SocketException e){
                        System.out.println("A conexão TCP foi terminada.");
                        socketSS.close();
                        running=false;
                    }
                    oos.flush();
                }
            } else {
                ObjectOutputStream oos = new ObjectOutputStream(socketSS.getOutputStream());
                oos.writeObject("Não te aceito.");
            }
        	if(flag!=2) running=false;
	}
        LocalDateTime finalZT = LocalDateTime.now();
        Duration duracao = Duration.between(timeRR, finalZT);

        logfile.updateLogFileZT("SP", "4444", duracao.toMillis(), tamanho, finalZT, "ZT");
        System.out.println("Acabou o processo de zona de transferência.");
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
