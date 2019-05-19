package datalog_ra.base.operator;

import datalog_ra.base.dataStructures.Tuple;

public interface Operator {
  /**
   * Returns the next Tuple in the operator result returns null at the
   * end of the result
   */
  public Tuple next();

  Tuple nonDistinctNext();

  public void reset();

  /**
   * Creates a copy of current operator, it is automatically reset.
   */
  public Operator instance();
}
