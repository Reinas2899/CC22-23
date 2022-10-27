import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ParserConfig {
    private List<LineParameter> lineParameters = new ArrayList<>();
    private String logfilename;
    private String dbfile;

    public  ParserConfig(String filename) throws FileNotFoundException, SintaxeIncorretaException {
        String[] componente;
        List<String> linhas = lerFicheiro(filename);
        int line = 0;
        for (String linha : linhas) {
            componente = linha.split(" ");
            if (!linha.isEmpty() && componente[0].charAt(0) != '#' && componente.length==3) {
                LineParameter l = new LineParameter(componente[0], componente[1], componente[2]);
                lineParameters.add(l);
            } else if(componente[0].charAt(0) != '#' && componente.length>3) throw new SintaxeIncorretaException("Sintaxe do ficheiro está incorreta."+line);
            line++;
        }
        logFilename();
        dbFilename();
    }

    public List<String> lerFicheiro (String nomeFich) throws FileNotFoundException {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(nomeFich), StandardCharsets.UTF_8);
        } catch (IOException exc) {
            lines = new ArrayList<>();
        }
        if (lines.isEmpty()) throw new FileNotFoundException("Ficheiro não encontrado");
        return lines;
    }

    public List<LineParameter> getLineParameters() {
        return lineParameters;
    }

    public void setLineParameters(List<LineParameter> lineParameters) {
        this.lineParameters = lineParameters;
    }

    public void logFilename(){
        String filename = "";
        for(LineParameter lp : lineParameters){
            if(lp.getTipo().equals(LineParameter.Tipo.LG) && lp.getParametro().equals("all")) filename = lp.getValor();
        }
        this.setLogfilename(filename);
    }

    public void dbFilename(){
        String filename = "";
        for(LineParameter lp : lineParameters){
            if(lp.getParametro().equals("DB")) filename = lp.getValor();
        }
        setDbfile(filename);
    }

    public String getLogfilename() {
        return logfilename;
    }

    public void setLogfilename(String logfilename) {
        this.logfilename = logfilename;
    }

    public String getDbfile() {
        return dbfile;
    }

    public void setDbfile(String dbfile) {
        this.dbfile = dbfile;
    }
}

