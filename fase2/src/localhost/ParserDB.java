import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParserDB {
    private List<ParameterDB> linesParametersDB = new ArrayList<>();
    private List<String> fileLines = new ArrayList<>();
    private int numberofLines;

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
        setFileLines(linhas);
        int line = 0;
        for (String linha : linhas) {
            //System.out.println(linha);
            componente = linha.split(" ");
            if(componente[0].equals("@") && componente[1].equals("DEFAULT")) {
                parameter=componente[0];
                defaultname=componente[1];
                defaultdomain=componente[2];
                line++;
            }
            else if(componente[0].equals("TTL")){
                ttldefault=componente[0]+componente[1];
                defaultprior=componente[2];
                line++;
            }
            else if(componente[0].equals("Osaka.@") || componente[0].equals("Nagoya.@") ){
                subdomain=componente[0];
                domaintipo = componente[1];
                fulldomain = componente[2];
                line++;
                linesParametersDB.add(new ParameterDB(subdomain, domaintipo, fulldomain,null,null));
            }
            else if(!linha.isEmpty() && componente[0].charAt(0)!='#' && componente.length==4) {
                linesParametersDB.add(new ParameterDB(componente[0], componente[1], componente[2], componente[3], null));
                line++;
            }
            else if(!linha.isEmpty() && componente[0].charAt(0)!='#' && componente.length==5){
                linesParametersDB.add(new ParameterDB(componente[0], componente[1], componente[2], componente[3], componente[4]));
                line++;
            }
            else if(componente[0].charAt(0) != '#' && componente.length>5) throw new SintaxeIncorretaException("Sintaxe do ficheiro está incorreta."+line);

        }
        linesParametersDB.add(new ParameterDB(parameter, defaultname, defaultdomain, ttldefault, defaultprior));
        setNumberofLines(line);
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

    public List<ParameterDB> getLinesParametersDB() {
        return linesParametersDB;
    }

    public int getNumberofLines() {
        return numberofLines;
    }

    public void setNumberofLines(int numberofLines) {
        this.numberofLines = numberofLines;
    }

    public void setLinesParametersDB(List<ParameterDB> linesParametersDB) {
        this.linesParametersDB = linesParametersDB;
    }

    public List<String> getFileLines() {
        return fileLines;
    }

    public void setFileLines(List<String> fileLines) {
        for(String l : fileLines) {
            if(!l.startsWith("#")) this.fileLines.add(l);
        }
    }

    public int getSOARETRY(){
        String time="";
        for(ParameterDB parameterDB:this.linesParametersDB){
            if(parameterDB.getTipo().equals(ParameterDB.Tipo.SOARETRY)) time = parameterDB.getValor();
        }
        return Integer.parseInt(time);
    }

    public int getSOAEXPIRE(){
        String time="";
        for(ParameterDB parameterDB:this.linesParametersDB){
            if(parameterDB.getTipo().equals(ParameterDB.Tipo.SOAEXPIRE)) time = parameterDB.getValor();
        }
        return Integer.parseInt(time);
    }

    public List<String> getServers(){
        List<String> allServers= new ArrayList<>();
        for(ParameterDB db : this.linesParametersDB) {
            if (db.getTipo().equals(ParameterDB.Tipo.NS) || db.getTipo().equals(ParameterDB.Tipo.MX)){
                String [] valores= new String[10];
                valores = db.getValor().split("\\.");
                allServers.add(valores[0]);
            }
        }
        return allServers;
    }
    
        public String getSPsub(){
        String domain="";
        for(ParameterDB db : this.linesParametersDB) {
            if (!db.getParametro().equals("@") && db.getTipo().equals(ParameterDB.Tipo.NS) ){
                domain=db.getValor();
            }
        }
        return domain;
    }

    public String getSoaspValue(){
        String value="";
        for(ParameterDB db : this.linesParametersDB) {
            if (db.getTipo().equals(ParameterDB.Tipo.SOASP)){
                value = db.getValor();
            }
        }
        return value;
    }

    public List<String> getDomains(){
        List<String> allServerDomains= new ArrayList<>();
        for(ParameterDB db : this.linesParametersDB) {
            if (db.getTipo().equals(ParameterDB.Tipo.NS) || db.getTipo().equals(ParameterDB.Tipo.MX)){
                allServerDomains.add(db.getValor());
            }
        }
        String domain=allServerDomains.get(0).split("\\.")[1];
        allServerDomains.add(domain + ".");
        return allServerDomains;
    }

    public int setResponseCode(String domain){//  tokyoSP.tokyo.
        int code=2;
        List<String> l = getDomains();
        for(String s : l){
            if(s.equals(domain)){
                code = 0;
                break;
            }
        }
        if(getSoaspValue().equals("tokyoSP.tokyo.") && (!domain.equals("tokyo.") && !domain.equals("tokyoSP.tokyo.") && !domain.equals("tokyoSS1.tokyo.") && !domain.equals("tokyoSS2.tokyo."))) code=1;
        if(getSoaspValue().equals("tokyoSP.tokyo.") && (domain.equals("tokyo.") || domain.equals("tokyoSP.tokyo.") || domain.equals("tokyoSS1.tokyo.") || domain.equals("tokyoSS2.tokyo."))) code=0;
        if(getSoaspValue().equals("osakaSP.osaka.tokyo.") && (!domain.equals("tokyo.") && !domain.equals("tokyoSP.tokyo.") && !domain.equals("tokyoSS1.tokyo.") && !domain.equals("tokyoSS2.tokyo."))) code=0;
        return code;
    }
    
    
    
    public String getIPSPsub(){
        String ipport="";
        for(ParameterDB parameterDB:this.linesParametersDB){
            if(parameterDB.getParametro().equals("osakaSP.osaka")) ipport = parameterDB.getValor();
            if(parameterDB.getParametro().equals("nagoyaSP.nagoya")) ipport=parameterDB.getValor();
        }
        return ipport;
    }

    public String getIPDomain(String domain){
        String ip="";
        String [] sub= new String[10];
        sub = domain.split("\\.");
        for(ParameterDB db : this.linesParametersDB) {
            if(sub[0].equals(db.getParametro())) ip= db.getValor();
        }
        return ip;
    }

    public String serverDomain(){
        String valor="";
        for (ParameterDB pdb : linesParametersDB){
            if(pdb.getTipo().equals(ParameterDB.Tipo.DEFAULT)) valor= pdb.getValor();
        }
        return valor;
    }

    public String ttlValue(){
        String ttl=null;
        for(ParameterDB db : this.linesParametersDB) {
            if (db.getTtl()!=null && db.getTtl().equals("TTLDEFAULT") && db.getPrioridade()!=null) ttl = db.getPrioridade() ;
        }
        return ttl;
    }

    public int countValues(String queryinfotype){
        int c=0;
        for(ParameterDB db : this.linesParametersDB){
            if(db.getTipo().equals(db.convStrTipo(queryinfotype))) c++;
        }
        return c;
    }

    public int countAuthorities(String name){
        int c=0;
        for(ParameterDB db : this.linesParametersDB){
            if(db.getParametro().equals("@") && db.getTipo().equals(ParameterDB.Tipo.NS) && db.getValor().contains(name)) c++;
        }
        return c;
    }

    public int countExtra(String queryinfotype,String name){
        return countValues(queryinfotype)+countAuthorities(name);
    }

    public List<String> procuraEntradas(String parametro){
        List<String> list = new ArrayList<>();
        for(ParameterDB db : this.linesParametersDB){
            if(db.getParametro().equals(parametro)) list.add(db.toString());
        }
        return list;
    }
        
    public String procuraIPdominio(String domain,ParameterDB.Tipo type){
        String ip="";
        //System.out.println("dominio:"+domain+" tipo:"+type);
        for(ParameterDB db : this.linesParametersDB) {
            //System.out.println(db);
            if (db.getTtl()!=null && domain.contains(db.getParametro()) && db.getTipo().equals(type)){
                //System.out.println("MATCH");
                ip=db.getValor();
            }
        }
        return ip;
    }

    public List<String> procuraValues(String domain,ParameterDB.Tipo type){
        List<String> l = new ArrayList<>() ;
        System.out.println("dominio:"+domain+" tipo:"+type);
        for(ParameterDB db : this.linesParametersDB) {
            if (db.getValor().contains(domain) && db.getTipo().equals(type)){
                System.out.println(serverDomain()+" "+db.getTipo()+" "+db.getValor()+" "+ttlValue());
                l.add(serverDomain()+" "+db.getTipo()+" "+db.getValor()+" "+ttlValue());
            }
        }
        return l;
    }

    public List<String> procuraExtra(List<String> dominios,ParameterDB.Tipo type){
        List<String> l = new ArrayList<>() ;
        for(String dominio:dominios) {
            for (ParameterDB db : this.linesParametersDB) {
                if (dominio.contains(db.getParametro()) && db.getTipo().equals(type))
                    l.add(dominio + " " + db.getTipo() + " " + db.getValor() + " " + ttlValue());
            }
        }
        return l;
    }

    
    public int contaAutoritativos(String name){
        int c=0;
        for(ParameterDB db : this.linesParametersDB){
            if(db.getParametro().equals(name) && db.getTipo().equals(ParameterDB.Tipo.A)) c++;
        }
        return c;
    }


    public List<String> getListofValues(String type){//MX
        List<String> listofValues=new ArrayList<>();
        for(ParameterDB db : this.linesParametersDB){
            if(db.getTipo().equals(db.convStrTipo(type))) listofValues.add(serverDomain()+" "+db.getTipo()+" "+db.getValor()+" "+ttlValue()+" "+db.getPrioridade());
        }
        /*int lastIndex = listofValues.size()-1;
        String lastValue= listofValues.get(lastIndex).substring(0,lastIndex);
        listofValues.set(lastIndex,lastValue);*/
        return listofValues;
    }

    public List<String> getListofAuthorities(String domain){//EXAMPLE.COM
        List<String> listofAuthorities=new ArrayList<>();
        for(ParameterDB db : this.linesParametersDB){
            if(db.getParametro().equals("@") && db.getTipo().equals(ParameterDB.Tipo.NS) && db.getValor().contains(domain)) listofAuthorities.add(serverDomain()+" "+db.getTipo()+" "+db.getValor()+" "+ttlValue());
        }
        return listofAuthorities;
    }

	
    public boolean isAutoritiveDomain(String domain) {
        List<String> listofAuthorities=new ArrayList<>();
        for (ParameterDB db : this.linesParametersDB) {
            if(db.getParametro().equals("@") && db.getTipo().equals(ParameterDB.Tipo.NS)) listofAuthorities.add(db.getValor());
        }
        boolean found = false;
        for (String line : listofAuthorities){
            if(line.contains(domain)) found=true;
        }
        return found;
    }

    public List<String> getListofExtra(){
        List<String> listofExtra=new ArrayList<>();
        for(String domain : getDomains()){
            listofExtra.add(domain+" "+ParameterDB.Tipo.A+" "+getIPDomain(domain)+ " "+ttlValue());
        }
        return listofExtra;
    }

    public List<String> cnameRecords(){
        List<String> list = new ArrayList<>();
        String soasp="";
        for(ParameterDB db : this.linesParametersDB){
            if(db.getTipo().equals(ParameterDB.Tipo.SOASP)) list.add("primary name server = "+db.getValor());
            if(db.getTipo().equals(ParameterDB.Tipo.SOAADMIN)) list.add("responsible mail addr = "+db.getValor());
            if(db.getTipo().equals(ParameterDB.Tipo.SOASERIAL)) list.add("serial = "+db.getValor());
            if(db.getTipo().equals(ParameterDB.Tipo.SOAREFRESH)) list.add("refresh = "+db.getValor());
            if(db.getTipo().equals(ParameterDB.Tipo.SOAEXPIRE)) list.add("expire = "+db.getValor());
            if(db.getTipo().equals(ParameterDB.Tipo.DEFAULT)) list.add("default TTL = "+db.getPrioridade());
        }
        return list;
    }

    public int checkQuery(DNSMsg query,String domain){
        int r=0;
        ExceptionHandler exceptionHandler = new ExceptionHandler();
        if(!exceptionHandler.isNumeric(query.getHeader().getMessageID()) || !exceptionHandler.isNumeric(query.getHeader().getResponse_code())
                || !exceptionHandler.isNumeric(query.getHeader().getN_values()) || !exceptionHandler.isNumeric(query.getHeader().getN_authorities())
                || !exceptionHandler.isNumeric(query.getHeader().getN_extravalues())) r=3;
        if(!exceptionHandler.typeExists(query.getData().getQinfo().getType_value())) r=1;
        if(!query.getData().getQinfo().getName().equals(domain)) r=2;
        return r;
    }

    public DNSMsg respondeQuery(DNSMsg msg){
            String dominio = msg.getData().getQinfo().getName();
            int code = setResponseCode(dominio);
            msg.getHeader().setResponse_code(String.valueOf(code));

            if(msg.getHeader().getFlags().equals("Q+R")) msg.getHeader().setFlags("R+A");
            int N = countAuthorities(msg.getData().getQinfo().getName());
            msg.getHeader().setN_authorities(String.valueOf(N));
            if(msg.getData().getQinfo().getType_value().equalsIgnoreCase("A")){
                //System.out.println("ENTROU");
                String ip = procuraIPdominio(msg.getData().getQinfo().getName(),ParameterDB.Tipo.A);
                //System.out.println(ip);
                List<String> l = new ArrayList<>();
                l.add(ip);
                int s = l.size();
                int v = N+s;
                msg.getData().setResp_values(l);
                msg.getHeader().setN_values(String.valueOf(s));
                msg.getHeader().setN_extravalues(String.valueOf(v));
            }else if(msg.getData().getQinfo().getType_value().equalsIgnoreCase("NS")){
                List<String> l1 = procuraValues(msg.getData().getQinfo().getName(),ParameterDB.Tipo.NS);
                List<String> dominios = new ArrayList<>();
                l1.forEach(x->dominios.add(x.split(" ")[2]));
                List<String> l2 = procuraExtra(dominios,ParameterDB.Tipo.A);
                msg.getData().setResp_values(l1);
                msg.getData().setExt_values(l2);
                msg.getHeader().setN_values(String.valueOf(countValues(String.valueOf(l1.size()))));
                msg.getHeader().setN_extravalues(String.valueOf(l2.size()));
            }
            else if(msg.getData().getQinfo().getType_value().equals("MX")) {
                List<String> l1 = procuraValues(msg.getData().getQinfo().getName(),ParameterDB.Tipo.MX);
                List<String> dominios = new ArrayList<>();
                l1.forEach(x->dominios.add(x.split(" ")[2]));
                List<String> l2 = procuraExtra(dominios,ParameterDB.Tipo.A);
                //System.out.println(l1);
                msg.getData().setResp_values(l1);
                msg.getData().setExt_values(l2);
                //System.out.println(l2);
                msg.getHeader().setN_values(String.valueOf(countValues(String.valueOf(l1.size()))));
                msg.getHeader().setN_extravalues(String.valueOf(l2.size()));
           }else if(msg.getData().getQinfo().getType_value().equals("CNAME")){
                 msg.getData().setResp_values(cnameRecords());
                 msg.getHeader().setN_values(String.valueOf((cnameRecords().size())));
            }if(msg.getData().getQinfo().getType_value().equals("www")) {
                msg.getData().setResp_values(procuraEntradas("www"));//contaAutoritativos
                msg.getHeader().setN_values(String.valueOf((procuraEntradas("www").size())));
            }if(msg.getData().getQinfo().getType_value().equals("ftp")) {
                msg.getData().setResp_values(procuraEntradas("ftp"));
                msg.getHeader().setN_values(String.valueOf((procuraEntradas("ftp").size())));
            }
            return msg;
        //}else return null;
    }


    
    public ParserDB(){this.linesParametersDB=new ArrayList<>();}
    public ParserDB(List<ParameterDB> l){
        this.linesParametersDB=new ArrayList<>(l);
    }


    public List<ParameterDB> getDBLines(List<String> linhas){
        String[] componente;
        List<ParameterDB> list = new ArrayList<>();
        String parameter="";
        String defaultname="";
        String defaultdomain="";
        String ttldefault="";
        String defaultprior="";
        String subdomain = "";
        String domaintipo="";
        String fulldomain="";
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
            else if(componente[0].equals("Osaka.@") || componente[0].equals("Nagoya.@") ){
                subdomain=componente[0];
                domaintipo = componente[1];
                fulldomain = componente[2];
                list.add(new ParameterDB(subdomain,domaintipo,fulldomain,null,null));
            }
            else if(!linha.isEmpty() && componente[0].charAt(0)!='#' && componente.length==4) {
                list.add(new ParameterDB(componente[0], componente[1], componente[2], componente[3], null));
            }
            else if(!linha.isEmpty() && componente[0].charAt(0)!='#' && componente.length==5){
                list.add(new ParameterDB(componente[0], componente[1], componente[2], componente[3], componente[4]));
            }
        }
        list.add(new ParameterDB(parameter, defaultname, defaultdomain, ttldefault, defaultprior));
        return list;
    }
}

