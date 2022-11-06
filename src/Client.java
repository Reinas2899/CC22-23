    import java.io.*;
    import java.net.*;
    import java.io.IOException;
    import java.net.DatagramPacket;
    import java.net.DatagramSocket;
    import java.net.InetAddress;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;

    public class Client {
        public static void main(String[] args) throws IOException,SintaxeIncorretaException {
                DatagramSocket socket= new DatagramSocket();
                InetAddress address= InetAddress.getByName("localhost");
                byte[] buf = new byte[1024];

                DNSMsg msg = readquery("querydebug.txt");
                //System.out.println(msg.toString());
                buf = msg.getBytes(msg);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445); // controi datagrama
                socket.send(packet); //envia datagrama pelo socket
                DatagramPacket packet2= new DatagramPacket(buf,buf.length); 
                socket.setSoTimeout(5000);
                
            
                try {
                    socket.receive(packet2);
                    address = packet2.getAddress();
                    int port = packet2.getPort();
               // packet2= new DatagramPacket(buf, buf.length, address, port);
                    byte [] data = packet2.getData();
                    String a = new String(data,0,packet2.getLength());
                    System.out.println(a);
                } catch (SocketTimeoutException e) {
                    System.out.println("Servidor Inativo");
                    socket.close();
                }

                


                
                //ByteArrayInputStream in = new ByteArrayInputStream(data);
                ///ObjectInputStream is = new ObjectInputStream(in);
            //try {
            //    DNSMsg m = (DNSMsg) is.readObject();
            //    System.out.println(m);
           // } catch (ClassNotFoundException e) {
           //    e.printStackTrace();
           // }
            socket.close();

        }

        public static DNSMsg readquery(String filename) throws FileNotFoundException, SintaxeIncorretaException {
            String[] componente=new String[7];
            String [] componente2=new String[2];
            List<String> linhas = lerFicheiro(filename);
            for (String linha : linhas) {
                componente = linha.split(",");
                componente2 = componente[5].split(";");
            }
            Header header = new Header(componente[0],componente[1]);
            String tipo = componente[6].substring(0,componente[6].length()-1);
            Qinfo qinfo = new Qinfo(componente2[1],tipo);
            Data data = new Data(qinfo);
            return new DNSMsg(header,data);
        }
        /**
         * Autor: João Castro
         * Modificado: 20/out/2022
         * Descrição:
         * */
        public static List<String> lerFicheiro (String nomeFich) throws FileNotFoundException {
            List<String> lines;
            try {
                lines = Files.readAllLines(Paths.get(nomeFich), StandardCharsets.UTF_8);
            } catch (IOException exc) {
                lines = new ArrayList<>();
            }
            if (lines.isEmpty()) throw new FileNotFoundException("Ficheiro não encontrado");
            return lines;
        }


        /*
        public static void dividevalor(String[] componentes, HashMap<String,String> query_values){

            String[] dividido = new String[2];

            for (String string : componentes) {
                dividido = string.split(" = ");
                query_values.put(dividido[0], dividido[1]);
            }
        }

        public static DNSMsg constroiMsg(HashMap<String,String> query_values){
            Header header = new Header(query_values.get("MESSAGE-ID"),
                    query_values.get("FLAGS"),
                    query_values.get("RESPONSE-CODE"),
                    query_values.get("N-VALUES"),
                    query_values.get("N-AUTHORITIES"),
                    query_values.get("N-EXTRA-VALUES"));
            Qinfo qinfo = new Qinfo(query_values.get("QUERY-INFO.NAME"), query_values.get("QUERY-INFO.TYPE"));
            Data data = new Data(qinfo, query_values.get("RESPONSE-VALUES"), query_values.get("AUTHORITIES-VALUES"), query_values.get("EXTRA-VALUES"));

            DNSMsg dns_Msg = new DNSMsg(header, data);
            return dns_Msg;

        }*/

        /*
        public static DNSMsg readOptionalquery(String filename) throws FileNotFoundException, SintaxeIncorretaException {
            String[] componente;
            HashMap<String,String> query_values = new HashMap<>();
            List<String> linhas = lerFicheiro(filename);
            int line = 0;
            for (String linha : linhas) {
                componente = linha.split(", ");
                if (!linha.isEmpty() && componente[0].charAt(0) != '#') {

                    dividevalor(componente, query_values);
                } else if(componente[0].charAt(0) != '#' && componente.length>3) throw new SintaxeIncorretaException("Sintaxe do ficheiro está incorreta.");
                line++;
            }
            return constroiMsg(query_values);

        }
        */
    }
