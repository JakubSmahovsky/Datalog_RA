package datalog_ra.Test;

import datalog_ra.base.dataStructures.Instance;
import datalog_ra.evaluation.Rule;
import datalog_ra.evaluation.Query;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Jakub
 */
public class wellf extends Query {

  public wellf(Instance source) {
    super(source, 1);
    // answer(X):= m(X), not r(X)
    Rule a = new Rule("answer", Arrays.asList("X"));
    a.addPositiveSubgoal("m", new ArrayList());
    a.addNegativeSubgoal("r", new ArrayList());
    addRule(a);

    // r(X):= answer(X)
    // r(X):= s(X)
    Rule r1 = new Rule("r", Arrays.asList("X"));
    r1.addPositiveSubgoal("answer", Arrays.asList("X"));
    addRule(r1);
    Rule r2 = new Rule("r", Arrays.asList("X"));
    r2.addPositiveSubgoal("s", Arrays.asList("X"));
    addRule(r2);

    // s(X):= answer(X)
    // s(X):= r(X)
    Rule s1 = new Rule("s", Arrays.asList("X"));
    s1.addPositiveSubgoal("answer", Arrays.asList("X"));
    addRule(s1);
    Rule s2 = new Rule("s", Arrays.asList("X"));
    s2.addPositiveSubgoal("r", Arrays.asList("X"));
    addRule(s2);

    setQuery(a);
    findWFModel();
    System.out.println(answer());
  }

}
