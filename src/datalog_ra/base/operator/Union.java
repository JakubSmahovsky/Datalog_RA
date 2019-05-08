package datalog_ra.base.operator;

import datalog_ra.base.dataStructures.Tuple;

/**
 *
 * @author Jakub Class union does not create new duplicates but may carry
 * duplicates from o1|o2
 */
public class Union implements Operator {

  private final Operator o1, o2;
  //half is true after all tuples from o1 have been returned
  private boolean half;

  public Union(Operator operator1, Operator operator2) {
    o1 = operator1;
    o2 = operator2;
    half = false;
  }

  @Override
  public void reset() {
    o1.reset();
    o2.reset();
    half = false;
  }

  @Override
  public Tuple next() {
    //while no return value was found, ends at the end of o2
    while (true) {
      if (half) {
        Tuple t2 = o2.next();
        if (t2 == null) {
          return null;
        }

        o1.reset();
        //duplicate check calls nonDistinctNext()
        Tuple t1 = o1.nonDistinctNext();
        boolean distinct = true;

        while (t1 != null) {
          if (t1.subsums(t2)) { //found duplicate
            distinct = false;
            break;
          }
          //duplicate check calls nonDistinctNext()
          t1 = o1.nonDistinctNext();
        }
        if (distinct) {//did not find duplicate
          return t2;
        }
      } else {
        Tuple t = o1.next();
        if (t != null) {
          return t;
        } else {
          half = true;
        }
      }
    }
  }

  @Override
  public Tuple nonDistinctNext() {
    if (half) {
      return o2.nonDistinctNext();
    } else {
      Tuple t = o1.nonDistinctNext();
      if (t != null) {
        return t;
      } else {
        half = true;
        return t; //equivalent return null
      }
    }
  }

  @Override
  public Operator instance() {
    Union result = new Union(o1.instance(), o2.instance());
    result.reset();
    return result;
  }
}
