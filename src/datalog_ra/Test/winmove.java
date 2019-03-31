package datalog_ra.Test;

import datalog_ra.base.TupleTransformation.ProjectionTransformation;
import datalog_ra.base.TupleTransformation.condition.CompareCondition;
import datalog_ra.base.dataStructures.Instance;
import datalog_ra.evaluation.Rule;
import datalog_ra.evaluation.Query;
import java.util.Arrays;

/**
 *
 * @author Jakub
 */
public class winmove extends Query {
  
  public winmove(Instance source) {
    super(source, 1);
    Rule answer = new Rule("answer", 1);
    answer.addPositiveSubgoal("move", Arrays.asList("X", "Y"));
    answer.addNegativeSubgoal("answer", Arrays.asList("Y"));
    answer.addAntijoinCondition(new CompareCondition(1, 2));
    answer.setProjectionTransformation(new ProjectionTransformation(
        Arrays.asList(0)));
    addRule(answer);
    System.out.println(answer());
  }
}
