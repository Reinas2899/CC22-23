import java.io.Serializable;

public class Qinfo implements Serializable {
    String name;
    String type_value;

    
    public Qinfo(String name, String type_value) {
        this.name = name;
        this.type_value = type_value;
    }

    public Qinfo(Qinfo q){
        this.name = q.getName();
        this.type_value = q.getType_value();
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getType_value() {
        return type_value;
    }


    public void setType_value(String type_value) {
        this.type_value = type_value;
    }


    @Override
    public String toString() {
        return name + "," + type_value + ";";
    }

    public Qinfo clone(){
        return new Qinfo(this);
    }


}

