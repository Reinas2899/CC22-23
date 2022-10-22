public class Data {
    Qinfo qinfo;
    
    String resp_values;
    String authorties_values;
    String ext_values;


    public Data(Qinfo qinfo){
        this.qinfo = qinfo;
    }

    public Data(Qinfo qinfo, String resp_values, String authorties_values, String ext_values) {
        this.qinfo = qinfo;
        this.resp_values = resp_values;
        this.authorties_values = authorties_values;
        this.ext_values = ext_values;
    }


    public Qinfo getQinfo() {
        return qinfo;
    }


    public void setQinfo(Qinfo qinfo) {
        this.qinfo = qinfo;
    }


    public String getResp_values() {
        return resp_values;
    }


    public void setResp_values(String resp_values) {
        this.resp_values = resp_values;
    }


    public String getAuthorties_values() {
        return authorties_values;
    }


    public void setAuthorties_values(String authorties_values) {
        this.authorties_values = authorties_values;
    }


    public String getExt_values() {
        return ext_values;
    }


    public void setExt_values(String ext_values) {
        this.ext_values = ext_values;
    }


    @Override
    public String toString() {
        return "Data [qinfo=" + qinfo + ", resp_values=" + resp_values + ", authorties_values=" + authorties_values
                + ", ext_values=" + ext_values + "]";
    }

    
}

