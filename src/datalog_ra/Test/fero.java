package datalog_ra.Test;

import datalog_ra.base.TupleTransformation.*;
import datalog_ra.base.TupleTransformation.condition.*;
import datalog_ra.base.database.Database;
import datalog_ra.evaluation.Predicate;
import datalog_ra.evaluation.Query;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Krcmy, kde fero pil pivo.
 * @author Jakub
 */
public class fero extends Query{
    public fero(Database source) {
        super(source);
        Predicate ans = new Predicate("answer");
        
        ans.addPositive("navstivil", new ArrayList());
        ans.addPositive("vypil", new ArrayList());
        
        TupleTransformation fero = new CompareConstantCondition(1, "Fero");
        TupleTransformation pivo = new CompareConstantCondition(4, "pivo");
        TupleTransformation id = new CompareCondition(0, 3);
        TupleTransformation joincond = new TransformationSequence( 
                Arrays.asList(fero, pivo, id));
        ans.setPositiveCondition(joincond);
        
        TupleTransformation proj = new ProjectionTransformation(
                Arrays.asList(false, false, true, false, false, false));
        ans.setTransformation(proj);
        
        addPredicate(ans);
        System.out.println(answer().toString());
    }
}
