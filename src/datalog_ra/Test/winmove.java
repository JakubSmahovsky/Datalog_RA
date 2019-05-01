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
    // win(X):= move(X, Y), not win(Y)
    Rule win = new Rule("win", Arrays.asList("X"));
    win.addPositiveSubgoal("move", Arrays.asList("X", "Y"));
    win.addNegativeSubgoal("win", Arrays.asList("Y"));
    addRule(win);
    
    // ?win(X).
    Rule query = new Rule("win", Arrays.asList("X"));
    query.addPositiveSubgoal("win", Arrays.asList("X"));
    setQuery(query);    
    
    findWFModel();
    System.out.println(answer());
  }
}
