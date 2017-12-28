package datalog_ra.base.relation;

public class Attribute {
    private final String value;

    public Attribute(String value) {
        this.value = value;
    }
    public String getValue(){
        return value;
    }
    
    public boolean equals(Attribute attribute){
        return this.value.equals(attribute.getValue());
    }
}