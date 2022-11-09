import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.lang.*;

public class SP {

    
    public static void main(String[] args) throws IOException, SintaxeIncorretaException, ClassNotFoundException {
        new Thread(()-> {
            try {
                DatagramSocket();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SintaxeIncorretaException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(()-> {
            try {
                ServerSocket();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SintaxeIncorretaException e) {
                e.printStackTrace();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
    public static void DatagramSocket() throws IOException, SintaxeIncorretaException, ClassNotFoundException {
        InetAddress address;
        int porta = 4445;
        DatagramSocket socket = new DatagramSocket(porta);
        byte[] buf = new byte[1024];
        boolean running = true;
        LocalDateTime runningNow = LocalDateTime.now();
        String modo = "debug";
        int timeout = 2000;
        String packetAdress = "";
        ParserConfig parserConfig = new ParserConfig("SP.conf");//parse do conf file
        ParserDB parserDB = new ParserDB(parserConfig.getDbfile());
        String logFilename= parserConfig.getLogfilename(); 
        Logfile logfile = new Logfile(logFilename);
        
        
        while (running) {

            DatagramPacket packet = new DatagramPacket(buf, buf.length);//prepara o datagrama
            socket.receive(packet); //fica à espera de receber
            packetAdress = packet.getAddress().toString();
            System.out.println(packetAdress);


            LocalDateTime receivedNow = LocalDateTime.now();
            logfile.updateLogFileQR_QE("dados a inserir", receivedNow, "QR", packetAdress);

            address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);


            byte [] data = packet.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            DNSMsg m = (DNSMsg) is.readObject();
            if(m==null){ //perguntar se esta é a linha anterior correspondente à descodificacao
                LocalDateTime errorconvertNow = LocalDateTime.now();
                //updateLogFile(porta,modo,timeout,errorconvertNow,"ER","",parserConfig.logFilename());
                
            }else {
                //System.out.println(m);
                parserDB.respondeQuery(m);
                DNSMsg mensagem = parserDB.respondeQuery(m);//chamar a funcao
                if(mensagem.getHeader().getFlags().equals("Q+R")) mensagem.getHeader().setFlags("R+A");
                System.out.println(mensagem);
                //buf = mensagem.getBytes(mensagem);

                String servidormensagem = mensagem.toString();

                byte[] dados = servidormensagem.getBytes();
                InetAddress address2 = InetAddress.getByName("localhost");
                DatagramPacket packet2 = new DatagramPacket(dados, dados.length, address2, port);
                socket.send(packet2);
                LocalDateTime sentNow = LocalDateTime.now();
                String finalAdress[]=address2.toString().split("/");
                logfile.updateLogFileRP_RR("dados", sentNow, "RP", finalAdress[1]);
            }

        }
        socket.close();
        LocalDateTime shutdownNow = LocalDateTime.now();
        logfile.updateLogFileSP(shutdownNow, "SP", packetAdress, "razao qualquer");

    }
    public static void ServerSocket() throws IOException, ClassNotFoundException, SintaxeIncorretaException, InterruptedException {
        int portaSS = 4444;
        ServerSocket server = new ServerSocket(portaSS);
        boolean running = true;
        ParserConfig parserConfig = new ParserConfig("SP.conf");//parse do conf file
        ParserDB parserDB = new ParserDB(parserConfig.getDbfile());
        int soaretryTime = parserDB.getSOARETRY();
        int soarexpireTime = parserDB.getSOAEXPIRE();
        String domain = parserConfig.getWorkingDomain();
        
        int i = 1;
        int n;
        
        while (running) {
            System.out.println("Shit here we go again");
            Socket socketSS = server.accept();
            ObjectInputStream ois = new ObjectInputStream(socketSS.getInputStream());
            String receivedMessage = (String) ois.readObject();
            if (receivedMessage.equals(domain)) { // O SP e o SS trabalham no mesmo dominio
                ObjectOutputStream oos = new ObjectOutputStream(socketSS.getOutputStream());
                oos.writeObject(parserDB.getNumberofLines());//envia para o SS o numero de linhas a enviar
                ois = new ObjectInputStream(socketSS.getInputStream());
                receivedMessage = (String) ois.readObject();
                LocalDateTime timeRR = LocalDateTime.now();
                //updateLogFileRP_RR(receivedMessage, timeRR, "RR", socketSS.getInetAddress().toString());
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
                            if(socketConnected.isClosed) break;
                            oos = new ObjectOutputStream(socketSS.getOutputStream());
                            oos.writeObject(n+1 + " " + parserDB.getFileLines().get(n));
                            oos.flush();
                            n++;
                            //Thread.sleep(2000);
                        }
                        running=false;
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
        }
        System.out.println("Acabei");
    }
}
