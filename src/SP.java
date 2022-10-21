import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SP {
    private static List<LineParameter> lineParameters = new ArrayList<>();

    public static void main(String[] args) throws IOException, SintaxeIncorretaException{
        InetAddress address;
        DatagramSocket socket = new DatagramSocket(4445);;
        byte[] buf = new byte[256];
        boolean running = true;
        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            //path completo do ficheiro de configuracao
            parser("/home/joao/IdeaProjects/parsefile/src/main/java/sp.conf");


            address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println(received);
            socket.send(packet);
        }
        socket.close();

        }


    /**
     * Autor: João Castro
     * Modificado: 20/out/2022
     * Descrição:
     * */
    public static void parser (String filename) throws FileNotFoundException, SintaxeIncorretaException {
        String[] componente;
        List<String> linhas = lerFicheiro(filename);
        int line = 0;
        for (String linha : linhas) {
            componente = linha.split(" ");
            if (!linha.isEmpty() && componente[0].charAt(0) != '#' && componente.length==3) {
                LineParameter l = new LineParameter(componente[0], componente[1], componente[2]);
                lineParameters.add(l);
            } else if(componente[0].charAt(0) != '#' && componente.length>3) throw new SintaxeIncorretaException("Sintaxe do ficheiro está incorreta.");
            line++;
        }
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



}

