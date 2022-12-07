import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Data implements Serializable {
    Qinfo qinfo;

    List<String> resp_values;
    List<String> authorties_values;
    List<String> ext_values;


    public Data(Qinfo qinfo){

        this.qinfo = qinfo;
    }

    public Data(Qinfo qinfo, List<String> resp_values, List<String> authorties_values, List<String> ext_values) {
        this.qinfo = qinfo;
        this.resp_values = resp_values;
        this.authorties_values = authorties_values;
        this.ext_values = ext_values;
    }

    public Data(Data d){
        this.qinfo = d.getQinfo();
        this.ext_values = d.getExt_values();
        this.resp_values = d.getResp_values();
        this.authorties_values = d.getAuthorties_values();
    }

    public Qinfo getQinfo() {
        return qinfo;
    }


    public void setQinfo(Qinfo qinfo) {
        this.qinfo = qinfo;
    }


    public List<String> getResp_values() {
        return resp_values;
    }


    public void setResp_values(List<String> resp_values) {
        this.resp_values = new ArrayList<>(resp_values);
    }


    public List<String> getAuthorties_values() {
        return authorties_values;
    }


    public void setAuthorties_values(List<String> authorties_values) {
        this.authorties_values = new ArrayList<>(authorties_values);
    }


    public List<String> getExt_values() {
        return ext_values;
    }


    public void setExt_values(List<String> ext_values) {
        this.ext_values = ext_values;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int i=0;
        if(resp_values!=null) {
            for (String s : resp_values) {
                if (i == resp_values.size() - 1) stringBuilder.append(s).append(";").append("\n");
                else stringBuilder.append(s).append(",").append("\n");
                i++;
            }
        }
        i=0;
        if(authorties_values!=null) {
            for (String s : authorties_values) {
                if (i == authorties_values.size() - 1) stringBuilder.append(s).append(";").append("\n");
                else stringBuilder.append(s).append(",").append("\n");
                i++;
            }
        }
        i=0;
        if(ext_values!=null) {
            for (String s : ext_values) {
                if (i == ext_values.size() - 1) stringBuilder.append(s).append(";");
                else stringBuilder.append(s).append(",").append("\n");
                i++;
            }
        }
        return  qinfo + "\n" + stringBuilder;
    }

    public Data clone(){
        return new Data(this);
    }
}

