import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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


    public byte[] getBytes(Object object) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
             objectOutputStream.writeObject(object);
             return byteArrayOutputStream.toByteArray();
        }
    }

}

