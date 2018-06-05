package datalog_ra.evaluation;

import datalog_ra.base.TupleTransformation.*;
import datalog_ra.base.TupleTransformation.condition.*;
import datalog_ra.base.database.Database;
import datalog_ra.base.operator.*;
import datalog_ra.base.relation.Relation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Jakub
 */
public class Predicate {
    private final String name; 
    private LinkedList<SubTarget> positiveOrder= new LinkedList<>();
    private LinkedList<SubTarget> negativeOrder= new LinkedList<>();
    private Database positives = new Database();
    private Database negatives = new Database();
    private Operator operator;
    
    // transformation may be removed once bluildTransformation function is added
    private TupleTransformation transformation = new TrueCondition(); 
    private TupleTransformation AJCondition = new TrueCondition(); 
    private TupleTransformation positiveCondition = new TrueCondition(); 
    private TupleTransformation negativeCondition = new TrueCondition(); 
    
    public Predicate(String name) {
        this.name = name;
    }
    
    /**
     * Goes through all positive subtargets and sets their source to the
     * respective relations in database. If a relation is absent method returns 
     * false and it's effect is NOT reversed. Changing the content of database 
     * before the result is calculated results in undefined behavior.
     * @param database - the source database, should contain all relations
     * coresponding to positive subtargets
     * @return true if all predicates were updated correctly and false otherwise
     */
    public boolean updatePositives(Database database){
        Iterator<SubTarget> it = positiveOrder.iterator();
        for (int i = 0;it.hasNext(); i++) {
            String relation = it.next().name;
            Relation newRelation = database.get(relation);
            if (newRelation == null) {
                System.out.println("Unable to update relation \"" + relation 
                        + "\". Not present in the new database.");
                return false;
            }
            positives.replace(relation, newRelation);
        }
        return true;
    }
    
    /**
     * See udatePositives.
     */
    public boolean updateNegatives(Database database){
        Iterator<SubTarget> it = negativeOrder.iterator();
        for (int i = 0; it.hasNext(); i++) {
            String relation = it.next().name;
            Relation newRelation = database.get(relation);
            if (newRelation == null) {
                System.out.println("Unable to update relation \"" + relation 
                        + "\". Not present in the new database.");
                return false;
            }
            negatives.replace(relation, newRelation);
        }
        return true;
    }
    
    /**
     * Updates the internal operator used to calculate the result. Operator
     * contains relations in positive and negative database, so this should be
     * called allways after either of those is updated.
     */
    public void buildOperator(){
        Operator positive = buildPositiveJoin();
        Operator negative = buildNegativeJoin();
        if (negative == null) {
            operator = new Selection_Projection(positive, transformation);
        }
        else {
            operator = new Selection_Projection(
                    new AntiJoin(positive, negative, AJCondition)
                    , transformation);
        }
    }
    
    /**
     * Positive join is Join of all positive subtargets.
     */
    private Operator buildPositiveJoin(){
        Iterator<SubTarget> it = positiveOrder.iterator();
        if (!it.hasNext()) {
            System.out.println("Unable to build operator of " + name + ". "
                    + "0 SubTargets.");
            return null;
        }
        //no such elenemt exception is thrown if positives are empty 
        String relation = it.next().name;
        Operator result = positives.get(relation).operator(); 
        
        if (it.hasNext())
            while (true) {
                relation = it.next().name;
                if (it.hasNext())
                    result = new Join(result,
                            positives.get(relation).operator(),
                            new TrueCondition());
                else {
                    result = new Join(result,
                            positives.get(relation).operator(),
                            positiveCondition);
                    break;
                }
            }
        return result;
    }
    
    /**
     * Negative join is Join of all negative subtargets.
     */
    private Operator buildNegativeJoin(){
        Iterator<SubTarget> it = negativeOrder.iterator();
        
        if (!it.hasNext())
            return null;
        
        String relation = it.next().name;
        Operator result = negatives.get(relation).operator(); 
        
        while (it.hasNext()) {
            relation = it.next().name;
            if (it.hasNext())
            result = new Join(result, 
                    negatives.get(relation).operator(), new TrueCondition());
            else {
            result = new Join(result, 
                    negatives.get(relation).operator(), negativeCondition);
            break;
            }
        }
        return result;
    }
    
    /**
     * Calculates and returns the result of predicate and returns it as a 
     * relation.
     * @return Relation containing the result of predicate.
     */
    public Relation result(){
        Relation result = new Relation(operator);
        return result;
    }
    
    /*TO BE REPLACED WITH STRING PARSING [BEGIN]*/
    public void addPositive(String name, ArrayList<String> variables){
        positiveOrder.add(new SubTarget(name, variables));
        positives.add(name, new Relation());
    }
    public void addNegative(String name, ArrayList<String> variables){
        negativeOrder.add(new SubTarget(name, variables));
        negatives.add(name, new Relation());
    }
    public void setAJCondition(TupleTransformation tt) {
        AJCondition = tt;
    }
    public void setTransformation(TupleTransformation tt) {
        transformation = tt;
    }
    public void setPositiveCondition(TupleTransformation tt) {
        positiveCondition = tt;
    }
    public void setNegativeCondition(TupleTransformation tt) {
        negativeCondition = tt;
    }
    /*TO BE REPLACED WITH STRING PARSING [END]*/
    
    
    /**
     * A struct notaining all the necessary information about a subtarget.
     */
    private class SubTarget{
        public String name;
        public ArrayList<String> variables;
        public SubTarget(String name, ArrayList<String> variables) {
        this.name = name;
        this.variables = variables;
        }
    }
    
    public String getName() {
        return name;
    }
}

