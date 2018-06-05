package datalog_ra.Test;

import datalog_ra.base.TupleTransformation.ProjectionTransformation;
import datalog_ra.base.TupleTransformation.condition.CompareCondition;
import datalog_ra.base.database.Database;
import datalog_ra.evaluation.Predicate;
import datalog_ra.evaluation.Query;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Jakub
 */
public class winmove extends Query{
    
    public winmove(Database source) {
        super(source);
        Predicate answer = new Predicate("answer");
        answer.addPositive("move", new ArrayList()); //variables are not used yet
        answer.addNegative("answer", new ArrayList()); //---------//-----------
        answer.setAJCondition(new CompareCondition(1, 2));
        answer.setTransformation(new ProjectionTransformation(
                Arrays.asList(true, false, false)));
        addPredicate(answer);
        System.out.println(answer().toString());
    }
}
