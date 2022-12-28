import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParserRev {
    private List<LineParameter> lineParameters = new ArrayList<>();

    public ParserRev(String filename) throws FileNotFoundException, SintaxeIncorretaException {
        String[] componente;
        List<String> linhas = lerFicheiro(filename);
        int line = 0;
        for (String linha : linhas) {
            componente = linha.split(" ");
            if (!linha.isEmpty() && componente[0].charAt(0) != '#' && componente.length==2) {
                LineParameter l = new LineParameter(componente[0],"null", componente[1]);
                lineParameters.add(l);
            } else if(componente[0].charAt(0) != '#' && componente.length>3) throw new SintaxeIncorretaException("Sintaxe do ficheiro está incorreta."+line);
            line++;
        }
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

    public DNSMsg arpa(DNSMsg m){
        String address = m.getData().getQinfo().getName();
        String response = "Não foi possível encontrar o domínio da máquina com o endereço "+address;
        for(LineParameter l : lineParameters){
           String [] parts =  l.getValor().split("\\.");
           int i=0;
           StringBuilder adr= new StringBuilder();
           while(i<4){
               adr.append(parts[3-i]);
	       if(i!=3) adr.append(".");
               i++;
           }
	   //System.out.println(adr);
           if(adr.toString().equals(address)){
               response=l.getValor() + " domain name pointer "+ l.getParametro();
               break;
           }
        }
        m.getData().setResp_values(new ArrayList<>(Collections.singleton(response)));
        return m;
    }
}
