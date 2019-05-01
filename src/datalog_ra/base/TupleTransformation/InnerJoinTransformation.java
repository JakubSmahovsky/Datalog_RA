package datalog_ra.base.TupleTransformation;

import datalog_ra.base.TupleTransformation.condition.CompareCondition;
import datalog_ra.base.dataStructures.Attribute;
import datalog_ra.base.dataStructures.Tuple;
import java.util.LinkedList;
import java.util.List;

/**
 * Transformationt that performs CompareCondition checks and discards the
 * second attribute of each of these checks.
 *
 * @author Jakub
 */
public class InnerJoinTransformation implements TupleTransformation {
  private final LinkedList<Integer> toDiscard = new LinkedList<>();
  private final TransformationSequence comparisonSequence;
  
  /**
   * Creates a transformation performing compare conditions and discarding 
   * every second attribute in each comaprison. Indexes of attributes are 
   * the same as in non discarding sequence on compare conditions
   */
  public InnerJoinTransformation(List<CompareCondition> conditions) {   
    for (CompareCondition condition : conditions) {
      // keep toDiscard ordered, pre-processing is cheap
      boolean found = false;
      int i = 0;
      for (int elem : toDiscard) {
        if (elem >= condition.getPos2()) {
          found = true;
          // every discard only needs to be done once, remove excess now
          if (elem > condition.getPos2()) {
            toDiscard.add(i, condition.getPos2());
          }
          break;
        }
        i++;
      }
      if (!found) {
        toDiscard.add(condition.getPos2());
      }
    }
    
    this.comparisonSequence = new TransformationSequence(conditions);
  }

  @Override
  public Tuple transform(Tuple tuple) {
    if (comparisonSequence.transform(tuple) == null) {
      return null;
    }

    LinkedList<Attribute> attribs = new LinkedList<>();
    
    int i = 0;
    for (int discard : toDiscard) {
      while (i < discard) {
        // add
        attribs.add(tuple.get(i));
        i++;
      }
      // discard
      i++;
    }
    
    return new Tuple(attribs);
  }
}