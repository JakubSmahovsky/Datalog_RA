package datalog_ra.base.TupleTransformation.condition;

import datalog_ra.base.relation.Attribute;
import datalog_ra.base.relation.Tuple;

/**
 *
 * @author Jakub
 */
public class CompareConstantCondition extends Condition{
    int pos;
    Attribute value;
    
    
    public CompareConstantCondition(int pos, String value) {
        this.pos = pos;
        this.value = new Attribute(value);
    }
    
    @Override
    boolean eval(Tuple tuple) {
        return tuple.get(pos).compareTo(value);
    }
}