package datalog_ra.base.relation;

import java.util.ArrayList;
import java.util.Iterator;
import datalog_ra.base.operator.Operator;

public class Relation {

  private final ArrayList<Tuple> tuples;

  public Relation() {
    tuples = new ArrayList<>();
  }

  /**
   * Materializes Operator o. Creates a Relation out of tuples remaining in the
   * result of Operator o. Does not reset Operator o before nor after execution.
   */
  public Relation(Operator o) {
    tuples = new ArrayList<>();
    Tuple t = o.next();
    while (t != null) {
      tuples.add(t);
      t = o.next();
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
    String result = "";
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
    Relation result = new Relation();
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

  /* Operator of a relation. Similar to iterator but has no remove() function.
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
