package datalog_ra.base.TupleTransformation;

import datalog_ra.base.relation.Attribute;
import datalog_ra.base.relation.Tuple;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * If net is shorter then Tuples in operator the missing booleans 
 * are deemed false, the remainder of Tuples will be discarded
 * @author Jakub
*/
public class ProjectionTransformation implements TupleTransformation{
    private final List<Boolean> net;
    
    public ProjectionTransformation(List<Boolean> net) {
        this.net = net;
    }
    
    @Override
    public Tuple transform(Tuple tuple) {
        if (tuple == null)
            return null; 
        LinkedList<Attribute> attribs= new LinkedList<>();
        Iterator<Boolean> index = net.iterator();
        for (Attribute a : tuple) {
            if (index.hasNext()) { 
                if (index.next()) {
                    attribs.add(a);
                }
            }
        }
        return new Tuple(attribs);
    }
    
}
