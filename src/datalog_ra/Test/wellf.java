package datalog_ra.Test;

import datalog_ra.base.TupleTransformation.ProjectionTransformation;
import datalog_ra.base.TupleTransformation.condition.CompareCondition;
import datalog_ra.base.TupleTransformation.condition.TrueCondition;
import datalog_ra.base.database.Database;
import datalog_ra.evaluation.Predicate;
import datalog_ra.evaluation.Query;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Jakub
 */
public class wellf extends Query{

    public wellf(Database source) {
        super(source);
        Predicate p = new Predicate("answer");
        p.addPositive("m", new ArrayList());
        p.addNegative("r", new ArrayList());
        p.setAJCondition(new CompareCondition(0, 1));
        p.setTransformation(new ProjectionTransformation(Arrays.asList(true, false)));
        addPredicate(p);
        
        Predicate r1 = new Predicate("r");
        r1.addPositive("answer", new ArrayList());
        r1.setTransformation(new TrueCondition());
        addPredicate(r1);
        Predicate r2 = new Predicate("r");
        r2.addPositive("s", new ArrayList());
        r2.setTransformation(new TrueCondition());
        addPredicate(r2);
        
        Predicate s1 = new Predicate("s");
        s1.addPositive("answer", new ArrayList());
        s1.setTransformation(new TrueCondition());
        addPredicate(s1);
        Predicate s2 = new Predicate("s");
        s2.addPositive("r", new ArrayList());
        s2.setTransformation(new TrueCondition());
        addPredicate(s2);
        
        System.out.println(answer().toString());
    }
    
}
