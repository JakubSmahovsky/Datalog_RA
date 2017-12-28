package datalog_ra.base.relation;

import java.util.ArrayList;
import java.util.Iterator;
import datalog_ra.base.operator.Operator;

public class Relation{
    private final ArrayList<Tuple> tuples;
    
    public Relation() {
        tuples = new ArrayList<>();
    }
    
    /* If newTuple is unique in this relation adds newTuple to tuples.
     * Does not add redundant tuples!    
     */
    public void add(Tuple newTuple) {
        for (Tuple t : tuples) 
            if (t.subsumed(newTuple)) return;
        tuples.add(newTuple);
    }
       
    @Override
    public String toString() {
        String result = "";
        for(Tuple tuple: tuples){
            result += tuple.toString() + "";
        }
        
        return result;
    }
    
    /* returns an operator that can iterate through this relation
     * two operators are indemendent
     */
    public Operator operator(){
        return new RelationOperator(this);
    }
    
    /* Function is used in private class RelationOperator.
     * Access to tuples.iterator should stay private not to alow tuple removal!
     */
    private Iterator<Tuple> iterator(){
        return tuples.iterator();
    }
    
    /* Operator of a relation. Similar to iterator but has no remove() function.
    */
    private class RelationOperator implements Operator{
        Iterator<Tuple> it;
        private final Relation parent;
        public RelationOperator(Relation r){
            parent = r;
            it = r.iterator();
        }
        
        @Override
        public Tuple next(){
            if (it.hasNext())
                return it.next();
            else return null;
        }

        @Override
        public void reset() {
            it = parent.iterator();
        }

        @Override
        public Operator instance() {
            return new RelationOperator(parent);
        }

        @Override
        public Tuple nonDistinctNext() {
            return next();
        }
    }
}

