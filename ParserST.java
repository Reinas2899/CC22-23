import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List; 

public class ParserST{
    private List<LineParameter> lineParameters = new ArrayList<>();
    private String logfilename;
    private String workingDomain;


    public ParserST(String filename) throws FileNotFoundException, SintaxeIncorretaException {
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
        int i;
        for(i=0;i<lines.size();i++){
            if(lines.get(i).equals("\n") || lines.get(i).length()==0) lines.remove(i);
        }
        return lines;
    }

    public void logFilename(){
        String filename = "";
        for(LineParameter lp : lineParameters){
            if(lp.getTipo().equals(LineParameter.Tipo.LG) && !lp.getParametro().equals("all")) filename = lp.getValor();
        }
        this.setLogfilename(filename);
    }

    public String getLogfilename() {
        return logfilename;
    }

    public void setLogfilename(String logfilename) {
        this.logfilename = logfilename;
    }

    public void workDomain(){
        String domain="";
        for(LineParameter lp : lineParameters){
            if(lp.getTipo().equals(LineParameter.Tipo.LG) && !lp.getParametro().equals("all")) domain = lp.getParametro();
        }
        setWorkingDomain(domain);
    }

    public String getWorkingDomain() {
        return workingDomain;
    }

    public void setWorkingDomain(String workingDomain) {
        this.workingDomain = workingDomain;
    }
}