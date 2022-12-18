import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Cache {
    Hashtable<Integer, List<String>> cache = new Hashtable<>();
    //entrada | 0:name 1:type 2:value 3:ttl 4:order 5:origin 6:timestamp 7:index 8:status

    public Cache() {
        int N = 100;
        int e = 0;
        List<String> array = new ArrayList<>(9);
        int i;
        while (e < N) {
            for (i = 0; i < 8; i++) {
                array.add(null);
            }
            array.add("FREE");
            this.cache.put(e,array);
            e++;
        }
    }

    public int primeiraEntradaFree(){
        int entrada=0;
        for(int i=0;i<this.cache.size();i++){
            //System.out.println(this.getName(i)+"-"+this.getType(i)+"-"+this.getValue(i)+"-"+this.getTTL(i)+"-"+this.getOrder(i)+"-"+this.getOrigin(i)+"-"+"-"+this.getTimeStamp(i)+"-"+this.getIndex(i)+"-"+this.getStatus(i));
            if(this.getStatus(i).equals("FREE")){
                entrada=i;
                break;
            }
        }
        return entrada;
    }

    public void adicionarEntrada(List<String> entradas, LocalDateTime begin,LocalDateTime now,Logfile logfile,String componente){
        int N= this.primeiraEntradaFree();
        long duration = ChronoUnit.SECONDS.between(begin,now);
        entradas.add(String.valueOf(duration));
        entradas.add(String.valueOf(N));
        entradas.add("VALID");
        this.cache.put(N,entradas);
        logfile.updateLogFileRegistoEntradaCache(now,N,componente);
    }

    public boolean isAutoritiveDomain(String domain) {
        List<String> listofAuthorities=new ArrayList<>();
        int N = getCacheSize();
        for(int i=0;i<N;i++){
            if(this.getType(i)!=null && this.getType(i).equals("NS")) listofAuthorities.add(this.getValue(i));
        }
        boolean found = false;
        for (String line : listofAuthorities){
            if(line.contains(domain)) found=true;
        }
        return found;
    }

    //entrada | 0:name 1:type 2:value 3:ttl 4:order 5:origin 6:timestamp 7:index 8:status
    public String getName(int entrada){
        return this.cache.get(entrada).get(0);
    }

    public String getType(int entrada){
        return this.cache.get(entrada).get(1);
    }

    public String getValue(int entrada){
        return this.cache.get(entrada).get(2);
    }

    public String getTTL(int entrada){
        return this.cache.get(entrada).get(3);
    }

    public String getOrder(int entrada){
        return this.cache.get(entrada).get(4);
    }

    public String getOrigin(int entrada){
        return this.cache.get(entrada).get(5);
    }

    public String getTimeStamp(int entrada){
        return this.cache.get(entrada).get(6);
    }

    public String getIndex(int entrada){
        return this.cache.get(entrada).get(7);
    }

    public String getStatus(int entrada){
        return this.cache.get(entrada).get(8);
    }

    public int getCacheSize(){return this.cache.size();}
}
