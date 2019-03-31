package datalog_ra.base.TupleTransformation;

import datalog_ra.base.dataStructures.Attribute;
import datalog_ra.base.dataStructures.Tuple;
import java.util.LinkedList;
import java.util.List;

/**
 * Transformation, that creates a new tuple containing attributes of source
 * tuple in order given by attributeOrder. Missing attributes are discarded.
 *
 * @author Jakub
 */
public class ProjectionTransformation implements TupleTransformation {

  private final List<Integer> attributeOrder;

  public ProjectionTransformation(List<Integer> attributeOrder) {
    this.attributeOrder = attributeOrder;
  }

  @Override
  public Tuple transform(Tuple tuple) {
    if (tuple == null) {
      return null;
    }

    LinkedList<Attribute> attribs = new LinkedList<>();

    for (Integer index : attributeOrder) {
      attribs.add(tuple.get(index));
    }

    return new Tuple(attribs);
  }

}
