package datalog_ra.Test;

import datalog_ra.base.TupleTransformation.ProjectionTransformation;
import datalog_ra.base.TupleTransformation.condition.CompareCondition;
import datalog_ra.base.TupleTransformation.condition.TrueCondition;
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
    Rule p = new Rule("answer", 1);
    p.addPositiveSubgoal("m", new ArrayList());
    p.addNegativeSubgoal("r", new ArrayList());
    p.addAntijoinCondition(new CompareCondition(0, 1));
    p.setProjectionTransformation(
        new ProjectionTransformation(Arrays.asList(0))
    );
    addRule(p);

    Rule r1 = new Rule("r", 1);
    r1.addPositiveSubgoal("answer", Arrays.asList("X"));
    r1.setProjectionTransformation(new TrueCondition());
    addRule(r1);
    Rule r2 = new Rule("r", 1);
    r2.addPositiveSubgoal("s", Arrays.asList("X"));
    r2.setProjectionTransformation(new TrueCondition());
    addRule(r2);

    Rule s1 = new Rule("s", 1);
    s1.addPositiveSubgoal("answer", Arrays.asList("X"));
    s1.setProjectionTransformation(new TrueCondition());
    addRule(s1);
    Rule s2 = new Rule("s", 1);
    s2.addPositiveSubgoal("r", Arrays.asList("X"));
    s2.setProjectionTransformation(new TrueCondition());
    addRule(s2);

    System.out.println(answer());
  }

}
