public class Header {
    private String messageID; 
    private String flags; 
    private String response_code; 
    private String n_values; 
    private String n_authorities; 
    private String n_extravalues;
    
    
    /**
     * @param messageID
     * @param flags
     * @param response_code
     * @param n_values
     * @param n_authorities
     * @param n_extravalues
     */
    public Header(String messageID, String flags, String response_code, String n_values, String n_authorities, String n_extravalues) {
        this.messageID = messageID;
        this.flags = flags;
        this.response_code = response_code;
        this.n_values = n_values;
        this.n_authorities = n_authorities;
        this.n_extravalues = n_extravalues;
    }


    public String getMessageID() {
        return messageID;
    }


    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }


    public String getFlags() {
        return flags;
    }


    public void setFlags(String flags) {
        this.flags = flags;
    }


    public String getResponse_code() {
        return response_code;
    }


    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }


    public String getN_values() {
        return n_values;
    }


    public void setN_values(String n_values) {
        this.n_values = n_values;
    }


    public String getN_authorities() {
        return n_authorities;
    }


    public void setN_authorities(String n_authorities) {
        this.n_authorities = n_authorities;
    }


    public String getN_extravalues() {
        return n_extravalues;
    }


    public void setN_extravalues(String n_extravalues) {
        this.n_extravalues = n_extravalues;
    }


    @Override
    public String toString() {
        return "Header [messageID=" + messageID + ", flags=" + flags + ", response_code=" + response_code
                + ", n_values=" + n_values + ", n_authorities=" + n_authorities + ", n_extravalues=" + n_extravalues
                + "]";
    }

}
