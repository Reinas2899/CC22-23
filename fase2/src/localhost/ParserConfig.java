import javax.sound.sampled.Line;
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
    private String allLogfile;
    private String dbfile;

    public String getAllLogfile() {
        return allLogfile;
    }

    public void setAllLogfile(String allLogfile) {
        this.allLogfile = allLogfile;
    }

    private String workingDomain;

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
        workDomain();
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

    public String getSPaddress(){
      String ip ="";
      for(LineParameter l : lineParameters){
	   if(l.getTipo().equals(LineParameter.Tipo.SP)) ip = l.getValor();
	}
	return ip;
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
            if(lp.getTipo().equals(LineParameter.Tipo.LG) && !lp.getParametro().equals("all")) filename = lp.getValor();
        }
        this.setLogfilename(filename);
    }

    public void alllogFilename(){
        String filename = "";
        for(LineParameter lp : lineParameters){
            if(lp.getTipo().equals(LineParameter.Tipo.LG) && lp.getParametro().equals("all")) filename = lp.getValor();
        }
        this.setAllLogfile(filename);
    }


    public String getIPPortSP(){
        String a="";
        for (LineParameter l:lineParameters){
            if(l.getTipo().equals(LineParameter.Tipo.SP)) a= l.getValor();
        }
        return a;
    }

    public String getroot(){
        String a="";
        for (LineParameter l:lineParameters){
            if(l.getTipo().equals(LineParameter.Tipo.ST)) a= l.getValor();
        }
        return a;
    }

    public String getIPPortSS(){
        String a="";
        for (LineParameter l:lineParameters){
            if(l.getTipo().equals(LineParameter.Tipo.SS)) a= l.getValor();
        }
        return a;
    }


    public void dbFilename(){
        String filename = "";
        for(LineParameter lp : lineParameters){
            if(lp.getTipo().equals(LineParameter.Tipo.DB)){
                String [] dirs = lp.getValor().split("/");
                String pathaux = dirs[1]+"//"+dirs[2];
                String pathFinal = pathaux+"//" + dirs[3] ;

                filename =pathFinal;
            }
        }
        setDbfile(filename);
    }

    public void workDomain(){
        String domain="";
        for(LineParameter lp : lineParameters){
            if(lp.getTipo().equals(LineParameter.Tipo.LG) && !lp.getParametro().equals("all")) domain = lp.getParametro();
        }
        setWorkingDomain(domain);
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

    public String getWorkingDomain() {
        return workingDomain;
    }

    public void setWorkingDomain(String workingDomain) {
        this.workingDomain = workingDomain;
    }
}

