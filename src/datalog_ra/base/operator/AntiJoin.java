package datalog_ra.base.operator;

import datalog_ra.base.TupleTransformation.TupleTransformation;
import datalog_ra.base.dataStructures.Tuple;

/**
 *
 * @author Jakub
 */
public class AntiJoin implements Operator {

  private final Operator o1, o2;
  private final TupleTransformation tupleTrans;
  //t1 holds the tuple from o1 which is currently being joined with tuples from o2
  private Tuple tuple1;
  //found contains number of tuples returned by nonDistinctNext

  public AntiJoin(Operator operator1, Operator operator2, TupleTransformation tupleTrans) {
    o1 = operator1;
    o2 = operator2;
    this.tupleTrans = tupleTrans;
  }

  /** 
   * Returns the next tuple in AntiJoin of o1 and o2 by 
   * conditions set in TupleTransformation.
   * Returns the result of o1.next() if transformation is not null
   * Does not return the result of transform()!
   * Generates and caries duplicates from o1 and o2 
   */
  @Override
  public Tuple nonDistinctNext() {
    tuple1 = o1.nonDistinctNext();
    while (tuple1 != null) {
      o2.reset();
      Tuple tuple2 = o2.nonDistinctNext();

      boolean condition = true;
      while (tuple2 != null) {
        Tuple result = new Tuple(tuple1, tuple2);
        if (tupleTrans.transform(result) != null) {
          condition = false;
          break;
        }
        tuple2 = o2.nonDistinctNext();
      }

      if (condition) {
        return tuple1;
      }
      //if o2 runs out of Tupes, new t1 is taken from o1 and o2 is reset
      tuple1 = o1.nonDistinctNext();
    }
    //if o1 runs out of tuples (t1 == null), the operator is at the end
    return null;
  }

  /** 
   * Returns the next tuple in AntiJoin of o1 and o2 by 
   * conditions set in TupleTransformation.
   * Returns the result of o1.next() if transformation is not null
   * Does not return the result of transform()!
   */
  @Override
  public Tuple next() {
    tuple1 = o1.next();
    while (tuple1 != null) {
      o2.reset();
      Tuple tuple2 = o2.next();

      boolean condition = true;
      while (tuple2 != null) {
        Tuple result = new Tuple(tuple1, tuple2);
        if (tupleTrans.transform(result) != null) {
          condition = false;
        }
        tuple2 = o2.next();
      }

      if (condition) {
        return tuple1;
      }
      //if o2 runs out of Tupes, new t1 is taken from o1 and o2 is reset
      tuple1 = o1.next();
    }
    //if o1 runs out of tuples (t1 == null), the operator is at the end
    return null;
  }

  @Override
  public void reset() {
    o1.reset();
    o2.reset();
  }

  @Override
  public Operator instance() {
    AntiJoin result = new AntiJoin(o1.instance(), o2.instance(), tupleTrans);
    result.reset();
    return result;
  }
}
