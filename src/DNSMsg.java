import java.io.*;

public class DNSMsg implements Serializable {
    private Header header; 
    private Data data; 
    
 

    public DNSMsg(Header header, Data data) {
        this.data = data;
        this.header = header;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return header + ";" + data;
    }

    public byte[] getBytes(Object object) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
             objectOutputStream.writeObject(object);
             return byteArrayOutputStream.toByteArray();
        }
    }
}

