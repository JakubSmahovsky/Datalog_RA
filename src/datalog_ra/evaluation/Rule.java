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
  
  private final LinkedList<String> head = new LinkedList<>();
  private final LinkedList<Subgoal> positiveOrder = new LinkedList<>();
  private final LinkedList<Subgoal> negativeOrder = new LinkedList<>();
  private final LinkedList<TupleTransformation> inequalities = new LinkedList<>();
  
  private final Instance positiveFactSource = new Instance();
  private final Instance negativeFactSource = new Instance();
  
  private Operator operator;

  public Rule(String name, List<String> head) {
    this.name = name;
    this.arity = head.size();
    this.head.addAll(head);
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

    // make join of all positive subgoals
    operator = buildJoin();

    // add antijoin for each negative subgoal
    for (int i = 0; i < negativeOrder.size(); i++) {
      Subgoal subgoal = negativeOrder.get(i);
      operator = new AntiJoin(
          operator,
          negativeFactSource.get(
              subgoal.name,
              subgoal.variables.size()
          ).operator(),
          buildAntijoinCondition(negativeOrder.get(i))
      );
    }

    operator = new Projection(operator, buildProjectionTransformation());
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
    result = new Selection(result, buildJoinCondition());

    return result;
  }
  
  private TupleTransformation buildJoinCondition(){
    // create a row of all variables' occurrences in positive subgoals
    ArrayList<String> variables = new ArrayList<>();
    for (Subgoal subgoal : positiveOrder) {
      variables.addAll(subgoal.variables);
    }
    
    TransformationSequence result = new TransformationSequence();
    
    for (int i = 0; i < variables.size(); i++){
      // constants begin with lowercase
      // compare the position to this constant
      if (Character.isLowerCase(variables.get(i).charAt(0))) {
        result.add(new CompareConstantCondition(i, variables.get(i)));
        continue;
      }
      
      // otherwise it's an actual variable
      // compare it to the next occurrence if it exists
      for (int j = i+1; j < variables.size(); j++) {
        if (variables.get(i).compareTo(variables.get(j)) == 0){
          result.add(new CompareCondition(i, j));
          break;
        }
      }
    }
    
    // filter according to inequalities
    for (TupleTransformation cond : inequalities) {
      result.add(cond);
    }
    
    return result;
  }
  
  private TupleTransformation buildAntijoinCondition(Subgoal negativeSubgoal){
    // create a row of all variables' occurrences in positive subgoals
    ArrayList<String> variables = new ArrayList<>();
    for (Subgoal subgoal : positiveOrder) {
      variables.addAll(subgoal.variables);
    }
    
    TransformationSequence result = new TransformationSequence();
    
    for (int i = 0; i < negativeSubgoal.variables.size(); i++){
      // constants begin with lowercase
      // compare the position to this constant
      if (Character.isLowerCase(negativeSubgoal.variables.get(i).charAt(0))) {
        result.add(new CompareConstantCondition(variables.size() + i, variables.get(i)));
        continue;
      }
      
      // otherwise it's an actual variable
      // compare it to the first occurrence in positive subgoals
      for (int j = 0; j < variables.size(); j++) {
        if (negativeSubgoal.variables.get(i).compareTo(variables.get(j)) == 0){
          result.add(new CompareCondition(variables.size() + i, j));
          break;
        }
      }
    }
    
    return result;
  }
  
  private TupleTransformation buildProjectionTransformation(){
    // create a row of all variables' occurrences in positive subgoals
    ArrayList<String> variables = new ArrayList<>();
    for (Subgoal subgoal : positiveOrder) {
      variables.addAll(subgoal.variables);
    }
    
    LinkedList<Integer> indexes = new LinkedList<>();
    
    // for all variables in head
    for (int i = 0; i < head.size(); i++){
      // add index for first occurrence in positive subgoals
      for (int j = 0; j < variables.size(); j++) {
        if (head.get(i).compareTo(variables.get(j)) == 0){
          indexes.add(j);
          break;
        }
      }
    }
    
    return new ProjectionTransformation(indexes);
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

  public void addInequality(int pos1, int pos2) {
    inequalities.push(new NegateCondition(new CompareCondition(pos1, pos2)));
  }
  
  public void addInequality(int pos, String constant) {
    inequalities.push(new NegateCondition(
        new CompareConstantCondition(pos, constant)));
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
