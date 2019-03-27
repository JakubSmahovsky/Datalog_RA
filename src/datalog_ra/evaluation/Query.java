package datalog_ra.evaluation;

import datalog_ra.base.instance.Instance;
import datalog_ra.base.operator.*;
import datalog_ra.base.relation.Relation;
import java.util.LinkedList;

/**
 *
 * @author Jakub
 */
public class Query {

  private Instance oldOldInstance, oldInstance, newInstance, inputInstance;
  private LinkedList<Rule> rules;

  public Query(Instance inputInstance) {
    this.oldOldInstance = new Instance();
    this.oldInstance = new Instance();
    this.inputInstance = inputInstance.copy();
    this.newInstance = inputInstance.copy();
    this.rules = new LinkedList();
  }

  /**
   * Performs the evaluation and returns the relation named "answer"
   */
  public Relation answer() {
    System.out.println(oldOldInstance.toString());

    outerCycle();
    return buildAnswer();
  }

  public void addRule(Rule rule) {
    rules.add(rule);
    newInstance.add(rule.getName(), new Relation());
    inputInstance.add(rule.getName(), new Relation());
  }

  /**
   * Function building sequence of instances until limits are found. Each new
   * instance is the result of gelfond-lifschitz-reduktion from the preceding
   * instance.
   */
  private void outerCycle() {
    while (!oldOldInstance.compareTo(newInstance)) {
      oldOldInstance = oldInstance;
      oldInstance = newInstance.copy();
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

        newInstance.append(p.getName(), p.result());
      }
    } while (!innerOldInstance.compareTo(newInstance));
  }

  /**
   * Creates an intersection of the limit instances. The answer contains all
   * tuples that are true in the well-founded model.
   */
  private Relation buildAnswer() {
    return new Relation(new Intersection(
            newInstance.get("answer").operator(), oldInstance.get("answer").operator()));
  }
}
