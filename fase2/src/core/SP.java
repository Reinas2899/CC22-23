import java.io.*;
import java.net.*;
import java.lang.*;


public class SP {

    public static void main(String[] args) {
        new Thread(()-> {
            try {
                SocketSR(args[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SintaxeIncorretaException e) {
                System.out.println(e.getMessage());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(()-> {
            try {
                SocketSS(args[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SintaxeIncorretaException e) {
                System.out.println(e.getMessage());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public static void SocketSR(String filename) throws IOException, SintaxeIncorretaException, ClassNotFoundException,InterruptedException {
        ParserConfig parserConfig = new ParserConfig(filename);// filename: tokyo.conf
        String ipport = parserConfig.getIPPortSP();
        System.out.println(ipport);
        ParserDB parserDB = new ParserDB(parserConfig.getDbfile());
        int port = Integer.parseInt(ipport.split(":")[1]);
        String address = ipport.split(":")[0];
        ServerSocket serverSocket = new ServerSocket(port);
        boolean running = true;
        byte[] buf = new byte[1024];

        while (running) {
            System.out.println("nova iteracao");
            Socket socket = serverSocket.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            DNSMsg receivedMessage = (DNSMsg) ois.readObject();
            System.out.println("recebi isto:"+receivedMessage);
            DNSMsg m = parserDB.respondeQuery(receivedMessage);
            System.out.println("Eis a resposta:");
            System.out.println(m.toString());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(m);
            if(m.getHeader().getResponse_code().equals(String.valueOf(1))) {
                    String ip="";
                    if(m.getData().getQinfo().getName().contains("osaka")) ip=parserDB.getIPSPsub();
                    System.out.println("IP a enviar "+ip);
                    oos.writeObject(ip);
            }
        }
    }

    public static void SocketSS(String filename) throws IOException, SintaxeIncorretaException, ClassNotFoundException,InterruptedException {
        boolean running = true;
        ParserConfig parserConfig = new ParserConfig(filename);//parse do conf file
        int port = Integer.parseInt(parserConfig.getIPPortSS().split(":")[1])+1;
        ServerSocket server = new ServerSocket(port); // coloquei numa porta diferente pois já temos um processo à escuta na porta 4450
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
