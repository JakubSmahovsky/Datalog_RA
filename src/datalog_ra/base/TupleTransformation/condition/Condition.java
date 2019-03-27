package datalog_ra.base.TupleTransformation.condition;

import datalog_ra.base.TupleTransformation.TupleTransformation;
import datalog_ra.base.relation.Tuple;

public abstract class Condition implements TupleTransformation {

  @Override
  public Tuple transform(Tuple tuple) {
    if (eval(tuple)) {
      return tuple;
    } else {
      return null;
    }
  }

  boolean eval(Tuple tuple) {
    return true;
  }
}
