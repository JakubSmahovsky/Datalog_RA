package datalog_ra.base.TupleTransformation.condition;

import datalog_ra.base.relation.Tuple;

/**
 *
 * @author Jakub
 */
public class AttributeContentCondition extends Condition {

  public AttributeContentCondition() {

  }

  @Override
  boolean eval(Tuple tuple) {
    return true;
  }

  private class Negated extends AttributeContentCondition {

    @Override
    public Tuple transform(Tuple tuple) {
      if (eval(tuple)) {
        return tuple;
      } else {
        return null;
      }
    }
  }
}
