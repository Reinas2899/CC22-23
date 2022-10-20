import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SP {
    static List<LineParameter> lineParameters = new ArrayList<>();
    static List<ParameterDB> parametersDB = new ArrayList<>();
    
    public static void main(String[] args) {

        try {
            //path completo do ficheiro de configuracao
            parser("src/sp.conf");


            //while(true){;}

            /** DEBUG*/
            for(LineParameter l : lineParameters){
                System.out.print(l.toString());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SintaxeIncorretaException s) {
            System.out.print(s.getMessage());
        }

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


    public static void parserDB (String filename) throws FileNotFoundException, SintaxeIncorretaException {
        String[] componente;
        List<String> linhas = lerFicheiro(filename);
        int line = 0;
        for (String linha : linhas) {
            componente = linha.split(" ");
            if (!linha.isEmpty() && componente[0].charAt(0) != '#' && componente.length<=5 && componente.length>=3) {
                ParameterDB l = new ParameterDB(componente[0], componente[1], componente[2], componente[3], componente[4]);
                parametersDB.add(l);
            } else if(componente[0].charAt(0) != '#' && componente.length>5) throw new SintaxeIncorretaException("Sintaxe do ficheiro está incorreta.");
            line++;
        }
    }

}
