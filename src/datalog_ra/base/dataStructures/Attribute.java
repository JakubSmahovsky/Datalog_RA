package datalog_ra.base.dataStructures;

public class Attribute {

  private final String value;

  public Attribute(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public boolean compareTo(Attribute attribute) {
    return (value.compareTo(attribute.getValue()) == 0);
  }

  @Override
  public String toString() {
    return value;
  }

  public Attribute copy() {
    return new Attribute(value);
  }
}
