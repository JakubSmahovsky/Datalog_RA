package datalog_ra.base.operator;

import datalog_ra.base.TupleTransformation.TupleTransformation;
import datalog_ra.base.dataStructures.Tuple;

public class Selection implements Operator {

  private final Operator operator;
  private final TupleTransformation tupleTrans;

  public Selection(Operator operator, TupleTransformation tupleTrans) {
    this.operator = operator;
    this.tupleTrans = tupleTrans;
  }

  @Override
  public Tuple next() {
    return this.nonDistinctNext();
  }

  @Override
  public Operator instance() {
    Selection result = new Selection(operator.instance(), tupleTrans);
    result.reset();
    return result;
  }

  /**
   * Returns the next tuple from input operator that suits the condition in 
   * the input TupleTranformation.
   */
  @Override
  public Tuple nonDistinctNext() {
    Tuple tuple = operator.nonDistinctNext();
    while (tuple != null) {
      if (tupleTrans.transform(tuple) != null) {
        return tuple;
      }
      tuple = operator.nonDistinctNext();
    }
    return null;
  }

  @Override
  public void reset() {
    operator.reset();
  }
}
