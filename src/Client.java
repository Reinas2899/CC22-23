import java.io.*;
import java.net.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;


public class Client {
    public static void main(String[] args) throws IOException,InterruptedException {
        DatagramSocket socket= new DatagramSocket();
        InetAddress address= InetAddress.getByName("localhost");
        byte[] buf = new byte[1024];
        int connectionFailed=0;
        Logfile logfile = new Logfile("/var/dns/Client.log");
        logfile.updateLogFileEV("log-file-created", LocalDateTime.now(), "EV", "/var/dns/Client.log");

	DNSMsg msg = readquery("3874,Q+R,0,0,0,0;example.com.,MX;");
	buf = msg.getBytes(msg);
	String [] endereco = address.toString().split("/");
	logfile.updateLogFileST(4445, "debug", 5000, LocalDateTime.now(), "ST",  endereco[1]);
	DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445); // controi datagrama
	logfile.updateLogFileQR_QE(msg.toString(), LocalDateTime.now(), "QE", endereco[1]);
	socket.send(packet); //envia datagrama pelo socket
	DatagramPacket packet2= new DatagramPacket(buf,buf.length);
	StringBuilder stringBuilder = new StringBuilder();
	Thread.sleep(1000);
	System.out.print("A estabelecer conexão com SP");
	Thread.sleep(1000);
	System.out.print(".");
	Thread.sleep(1000);
	System.out.print(".");
	Thread.sleep(1000);
	System.out.print(".");
	Thread.sleep(1000);
	System.out.print("\n");
	socket.setSoTimeout(5000);


        try {
            socket.receive(packet2);
            address = packet2.getAddress();
            int port = packet2.getPort();
            byte [] data = packet2.getData();
            String a = new String(data,0,packet2.getLength());
            int npackets = Integer.parseInt(a);
            endereco = address.toString().split("/");
            int i=0;
            while(i<npackets){
                DatagramPacket packet3= new DatagramPacket(buf,buf.length,address,port);
                socket.receive(packet3);
                byte [] data3 = new byte[1024];
                data3 = packet3.getData();
                stringBuilder.append(new String(data3,0,packet3.getLength()));
                i++;
            }
            System.out.println(stringBuilder.toString());
        } catch (SocketTimeoutException e) {
            System.out.println("Conexão com SP falhou.");
            logfile.updateLogFileTO("conexao-SP",5000, LocalDateTime.now(), "TO", endereco[1]);
            
            connectionFailed=1;
        }
        try{
        if(connectionFailed==1){
            byte[] buffer = new byte[1024];
            buffer=msg.getBytes(msg);
            DatagramPacket packetSS= new DatagramPacket(buffer,buffer.length,address,4444);
            socket.send(packetSS);
            
            DatagramPacket numPacket= new DatagramPacket(buffer,buffer.length);
            socket.receive(numPacket);
            InetAddress end = numPacket.getAddress();
            int nporta = numPacket.getPort();
            byte [] numData = numPacket.getData();
            String numPacotes = new String(numData,0,numPacket.getLength());
            int num = Integer.parseInt(numPacotes);
            
            
            logfile.updateLogFileQR_QE(msg.toString(), LocalDateTime.now(), "QE", endereco[1]);
            int cic=0;
            StringBuilder sb = new StringBuilder();
            while(cic<num){
		    DatagramPacket packet2SS = new DatagramPacket(buffer,buffer.length,end,nporta);
		    socket.receive(packet2SS);
		    byte[] dados = new byte[1024];
		    dados=packet2SS.getData();
		    String a = new String(dados,0,packet2SS.getLength());
		    sb.append(a);
		    cic++;
            }
            System.out.println(sb.toString());
        }
        }catch (SocketTimeoutException e) {
            System.out.println("Conexão com SS falhou.");
            System.out.println("Tente mais tarde novamente.");
        }
        socket.close();
        //logfile.updateLogFileSP(LocalDateTime.now(), "SP", endereco[1], "Encerrou sem problemas");
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
