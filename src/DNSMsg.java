public class DNSMsg {
    private Header header; 
    private Data data; 
    
 

    public DNSMsg(Header header, Data data) {
        this.data = data;
        this.header = header;
    }



    @Override
    public String toString() {
        return "DNSMsg [header=" + header + ", data=" + data + "]";
    }




}
