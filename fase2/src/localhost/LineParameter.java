/**
 * Autor: João Castro
 * Modificado: 20/out/2022
 * Descrição:
 * */
public class LineParameter {
    private String parametro;
    private Tipo tipo;
    private String valor;

    public LineParameter(String parametro,String tipo,String valor){
        this.parametro = parametro;
        this.tipo = convStrTipo(tipo);
        this.valor = valor;
    }
    public String getParametro() {
        return parametro;
    }

    public void setParametro(String domain) {
        this.parametro = domain;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = convStrTipo(tipo);
    }

    public String getValor() {
        return valor;
    }

    public void setValue(String parValue) {
        this.valor = parValue;
    }

    public enum Tipo {
        DB,
        SP,
        SS,
        DD,
        ST,
        LG,
        ST1,
        ST2
    }
    public static Tipo convStrTipo(String s){
        Tipo t=null;
        switch (s){
            case ("DB"):
                t=Tipo.DB;
                break;
            case ("SP"):
                t=Tipo.SP;
                break;
            case ("SS"):
                t=Tipo.SS;
                break;
            case ("DD"):
                t=Tipo.DD;
                break;
            case ("ST"):
                t=Tipo.ST;
                break;
            case ("LG"):
                t=Tipo.LG;
                break;
            case ("ST1"):
                t=Tipo.ST1;
                break;
            case ("ST2"):
                t=Tipo.ST2;
                break;

        }
        return t;
    }
    @Override
    public String toString() {
        return "LineParameter{" +
                "parametro='" + parametro + '\'' +
                ", tipo=" + tipo +
                ", valor='" + valor + '\'' +
                '}'+"\n";
    }
}

