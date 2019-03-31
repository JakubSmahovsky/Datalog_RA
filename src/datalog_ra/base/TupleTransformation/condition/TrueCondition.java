package datalog_ra.base.TupleTransformation.condition;

import datalog_ra.base.dataStructures.Tuple;

/**
 *
 * @author Jakub
 */
public class TrueCondition extends Condition {

  public TrueCondition() {
  }

  @Override
  boolean eval(Tuple tuple) {
    return true;
  }
}
