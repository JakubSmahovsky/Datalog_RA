package datalog_ra.evaluation;

import datalog_ra.base.dataStructures.Instance;
import datalog_ra.base.operator.*;
import datalog_ra.base.dataStructures.Relation;
import java.util.LinkedList;

/**
 * Class representing a datalog program with a query.
 * TODO: currently, calcullation can change EDB relations -> change?
 * @author Jakub
 */
public class Query {
  private Rule query;
  private Instance oldOldInstance, oldInstance, newInstance, inputInstance, wf;
  private LinkedList<Rule> rules;

  public Query(Instance inputInstance, int answerArity) {    
    this.inputInstance = inputInstance.copy();
    this.rules = new LinkedList();
    
    this.oldOldInstance = new Instance();
    this.oldInstance = new Instance();
    this.newInstance = inputInstance.copy();
  }

  /**
   * Performs the evaluation and returns the result of query
   */
  public Relation answer() {
    this.query.updatePositiveFactSource(wf);
    this.query.updateNegativeFactSource(wf);
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
    
    this.wf = new Instance();
    for (Relation r1 : newInstance) {
      String name = r1.getName();
      int arity = r1.getArity();
      Relation r2 = oldInstance.get(name, arity);
      
      /* 
       Make intersection of r1, r2. TODO: In relational algebra it is
       innerjoin[^1 = ^1, ..., ^arity = ^arity](r1|arity, r2|arity)
      */
      Intersection intersection = new Intersection(r1.operator(), r2.operator());
      
      // assign the resulting realtion to wf model instance
      this.wf.add(new Relation(intersection, name));
      System.out.println(name);
    }
    
    this.oldInstance = null;
    this.newInstance = null;
  }
  
  public void addRule(Rule rule) {
    rules.add(rule);
    newInstance.add(new Relation(rule.getName(), rule.getArity()));
    inputInstance.add(new Relation(rule.getName(), rule.getArity()));
  }
  
  public void defineQuery(Rule query) {
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
        p.updateNegativeFactSource(oldInstance);
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
        p.updatePositiveFactSource(newInstance);
        p.buildOperator();

        newInstance.append(p.result());
      }
    } while (!innerOldInstance.compareTo(newInstance));
  }
}
