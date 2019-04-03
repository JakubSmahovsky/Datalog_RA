package datalog_ra.base.TupleTransformation.condition;

import datalog_ra.base.dataStructures.Tuple;

/**
 *
 * @author Jakub
 */
public class NegateCondition extends Condition {
  private Condition condition;
  
  public NegateCondition(Condition condition) {
    this.condition = condition;
  }

  @Override
  boolean eval(Tuple tuple) {
    return !condition.eval(tuple);
  }
}
