package datalog_ra.base.TupleTransformation;

import datalog_ra.base.relation.Tuple;

/**
 *
 * @author Jakub
 */
public interface TupleTransformation {
    public Tuple transform(Tuple tuple);
}
