package datalog_ra.evaluation;

import datalog_ra.base.TupleTransformation.*;
import datalog_ra.base.TupleTransformation.condition.*;
import datalog_ra.base.dataStructures.Instance;
import datalog_ra.base.operator.*;
import datalog_ra.base.dataStructures.Relation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jakub
 */
public class Rule {
  private final String name;
  private final int arity;
  
  private LinkedList<Subgoal> positiveOrder = new LinkedList<>();
  private LinkedList<Subgoal> negativeOrder = new LinkedList<>();
  private Instance positiveFactSource = new Instance();
  private Instance negativeFactSource = new Instance();
  private Operator operator;

  private TupleTransformation projectionTransformation = new TrueCondition();
  private TupleTransformation joinCondition = new TrueCondition();
  private LinkedList<TupleTransformation> antijoinConditions = new LinkedList<>();

  public Rule(String name, int arity) {
    this.name = name;
    this.arity = arity;
  }

  /**
   * Updates sources of facts for positive subgoals. If a relation is missing
   * from the provided Instance, this function stops and returns false
   * (TODO(JakubSmahovsky): change this behaviour)
   *
   * @param source - the source instance, should contain all relations
   * coresponding to positive subgoals
   * @return true if all predicates were updated correctly and false otherwise
   */
  public boolean updatePositiveFactSource(Instance source) {
    for (Subgoal subgoal : positiveOrder) {
      if (source.get(
          subgoal.name,
          subgoal.variables.size()
      ) == null) {
        System.out.println("Unable to update relation \"" + subgoal.name
                + "\". Not present in the new instance.");
        return false;
      }
      positiveFactSource.replace(source.get(
          subgoal.name,
          subgoal.variables.size()
      ));
    }
    return true;
  }

  /**
   * Updates sources of facts for negative subgoals. If a relation is missing
   * from the provided Instance, this function stops and returns false
   * (TODO(JakubSmahovsky): change this behaviour)
   *
   * @param source - the source instance, should contain all relations
   * coresponding to positive subgoals
   * @return true if all predicates were updated correctly and false otherwise
   */
  public boolean updateNegativeFactSource(Instance source) {
    for (Subgoal subgoal : negativeOrder) {
      if (source.get(
          subgoal.name,
          subgoal.variables.size()
      ) == null) {
        System.out.println("Unable to update relation \"" + subgoal.name
                + "\". Not present in the new instance.");
        return false;
      }
      negativeFactSource.replace(source.get(
          subgoal.name,
          subgoal.variables.size()
      ));
    }
    return true;
  }

  /**
   * Updates the internal operator used to calculate the result. Operator
   * contains relations in positive and negative fact source. Should be called
   * after either of fact sources is updated.
   */
  public void buildOperator() {
    // at least one positive subgoal should exist
    if (positiveOrder.size() == 0) {
      System.out.println("No positive subgoals in rule " + name);
    }
    // each negative subgoal should have a condition and vice versa
    if (negativeOrder.size() != antijoinConditions.size()) {
      System.out.println("Antijoin subgoals and conditions "
              + "don't match in rule " + name);
    }

    // make join of all positive subgoals
    operator = buildJoin();

    for (int i = 0; i < negativeOrder.size(); i++) {
      Subgoal subgoal = negativeOrder.get(i);
      operator = new AntiJoin(
          operator,
          negativeFactSource.get(
              subgoal.name,
              subgoal.variables.size()
          ).operator(),
          antijoinConditions.get(i)
      );
    }

    operator = new Projection(operator, projectionTransformation);
  }

  /**
   * Positive join is Join of all positive subgoals.
   */
  private Operator buildJoin() {
    Operator result = null;

    for (int i = 0; i < positiveOrder.size(); i ++) {
      Subgoal subgoal = positiveOrder.get(i);
      
      // begin with simple relation operator
      if (i == 0) {
        result = positiveFactSource.get(
            subgoal.name,
            subgoal.variables.size()
        ).operator();
      } 
      //make a cartesian product of all positive subgoals
      else { 
        result = new Join(
            result,
            positiveFactSource.get(
                subgoal.name,
                subgoal.variables.size()
            ).operator(),
            new TrueCondition()
        );
      }
    }
    
    //make a selection according to condition
    result = new Selection(result, joinCondition);

    return result;
  }

  /**
   * Calculates and returns the result of rule and returns it as a relation.
   */
  public Relation result() {
    Relation result = new Relation(operator, name);
    return result;
  }

  /* Temporary functions [BEGIN] 
       These functions are used for tests
       untill proper translation of Datalog is implemented */
  public void addPositiveSubgoal(String name, List<String> variables) {
    positiveOrder.add(new Subgoal(name, variables));
    positiveFactSource.add(new Relation(name, variables.size()));
  }

  public void addNegativeSubgoal(String name, List<String> variables) {
    negativeOrder.add(new Subgoal(name, variables));
    negativeFactSource.add(new Relation(name, variables.size()));
  }

  public void setJoinCondition(TupleTransformation tt) {
    joinCondition = tt;
  }

  public void addAntijoinCondition(TupleTransformation tt) {
    antijoinConditions.push(tt);
  }

  public void setProjectionTransformation(TupleTransformation tt) {
    projectionTransformation = tt;
  }

  /*Temporary functions [END]*/

  /**
   * A struct containing all the necessary information about a subgoal.
   */
  private class Subgoal {

    public String name;
    public ArrayList<String> variables;

    public Subgoal(String name, List<String> variables) {
      this.name = name;
      this.variables = new ArrayList<>();
      this.variables.addAll(variables);
    }
  }

  // getters 

    public String getName() {
      return name;
    }
    
    public int getArity() {
      return arity;
    }
}
