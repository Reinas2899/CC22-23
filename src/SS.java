import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SS {
    public static void main(String[] args) throws IOException, SintaxeIncorretaException {
        int porta = 4444; //confirmar
        ServerSocket serverSocket = new ServerSocket(porta);
        Socket sp = serverSocket.accept();

        String db_name = new String(sp.getOutputStream().toString()); // coletar nome da DB
        ParserDB parserDB = new ParserDB(db_name);

        /* 
         Falta:
         1. Construir DB
         2. Conectar no lado do SP
         3. Confirmar valores
        */

        serverSocket.close();
        sp.close();
    }
}