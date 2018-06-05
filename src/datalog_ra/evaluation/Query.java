package datalog_ra.evaluation;

import datalog_ra.base.database.Database;
import datalog_ra.base.operator.*;
import datalog_ra.base.relation.Relation;
import java.util.LinkedList;

/**
 *
 * @author Jakub
 */
public class Query {
    //in order: previous result, thre result before previous, current result
    //being built, source database
    private Database oldDB_minus2, oldDB_minus1, newDB, source;
    private LinkedList<Predicate> predicates;
    
    public Query(Database source){
        oldDB_minus2 = new Database();
        oldDB_minus1 = new Database();
        this.source = source.copy();
        newDB = source.copy();
        predicates = new LinkedList();
        
        for (String relation : source.getNames()) {
        }
    }
    
    /**
     * Performs the evaluation and returns the relation named "answer"
     */
    public Relation answer(){
        while(!oldDB_minus2.compareTo(newDB)){
            cycle();
        }
        return buildAnswer();
    }
    
    public void addPredicate(Predicate predicate){
        predicates.add(predicate);
        newDB.add(predicate.getName(), new Relation());
        source.add(predicate.getName(), new Relation());
    }
    
    private void cycle(){
        oldDB_minus2 = oldDB_minus1;
        oldDB_minus1 = newDB.copy();
        
        newDB = source.copy();
        
        for(Predicate p : predicates) { //update (fixate) negatives
            p.updateNegatives(oldDB_minus1);
        }

        Database innerOldDB;
        do { //inner cycle 
            innerOldDB = newDB.copy();
            for(Predicate p : predicates) {
                p.updatePositives(newDB);
                
                p.buildOperator();
                newDB.append(p.getName(),p.result());
            }
        }
        while(!innerOldDB.compareTo(newDB));
    }
    
    /**
     * Creates an intersection of the final databases and the resulting answer
     * only contains tuples that are true in the well-founded model.
     */
    private Relation buildAnswer() {
        return new Relation(new Intersection(
        newDB.get("answer").operator(), oldDB_minus1.get("answer").operator()));
    }
}
