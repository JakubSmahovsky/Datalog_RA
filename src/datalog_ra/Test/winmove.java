package datalog_ra.Test;

import datalog_ra.base.TupleTransformation.ProjectionTransformation;
import datalog_ra.base.TupleTransformation.condition.CompareCondition;
import datalog_ra.base.instance.Instance;
import datalog_ra.evaluation.Rule;
import datalog_ra.evaluation.Query;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Jakub
 */
public class winmove extends Query{
    
    public winmove(Instance source) {
        super(source);
        Rule answer = new Rule("answer");
        answer.addPositiveSubgoal("move", new ArrayList()); //variables are not used yet
        answer.addNegativeSubgoal("answer", new ArrayList()); //---------//-----------
        answer.addAntijoinCondition(new CompareCondition(1, 2));
        answer.setProjectionTransformation(new ProjectionTransformation(
                Arrays.asList(true, false, false)));
        addRule(answer);
        System.out.println(answer().toString());
    }
}
