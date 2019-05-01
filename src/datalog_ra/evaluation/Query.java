package datalog_ra.evaluation;

import datalog_ra.base.TupleTransformation.InnerJoinTransformation;
import datalog_ra.base.TupleTransformation.condition.CompareCondition;
import datalog_ra.base.dataStructures.Instance;
import datalog_ra.base.operator.*;
import datalog_ra.base.dataStructures.Relation;
import java.util.LinkedList;

/**
 * Class representing a datalog program with a query.
 * @author Jakub
 */
public class Query {
  private Rule query;
  private final LinkedList<Rule> rules;
  private final Instance inputInstance;
  private Instance oldInstance, newInstance, oldOldInstance, wf;

  public Query(Instance inputInstance, int answerArity) {    
    this.inputInstance = inputInstance;
    this.rules = new LinkedList();
    
    this.oldOldInstance = new Instance();
    this.oldInstance = new Instance();
    this.newInstance = inputInstance.copy();
  }

  /**
   * Performs the evaluation and returns the result of query
   */
  public Relation answer() {
    this.query.setPositiveFactSource(wf);
    this.query.setNegativeFactSource(wf);
    query.buildOperator();
    
    return query.result();
  }
  
  /**
   * Finds the limits of instance sequences and create their intersections, 
   * which make up well founded model. After that reassing used instances,
   * so that they can be picked up by garbage-collection.
   */
  public void findWFModel() {
    outerCycle();
    
    this.oldOldInstance = null;
    
    // initialize wf as copy of input instance, since EDB relations don't change
    this.wf = inputInstance.copy(); 
    for (Relation r1 : newInstance) {
      // do not count with make intersections of EDB predicates
      if ((inputInstance.get(r1.getName(), r1.getArity()) != null)) {
        continue;
      }
      
      String name = r1.getName();
      int arity = r1.getArity();
      Relation r2 = oldInstance.get(name, arity);
      
      /* 
       Make intersection of r1, r2. To make intersection we apply
       innerjoin[^1 = ^1, ..., ^arity = ^arity](r1|arity, r2|arity)
      */
      LinkedList<CompareCondition> intersectionConditions= new LinkedList<>();
      for (int i = 0; i < arity; i++) {
        intersectionConditions.add(new CompareCondition(i, arity + i));
      }
      InnerJoinTransformation intersectionTransformation 
          = new InnerJoinTransformation(intersectionConditions);
      Join intersection = 
          new Join(r1.operator(), r2.operator(), intersectionTransformation);

      // assign the resulting realtion to wf model instance
      this.wf.add(new Relation(intersection, name));
    }
    
    this.oldInstance = null;
    this.newInstance = null;
  }
  
  /**
   * Adds a rule to program. 
   * The rule cannot define an EDB predicate in it's head.
   * @return true if rule was added and false if it wasn't
   */
  public boolean addRule(Rule rule) {
    if (inputInstance.get(rule.getName(), rule.getArity()) != null) {
      System.out.println("Attemplted to add rule " + rule.getName() + "|"
          + rule.getArity() + " defining EDB predicate");
      return false;
    }
    
    this.rules.add(rule);
    this.newInstance.add(new Relation(rule.getName(), rule.getArity()));
    return true;
  }
  
  public void setQuery(Rule query) {
    this.query = query;
  }

  /**
   * Function building sequence of instances until limits are found. Each new
   * instance is the result of gelfond-lifschitz-reduktion from the preceding
   * instance.
   */
  private void outerCycle() {
    while (!oldOldInstance.compareTo(newInstance)) {
      oldOldInstance = oldInstance;
      oldInstance = newInstance;
      newInstance = inputInstance.copy();

      for (Rule p : rules) { //update (fixate) negative subgoals
        p.setNegativeFactSource(oldInstance);
      }

      innerCycle();
    }
  }

  /**
   * Function building a single gelfond-lifschitz-reduktion. Program evaluation
   * is repeated with fixed negative facts untill a fixpoint is reached.
   */
  private void innerCycle() {
    Instance innerOldInstance;
    do {
      innerOldInstance = newInstance.copy();
      for (Rule p : rules) {
        p.setPositiveFactSource(newInstance);
        p.buildOperator();

        newInstance.append(p.result());
      }
    } while (!innerOldInstance.compareTo(newInstance));
  }
}
