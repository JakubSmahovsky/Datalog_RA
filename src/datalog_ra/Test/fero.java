package datalog_ra.Test;

import datalog_ra.base.TupleTransformation.*;
import datalog_ra.base.TupleTransformation.condition.*;
import datalog_ra.base.instance.Instance;
import datalog_ra.evaluation.Rule;
import datalog_ra.evaluation.Query;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Krcmy, kde fero pil pivo.
 * @author Jakub
 */
public class fero extends Query{
    public fero(Instance source) {
        super(source);
        Rule ans = new Rule("answer");
        
        ans.addPositiveSubgoal("navstivil", new ArrayList());
        ans.addPositiveSubgoal("vypil", new ArrayList());
        
        TupleTransformation fero = new CompareConstantCondition(1, "Fero");
        TupleTransformation pivo = new CompareConstantCondition(4, "pivo");
        TupleTransformation id = new CompareCondition(0, 3);
        TupleTransformation joincond = new TransformationSequence( 
                Arrays.asList(fero, pivo, id));
        ans.setJoinCondition(joincond);
        
        TupleTransformation proj = new ProjectionTransformation(
                Arrays.asList(false, false, true, false, false, false));
        ans.setProjectionTransformation(proj);
        
        addRule(ans);
        System.out.println(answer().toString());
    }
}
