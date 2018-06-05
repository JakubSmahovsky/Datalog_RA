package datalog_ra.base.operator;

import datalog_ra.base.relation.Tuple;

/**
 *
 * @author Jakub
 */
/** Iterates through o1 and o2 and returns tuples which appear in both.
*/
public class Intersection implements Operator{
    Operator o1, o2;
    
    public Intersection(Operator o1, Operator o2){
        this.o1 = o1;
        this.o2 = o2;
    }

    @Override
    public Tuple next() {
        return nonDistinctNext();
    }
    
    /** Returns the next respective tuple of operator Intersection.
     */
    @Override
    public Tuple nonDistinctNext() {
        for (Tuple tuple1 = o1.next();tuple1 != null; tuple1 = o1.next()) {
            o2.reset();
            for (Tuple tuple2 = o2.next(); tuple2 != null; tuple2 = o2.next())
                if (tuple1.compareTo(tuple2))
                    return tuple1;
        }
        return null;
    }

    @Override
    public void reset() {
        o1.reset();
        o2.reset();
    }

    @Override
    public Operator instance() {
        Intersection result = new Intersection(o1, o2);
        result.reset();
        return result;
    }
    
}
