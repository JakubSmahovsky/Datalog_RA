package datalog_ra.base.TupleTransformation;

import datalog_ra.base.relation.Tuple;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jakub
 */
public class TransformationSequence implements TupleTransformation{
    private final List<TupleTransformation> transformation;
    
    public TransformationSequence(TupleTransformation transformation1, TupleTransformation  transformation2){
        transformation = new LinkedList();
        transformation.add(transformation1);
        transformation.add(transformation2);
    }
    
    public TransformationSequence(List<TupleTransformation> transformation){
        this.transformation = transformation;
    }
            
    @Override
    public Tuple transform(Tuple tuple) {
        Tuple result = tuple;
        for (TupleTransformation tupleTrans : transformation) {
                result = tupleTrans.transform(result);
                if (result == null) 
                    break; 
            }
        return result;
    }
}
