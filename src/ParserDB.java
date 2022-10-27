import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ParserDB {
    private List<ParameterDB> linesParametersDB = new ArrayList<>();

    public ParserDB(String filename) throws FileNotFoundException, SintaxeIncorretaException {
        String[] componente;
        ParameterDB p = null;
        String parameter="";
        String defaultname="";
        String defaultdomain="";
        String ttldefault="";
        String defaultprior="";
        String subdomain = "";
        String domaintipo="";
        String fulldomain="";
        List<String> linhas = lerFicheiro(filename);
        int line = 0;
        for (String linha : linhas) {
            componente = linha.split(" ");
            if(componente[0].equals("@") && componente[1].equals("DEFAULT")) {
                parameter=componente[0];
                defaultname=componente[1];
                defaultdomain=componente[2];
            }
            else if(componente[0].equals("TTL")){
                ttldefault=componente[0]+componente[1];
                defaultprior=componente[2];
            }
            else if(componente[0].equals("Smaller.@")){
                subdomain=componente[0];
                domaintipo = componente[1];
                fulldomain = componente[2];
            }
            else if(!linha.isEmpty() && componente[0].charAt(0)!='#' && componente.length==4) {
                linesParametersDB.add(new ParameterDB(componente[0], componente[1], componente[2], componente[3], null));
            }
            else if(!linha.isEmpty() && componente[0].charAt(0)!='#' && componente.length==5){
                linesParametersDB.add(new ParameterDB(componente[0], componente[1], componente[2], componente[3], componente[4]));
            }
            else if(componente[0].charAt(0) != '#' && componente.length>5) throw new SintaxeIncorretaException("Sintaxe do ficheiro está incorreta."+line);
            line++;
        }
        linesParametersDB.add(new ParameterDB(parameter, defaultname, defaultdomain, ttldefault, defaultprior));
        linesParametersDB.add(new ParameterDB(subdomain,domaintipo,fulldomain,null,null));
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

    public int countAuthoritatives(){
        int c=0;
        for(ParameterDB p : linesParametersDB){
            if(p.getTipo().equals(ParameterDB.Tipo.NS)) c++;
        }
        return c;
    }

    public int countExtravalues(){
        int c=0;
        for(ParameterDB p : linesParametersDB){
            if(p.getTipo().equals(ParameterDB.Tipo.A)) c++;
        }
        return c;
    }

    public List<ParameterDB> getLinesParametersDB() {
        return linesParametersDB;
    }

    public void setLinesParametersDB(List<ParameterDB> linesParametersDB) {
        this.linesParametersDB = linesParametersDB;
    }
    public String serverDomain(){
        String valor="";
        for (ParameterDB pdb : linesParametersDB){
            if(pdb.getTipo().equals(ParameterDB.Tipo.DEFAULT)) valor= pdb.getValor();
        }
        return valor;
    }
    public String respondeQuery(DNSMsg msg){
        String resposta="Nao somos do mesmo dominio";
        if(msg.getData().getQinfo().getName().equals(serverDomain())) resposta ="O SP conhece esse dominio";
        return resposta;
    }
}

