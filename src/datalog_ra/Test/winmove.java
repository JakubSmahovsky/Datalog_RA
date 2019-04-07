package datalog_ra.Test;

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
    // anwer(X):= move(X, Y), not answer(Y)
    Rule answer = new Rule("answer", Arrays.asList("X"));
    answer.addPositiveSubgoal("move", Arrays.asList("X", "Y"));
    answer.addNegativeSubgoal("answer", Arrays.asList("Y"));

    addRule(answer);
    System.out.println(answer());
  }
}
