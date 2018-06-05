package datalog_ra.base.TupleTransformation.condition;

import datalog_ra.base.relation.Tuple;

/**
 *
 * @author Jakub
 */
public class CompareCondition extends Condition{
    int pos1, pos2;
    
    public CompareCondition(int pos1, int pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }
    
    @Override
    boolean eval(Tuple tuple) {
        return tuple.get(pos1).compareTo(tuple.get(pos2));
    }
}
