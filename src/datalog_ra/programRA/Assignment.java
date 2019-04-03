package datalog_ra.programRA;

import datalog_ra.base.TupleTransformation.*;
import datalog_ra.base.TupleTransformation.condition.*;
import datalog_ra.base.dataStructures.Instance;
import datalog_ra.base.dataStructures.Relation;
import datalog_ra.base.operator.*;
import static java.lang.Integer.parseInt;
import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author Jakub
 */
public class Assignment {
  private final String relationName;
  private final int relationArity;
  private final String body;
  private Operator operator;
  
  public Assignment(String assignemntString, Instance source) {
    String[] headBody = assignemntString.split(":=");
    if (headBody.length != 2) {
      System.out.println("Syntax error in assignement \"" 
          + assignemntString + "\"");
    }
    
    String[] nameArity = headBody[0].split("\\|");
    if (nameArity.length != 2) {
      System.out.println("Syntax error in head \"" + headBody[0] + "\"");
    }
    
    if (!isNumeric(nameArity[1])) {
      System.out.println("Syntax error non numeric arity in head \"" 
          + headBody[0] + "\"");
    }
    
    this.relationName = nameArity[0].trim();
    this.relationArity = parseInt(nameArity[1].trim());
    this.body = headBody[1];
  }
  
  public void rebuild(Instance source) {
    this.operator = build(new StringBuilder(body), source);
  }
  
  private Operator build(StringBuilder exp, Instance source) {
    String statement = chunk(exp);
    if (!Arrays.asList(',', '.', '[', '(', ')').contains(exp.charAt(0))) {
      System.out.println("Syntax error unexpected character " + exp.charAt(0));
      return null;
    }
    exp.delete(0, 1);
    
    if (statement.compareTo("union") == 0) {
      // expecting "operator,operator)"
      Operator o1 = build(exp, source);
      Operator o2 = build(exp, source);
      
      exp.delete(0, 1); // delete separating char after operator
      return new Union(o1, o2);
    }
    
    if (statement.compareTo("intersection") == 0) {
      // expecting "operator,operator)"
      Operator o1 = build(exp, source);
      Operator o2 = build(exp, source);
      
      exp.delete(0, 1); // delete separating char after operator
      return new Intersection(o1, o2);
    }
    
    if (statement.compareTo("selection") == 0) {
      // expecting "condition](operator)"
      TupleTransformation condition = buildCondition(exp);
      Operator o = build(exp, source);
      
      exp.delete(0, 1); // delete separating char after operator
      return new Selection(o, condition);
    }
    
    if (statement.compareTo("projection") == 0) {
      // expecting "condition](operator)"
      TupleTransformation condition = buildProjectionTT(exp);
      Operator o = build(exp, source);
      
      exp.delete(0, 1); // delete separating char after operator
      return new Projection(o, condition);
    }
    
    if (statement.compareTo("join") == 0) {
      // expecting "condition](operator, operator)"
      TupleTransformation condition = buildCondition(exp);
      Operator o1 = build(exp, source);
      Operator o2 = build(exp, source);
      
      exp.delete(0, 1); // delete separating char after operator
      return new Join(o1, o2, condition);
    }
    
    if (statement.compareTo("antijoin") == 0) {
      // expecting "condition](operator, operator)"
      TupleTransformation condition = buildCondition(exp);
      Operator o1 = build(exp, source);
      Operator o2 = build(exp, source);
      
      exp.delete(0, 1); // delete separating char after operator
      return new AntiJoin(o1, o2, condition);
    }
    
    // edb relation  
    String[] nameArity = statement.split("\\|");
    if (nameArity.length != 2) {
      System.out.println("Syntax error at statement \"" + statement + "\"");
      return null;
    }
    exp.delete(0, 1); // delete separating char after relation
    return source.get(nameArity[0], parseInt(nameArity[1])).operator();
  }

  private TupleTransformation buildProjectionTT(StringBuilder exp) {
    LinkedList<Integer> attributeOrder = new LinkedList<>();
    // expecting "int, int, ..., int"
    do {
      String attrNum = chunk(exp);
      if (!isNumeric(attrNum)) {
        System.out.println("Syntax error expected attribute index,"
            + " got \"" + attrNum + "\"");
        return null;
      }

      attributeOrder.add(parseInt(attrNum)-1); // -1 becasue indexing from 0
      
      // delete "," or "]"
      if (!Arrays.asList(',', ']').contains(exp.charAt(0))) {
        System.out.println("Syntax error unexpected character " + exp.charAt(0));
        return null;
      }
      exp.delete(0, 1);
    } while (exp.charAt(0) != '(');

    exp.delete(0, 1); // delete "("
    return new ProjectionTransformation(attributeOrder);
  }
  
  private TupleTransformation buildCondition(StringBuilder exp) {
    // TODO: don't use sequence for a single condition
    TransformationSequence tt = new TransformationSequence();
    // expecting conditions like "int=int" or "int=const" or "int!=int" ...
    do {
      String cond = chunk(exp);
      
      if (!cond.contains("=")) {
        System.out.println("Syntax error \"=\" expected in condition" + cond);
        return null;
      }
      
      String[] vals;
      if (cond.contains("!")) {
        vals = cond.split("!=");    
      } else {
        vals = cond.split("=");
      }
      
      if (vals.length != 2) {
        System.out.println("Syntax error "
            + "wrong number of comarisons in condition" + cond);
      }
      
      Condition condition;
      
      if (isNumeric(vals[1])) {
        // expecting "int = int" condition
        condition = new CompareCondition(
            parseInt(vals[0].trim())-1, //-1 because indexing from 0
            parseInt(vals[1].trim())-1 //-1 because indexing from 0
        ); 
      } else {
        // expecting "int = const" condition
        condition = new CompareConstantCondition(
            parseInt(vals[0].trim())-1, vals[1] //-1 because indexing from 0
        );
      }
      
      if (cond.contains("!")) {
        tt.add(new NegateCondition(condition));
      } else {
        tt.add(condition);
      }
      
      // delete "," or "]"
      if (!Arrays.asList(',', ']').contains(exp.charAt(0))) {
        System.out.println("Syntax error unexpected character " + exp.charAt(0));
        return null;
      }
      exp.delete(0, 1);
      
    } while (exp.charAt(0) != '(');
    
    exp.delete(0, 1); // delete "("
    return tt;
  }
  
  private String chunk(StringBuilder expression) {
    String statement = "";
    for (int i = 0; i < expression.length(); i++) {
      char c = expression.charAt(i);
      
      if (c == '[' || c == ']' || c == '(' || c== ')' || c == ',' || c == '.') {
      expression.delete(0, i);
        return statement.trim();
      }
      statement += c;
    }
    return null;
  }
  
  /**
   * There must be a better way. However this does PRECISELY what I want.
   * Returns true if parseInt() won't throw.
   */
  public static boolean isNumeric(String strNum) {
    try {
        int d = parseInt(strNum);
    } catch (NumberFormatException | NullPointerException nfe) {
        return false;
    }
    return true;
  }
  
  // getters
    public String getRelationName() {
      return relationName;
    }
        
    public int getRelationArity() {
      return relationArity;
    }
    
    public Operator getOperator() {
      return operator;
    }
}
