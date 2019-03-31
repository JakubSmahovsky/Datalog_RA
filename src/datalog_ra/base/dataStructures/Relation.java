package datalog_ra.base.dataStructures;

import java.util.ArrayList;
import java.util.Iterator;
import datalog_ra.base.operator.Operator;

public class Relation {
  private final String name;
  private int arity = 0;
  private final ArrayList<Tuple> tuples;

  public Relation(String name, int arity) {
    tuples = new ArrayList<>();
    this.name = name;
    this.arity = arity;
  }
  
  /**
   * Materializes Operator o. Creates a Relation out of tuples remaining in the
   * result of Operator o. Does not reset Operator o before nor after execution.
   */
  public Relation(Operator o, String name) {
    tuples = new ArrayList<>();
    Tuple t = o.next();
    
    if (t != null) {
      arity = t.size();
    }     
    
    while (t != null) {
      tuples.add(t);
      t = o.next();
    }
    
    this.name = name;
  }
  
  /**
   * Loads relation from a string of values.
   * @param relationString should be of form relation_name/arity:tuplevalues
   *  tuple values are separated by semicolon
   */
  public Relation(String relationString) {
    tuples = new ArrayList<>();
    
    if (relationString.indexOf("/") == -1) {
      System.out.println("\"Syntax error: \"/\" expected");
    }
    
    if (relationString.indexOf(":") == -1) {
      System.out.println("\"Syntax error: \":\" expected");
    }
    
    name = relationString.substring(0, relationString.indexOf("/")).trim();
    arity = Integer.parseInt(
        relationString.substring(
            relationString.indexOf("/") +1,
            relationString.indexOf(":")
        ).trim()
    );
    
    String[] tuplesStrings = relationString.split(";");
    
    for (int i = 1; i < tuplesStrings.length; i++) {
      String[] attributesStrings = tuplesStrings[i].split(",");
      
      ArrayList<Attribute> attributes = new ArrayList<>();
      for (String attribString : attributesStrings) {
        String attribValue = attribString.trim().replaceAll("\\(|\\)", "");
        
        attributes.add(new Attribute(attribValue));
      }
      tuples.add(new Tuple(attributes));
    }
  }

  /* If newTuple is unique in this relation adds newTuple to tuples.
     * Does not add redundant tuples!    
   */
  public void add(Tuple newTuple) {
    for (Tuple t : tuples) {
      if (t.subsumed(newTuple)) {
        return;
      }
    }
    tuples.add(newTuple);
  }

  @Override
  public String toString() {
    String result = name + "\n";
    for (Iterator<Tuple> it = tuples.iterator(); it.hasNext();) {
      result += it.next().toString();
      if (it.hasNext()) {
        result += "\n";
      }
    }

    return result;
  }

  /* returns an operator that can iterate through this relation
     * two operators are indemendent
   */
  public Operator operator() {
    return new RelationOperator(this);
  }

  /* Function is used in private class RelationOperator.
     * Access to tuples.iterator should stay private not to alow tuple removal!
   */
  private Iterator<Tuple> iterator() {
    return tuples.iterator();
  }

  public Relation copy() {
    Relation result = new Relation(name, arity);
    for (Tuple t : tuples) {
      result.add(t.copy());
    }
    return result;
  }

  public boolean compareTo(Relation relation) {
    if (this.size() != relation.size()) {
      return false;
    }
    Operator op = relation.operator();
    for (Tuple t = op.next(); t != null; t = op.next()) {
      if (!this.contains(t)) {
        return false;
      }
    }
    return true;
  }

  public int size() {
    return tuples.size();
  }

  public boolean contains(Tuple t) {
    boolean result = false;
    for (Tuple my : tuples) {
      if (my.compareTo(t)) {
        result = true;
      }
    }
    return result;
  }

  // getters
    public String getName() {
      return name;
    }
    
    public int getArity() {
      return arity;
    }
  
  /**
   * Operator of a relation. 
   * Similar to iterator but has no remove() function.
   */
  private class RelationOperator implements Operator {

    Iterator<Tuple> it;
    private final Relation parent;

    public RelationOperator(Relation r) {
      parent = r;
      it = r.iterator();
    }

    @Override
    public Tuple next() {
      if (it.hasNext()) {
        return it.next();
      } else {
        return null;
      }
    }

    @Override
    public void reset() {
      it = parent.iterator();
    }

    @Override
    public Operator instance() {
      return new RelationOperator(parent);
    }

    @Override
    public Tuple nonDistinctNext() {
      return next();
    }

    @Override
    public String toString() {
      return parent.toString();
    }
  }
}
