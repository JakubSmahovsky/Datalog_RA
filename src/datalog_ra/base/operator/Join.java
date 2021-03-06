package datalog_ra.base.operator;

import datalog_ra.base.TupleTransformation.TupleTransformation;
import datalog_ra.base.dataStructures.Tuple;

/* Join operator returns merged tuples from o1 and o2 if they meet the
 * requirements set by tupleTrans.transform() function.
 */
public class Join implements Operator {

  private final Operator o1, o2;
  private final TupleTransformation tupleTrans;
  //t1 holds the tuple from o1 which is currently being joined with tuples from o2
  private Tuple tuple1;
  //found contains number of tuples returned by nonDistinctNext

  public Join(Operator operator1, Operator operator2, TupleTransformation tupleTrans) {
    o1 = operator1;
    o2 = operator2;
    this.tupleTrans = tupleTrans;
    tuple1 = o1.next();
  }

  /**
   * Returns the next tuple that fills the requirements set in
   * tuple transformation.
   */
  @Override
  public Tuple nonDistinctNext() {
    //function takes tuples from o2, merges them with t1 from o1 and
    //evaluates them, if it finds a fitting Tuple, returns the result of merge;
    while (tuple1 != null) {
      Tuple tuple2 = o2.nonDistinctNext();

      while (tuple2 != null) {
        Tuple result = tupleTrans.transform(new Tuple(tuple1, tuple2));
        if (result != null) {
          return result;
        }
        tuple2 = o2.nonDistinctNext();
      }

      //if o2 runs out of Tupes, new t1 is taken from o1 and o2 is reset
      tuple1 = o1.nonDistinctNext();
      o2.reset();
    }
    //if o1 runs out of tuples (t1 == null), the operator is at the end
    return null;
  }

  /**
   * Returns the next tuple that fills the requirements set in
   * tuple transformation.
   */
  @Override
  public Tuple next() {
    return nonDistinctNext();
  }

  @Override
  public void reset() {
    o1.reset();
    o2.reset();
    tuple1 = o1.next();
  }

  @Override
  public Operator instance() {
    Join result = new Join(o1.instance(), o2.instance(), tupleTrans);
    result.reset();
    return result;
  }
}
