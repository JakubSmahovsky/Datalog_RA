package datalog_ra.base.operator;

import datalog_ra.base.relation.Tuple;

public interface Operator {
    public Tuple next();   
    Tuple nonDistinctNext();
    /*instance() returns new copy of current operator, it is automatically reset!
     */
    public void reset();
    public Operator instance();
}
