package datalog_ra.Test;

import datalog_ra.base.TupleTransformation.ProjectionTransformation;
import datalog_ra.base.TupleTransformation.condition.CompareCondition;
import datalog_ra.base.TupleTransformation.condition.TrueCondition;
import datalog_ra.base.instance.Instance;
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
    super(source);
    Rule p = new Rule("answer");
    p.addPositiveSubgoal("m", new ArrayList());
    p.addNegativeSubgoal("r", new ArrayList());
    p.addAntijoinCondition(new CompareCondition(0, 1));
    p.setProjectionTransformation(
        new ProjectionTransformation(Arrays.asList(0))
    );
    addRule(p);

    Rule r1 = new Rule("r");
    r1.addPositiveSubgoal("answer", new ArrayList());
    r1.setProjectionTransformation(new TrueCondition());
    addRule(r1);
    Rule r2 = new Rule("r");
    r2.addPositiveSubgoal("s", new ArrayList());
    r2.setProjectionTransformation(new TrueCondition());
    addRule(r2);

    Rule s1 = new Rule("s");
    s1.addPositiveSubgoal("answer", new ArrayList());
    s1.setProjectionTransformation(new TrueCondition());
    addRule(s1);
    Rule s2 = new Rule("s");
    s2.addPositiveSubgoal("r", new ArrayList());
    s2.setProjectionTransformation(new TrueCondition());
    addRule(s2);

    System.out.println(answer().toString());
  }

}
