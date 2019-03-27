package datalog_ra.evaluation;

import datalog_ra.base.TupleTransformation.*;
import datalog_ra.base.TupleTransformation.condition.*;
import datalog_ra.base.instance.Instance;
import datalog_ra.base.operator.*;
import datalog_ra.base.relation.Relation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Jakub
 */
public class Rule {

  private final String name;
  private LinkedList<Subgoal> positiveOrder = new LinkedList<>();
  private LinkedList<Subgoal> negativeOrder = new LinkedList<>();
  private Instance positiveFactSource = new Instance();
  private Instance negativeFactSource = new Instance();
  private Operator operator;

  private TupleTransformation projectionTransformation = new TrueCondition();
  private TupleTransformation joinCondition = new TrueCondition();
  private LinkedList<TupleTransformation> antijoinConditions = new LinkedList<>();

  public Rule(String name) {
    this.name = name;
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
      if (source.get(subgoal.name) == null) {
        System.out.println("Unable to update relation \"" + subgoal.name
                + "\". Not present in the new instance.");
        return false;
      }
      positiveFactSource.replace(subgoal.name, source.get(subgoal.name));
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
      if (source.get(subgoal.name) == null) {
        System.out.println("Unable to update relation \"" + subgoal.name
                + "\". Not present in the new instance.");
        return false;
      }
      negativeFactSource.replace(subgoal.name, source.get(subgoal.name));
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
      operator = new AntiJoin(
              operator,
              negativeFactSource.get(negativeOrder.get(i).name).operator(),
              antijoinConditions.get(i)
      );
    }

    operator = new Selection_Projection(operator, projectionTransformation);
  }

  /**
   * Positive join is Join of all positive subgoals.
   */
  private Operator buildJoin() {
    Iterator<Subgoal> it = positiveOrder.iterator();

    // begin with simple relation operator
    Operator result = positiveFactSource.get(it.next().name).operator();

    //make a cartesian product of all positive subgoals
    while (it.hasNext()) {
      result = new Join(
              result,
              positiveFactSource.get(it.next().name).operator(),
              new TrueCondition()
      );
    }

    //make a selection according to condition
    result = new Selection_Projection(result, joinCondition);

    return result;
  }

  /**
   * Calculates and returns the result of rule and returns it as a relation.
   */
  public Relation result() {
    Relation result = new Relation(operator);
    return result;
  }

  /* Temporary functions [BEGIN] 
       These functions are used for tests
       untill proper translation of Datalog is implemented */
  public void addPositiveSubgoal(String name, ArrayList<String> variables) {
    positiveOrder.add(new Subgoal(name, variables));
    positiveFactSource.add(name, new Relation());
  }

  public void addNegativeSubgoal(String name, ArrayList<String> variables) {
    negativeOrder.add(new Subgoal(name, variables));
    negativeFactSource.add(name, new Relation());
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

    public Subgoal(String name, ArrayList<String> variables) {
      this.name = name;
      this.variables = variables;
    }
  }

  public String getName() {
    return name;
  }
}
