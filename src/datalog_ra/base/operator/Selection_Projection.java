package datalog_ra.base.operator;

import datalog_ra.base.TupleTransformation.TupleTransformation;
import datalog_ra.base.relation.Tuple;

/**
 * If net is shorter then Tuples in operator the missing booleans are deemed
 * false, the remainder of Tuples will be discarded
 */
public class Selection_Projection implements Operator {

  private final Operator o;
  private long found;
  private final TupleTransformation tupleTrans;

  public Selection_Projection(Operator operator, TupleTransformation tupleTrans) {
    o = operator;
    found = 0;
    this.tupleTrans = tupleTrans;
  }

  /* Returns a Tuple from o that has been tranformed acording to the 
     * transformation specified in tupleTrans.
     * Does not create duplicates not cary duplicates over from o.
   */
  @Override
  public Tuple next() {
    Tuple current = this.nonDistinctNext();
    while (current != null) {
      Operator p = instance();
      Tuple old;
      boolean distinct = true;
      for (int i = 1; i < found; i++) {
        old = p.nonDistinctNext();
        if (old.subsumed(current)) {
          distinct = false;
          break;
        }
      }
      if (distinct) {
        return current;
      }
      current = this.nonDistinctNext();
    }
    return null;
  }

  @Override
  public Operator instance() {
    Selection_Projection result = new Selection_Projection(o.instance(), tupleTrans);
    result.reset();
    return result;
  }

  /* Returns a Tuple from o that has been tranformed acording to the 
     * transformation specified in tupleTrans.
     * Generates and caries duplicates from o1 and o2.
   */
  @Override
  public Tuple nonDistinctNext() {
    Tuple current = o.nonDistinctNext();
    while (current != null) {
      Tuple result = tupleTrans.transform(current);
      if (result != null) {
        found++;
        return result;
      }
      current = o.nonDistinctNext();
    }
    return null;
  }

  @Override
  public void reset() {
    o.reset();
    found = 0;
  }
}
