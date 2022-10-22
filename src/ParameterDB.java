public class ParameterDB {


    private String parametro;
    private Tipo tipo;
    private String valor;
    private String ttl;
    private String prioridade;

    public ParameterDB(String parametro,String tipo,String valor, String ttl, String prioridade){
        this.parametro = parametro;
        this.tipo = convStrTipo(tipo);
        this.valor = valor;
        this.ttl = ttl;
        this.prioridade = prioridade;
    }
    public ParameterDB(){}

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public enum Tipo {
        DEFAULT,
        SOASP,
        SOAADMIN,
        SOASERIAL,
        SOAREFRESH,
        SOARETRY,
        SOAEXPIRE,
        NS,
        A,
        CNAME,
        MX,
        PTR
    }
    public Tipo convStrTipo(String s){
        Tipo t=null;
        switch (s){
            case ("DEFAULT"):
                t=Tipo.DEFAULT;
                break;
            case ("SOASP"):
                t=Tipo.SOASP;
                break;
            case ("SOAADMIN"):
                t=Tipo.SOAADMIN;
                break;
            case ("SOASERIAL"):
                t=Tipo.SOASERIAL;
                break;
            case ("SOAREFRESH"):
                t=Tipo.SOAREFRESH;
                break;
            case ("SOAEXPIRE"):
                t=Tipo.SOAEXPIRE;
                break;
            case ("NS"):
                t=Tipo.NS;
                break; 
            case ("A"):
                t=Tipo.A;
                break;
            case ("CNAME"):
                t=Tipo.CNAME;
                break;
            case ("MX"):
                t=Tipo.MX;
                break;
            case ("PTR"):
                t=Tipo.PTR;
                break;       

        }
        return t;
    }

    @Override
    public String toString() {
        return "ParameterDB{" +
                "parametro='" + parametro + '\'' +
                ", tipo=" + tipo +
                ", valor='" + valor + '\'' +
                ", ttl='" + ttl + '\'' +
                ", prioridade='" + prioridade + '\'' +
                '}';
    }
}
