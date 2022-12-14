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

                SocketSR(args[0]);
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
    public static void SocketSR(String filename) throws IOException, SintaxeIncorretaException, ClassNotFoundException,InterruptedException {
        InetAddress address;
// = InetAddress.getByName("10.0.5.20");
        int porta = 4445;
        ServerSocket serverSocket = new ServerSocket(porta);
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

            Socket socket = serverSocket.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            DNSMsg receivedMessage = (DNSMsg) ois.readObject();
            parserDB.respondeQuery(receivedMessage);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(receivedMessage);

            

        }
                                             



        serverSocket.close();
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
