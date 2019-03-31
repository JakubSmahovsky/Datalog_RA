package datalog_ra.base.TupleTransformation;

import datalog_ra.base.dataStructures.Tuple;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jakub
 */
public class TransformationSequence implements TupleTransformation {

  private final List<TupleTransformation> transformation;

  public TransformationSequence() {
    transformation = new LinkedList();
  }

  public TransformationSequence(List<TupleTransformation> transformation) {
    this.transformation = new LinkedList(transformation);
  }

  public void add(TupleTransformation transformation) {
    this.transformation.add(transformation);
  }

  @Override
  public Tuple transform(Tuple tuple) {
    Tuple result = tuple;
    for (TupleTransformation tupleTrans : transformation) {
      result = tupleTrans.transform(result);
      if (result == null) {
        break;
      }
    }
    return result;
  }
}
