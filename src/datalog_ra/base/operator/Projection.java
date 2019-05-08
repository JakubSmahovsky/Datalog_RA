package datalog_ra.base.operator;

import datalog_ra.base.TupleTransformation.TupleTransformation;
import datalog_ra.base.dataStructures.Tuple;

public class Projection implements Operator {

  private final Operator operator;
  private long found;
  private final TupleTransformation tupleTrans;

  public Projection(Operator operator, TupleTransformation tupleTrans) {
    this.operator = operator;
    this.found = 0;
    this.tupleTrans = tupleTrans;
  }

  /** 
   * Returns the next tuple from input operator, that has been tranformed 
   * by the input TupleTransformation. Actively removes duplicates.
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
        if (old.subsums(current)) {
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
    Projection result = new Projection(operator.instance(), tupleTrans);
    result.reset();
    return result;
  }

  /** 
   * Returns the next tuple from input operator, that has been tranformed 
   * by the input TupleTransformation. May create new duplicates in result.
   */
  @Override
  public Tuple nonDistinctNext() {
    Tuple current = operator.nonDistinctNext();
    if (current != null) {
      found++;
      return tupleTrans.transform(current);
    } else {
      return null;
    }
  }

  @Override
  public void reset() {
    operator.reset();
    found = 0;
  }
}
