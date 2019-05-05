package datalog_ra.programRA;
import datalog_ra.base.dataStructures.Instance;
import datalog_ra.base.dataStructures.Relation;
import datalog_ra.base.TupleTransformation.*;
import datalog_ra.base.TupleTransformation.condition.*;
import datalog_ra.base.operator.*;
import static java.lang.Integer.parseInt;
import java.util.LinkedList;

/**
 *
 * @author Jakub
 */
public class Assignment extends RASubprogram{
  private String code;
  private String relationName;
  private int pos;
  private final int bodyBegin;

  public Assignment(String sourceCode) {
    this.code = sourceCode;
    pos = 0;
    head();
    bodyBegin = pos;
  }
  
  @Override
  public void run(Instance target) {
    pos = bodyBegin;
    target.replace(new Relation(operator(target), relationName));
  }
  
  private boolean head() {
    int begin = pos;
    String name = symbol();
    
    if (name.length() == 1 && isSeparator(name.charAt(0))) {
      System.out.println("Syntax error, expected relation name at " 
          + sample(begin));
      return false;
    }
    
    // name seems ok
    this.relationName = name;
    expect(":");
    expect("=");
    
    return true;
  }
  
  private Operator operator(Instance source) {
    int begin = pos;
    String symbol = symbol();

    if (symbol.compareTo("antijoin") == 0) {
      // antijoin[cond](o1, o2)
      TupleTransformation cond = condition();
      expect("(");
      Operator o1 = operator(source);
      expect(",");
      Operator o2 = operator(source);
      expect(")");
      
      return new AntiJoin(o1, o2, cond);
    }
    
    if (symbol.compareTo("join") == 0) {
      // join[cond](o1, o2)
      TupleTransformation cond = condition();
      expect("(");
      Operator o1 = operator(source);
      expect(",");
      Operator o2 = operator(source);
      expect(")");
      
      return new Join(o1, o2, cond);
    }
    
    if (symbol.compareTo("projection") == 0) {
      // projection[trans](o)
      TupleTransformation trans = projectionTransformation();
      expect("(");
      Operator o = operator(source);
      expect(")");
      
      return new Projection(o, trans);
    }
    
    if (symbol.compareTo("selection") == 0) {
      // selection[cond](o)
      TupleTransformation cond = condition();
      expect("(");
      Operator o = operator(source);
      expect(")");
      
      return new Selection(o, cond);
    }
    
    if (symbol.compareTo("union") == 0) {
      // union(o1, o2)
      expect("(");
      Operator o1 = operator(source);
      expect(",");
      Operator o2 = operator(source);
      expect(")");
      
      return new Union(o1, o2);
    }
    // name|arity
    
    String name = symbol;
    expect("|");
    symbol = symbol();
    if (!isNumeric(symbol)) {
      System.out.println("Syntax error, expected relation arity after \"|\" at "
          + sample(begin));
      return null;
    }
    int arity = parseInt(symbol);
    
    return source.forceGet(name, arity).operator();
  }
  
  private TupleTransformation projectionTransformation() {
    int transformBegin = pos;
    expect("[");
    
    LinkedList<Integer> positions = new LinkedList<>();

    while(pos < code.length()) {
      int positionBegin = pos;
      expect("^");
      
      // read position number
      String numString = symbol();
      if (!isNumeric(numString)) {
        System.out.println("Syntax error, expected a numeric position, at "
            + sample(positionBegin));
        return null;
      }
      
      positions.add(parseInt(numString) -1); //index from 0;
      
      String separator = symbol();
      // end after "]"
      if (separator.length() == 1 && separator.charAt(0) == ']') {
        return new ProjectionTransformation(positions);
      }
      
      // continue after "," 
      if (separator.length() == 1 && separator.charAt(0) == ',') {
        continue;
      }
      
      // incorrect symbol
      System.out.println("Syntax error, expected \",\" or \"]\", got "
          + separator + " at " + sample(positionBegin));
      return null;
    }
    
    // no closing symbol found
    System.out.println("Syntax error, expected \"]\" after "
            + sample(transformBegin));
    return null;
  }
  
  private TupleTransformation condition() {
    int conditionBegin = pos;
    expect("[");
    
    TupleTransformation condition = null;
    TransformationSequence sequence = null;

    while(pos < code.length()) {
      int compareBegin = pos;
      Condition newCondition = null;
      boolean negate = false;
      
      // declaration for both compare constant and compare - only 2 are used
      Integer position1 = null;
      String constant = null;
      
      // read left side
      String symbol = symbol();
      if (symbol.length() == 1 && symbol.charAt(0) == '^') {
        // position
        String numString = symbol();
        if (!isNumeric(numString)) {
          System.out.println("Syntax error, expected a numeric position, at "
              + sample(compareBegin));
          return null;
        }
        
        position1 = parseInt(numString) -1; //index from 0
      } else {
        // constant
        if (!Character.isDigit(symbol.charAt(0)) 
            && !Character.isLowerCase(symbol.charAt(0))) {
          System.out.println("Syntax error, expeted position or constant at "
              + sample(compareBegin));
          return null;
        }
        
        constant = symbol;
      }
      
      // read equality mark
      int markBegin = pos;
      String mark = symbol();
      if (mark.length() == 1 && mark.charAt(0) == '!') {
        negate = true;
        mark = symbol();
      }
      
      if (mark.length() == 1 && mark.charAt(0) != '=') {
        System.out.println("Syntax error, expeted \"=\" at "
            + sample(markBegin));
        return null;
      }
      
      // read right side
      symbol = symbol();
      if (symbol.length() == 1 && symbol.charAt(0) == '^') {
        // position
        String numString = symbol();
        if (!isNumeric(numString)) {
          System.out.println("Syntax error, expected a numeric position, at "
              + sample(compareBegin));
          return null;
        }
        if (position1 == null) {
          newCondition = 
              new CompareConstantCondition(parseInt(numString) -1, constant);
        } else {
          newCondition = new CompareCondition(position1,parseInt(numString) -1);
        }
      } else {
        // constant
        if (!Character.isDigit(symbol.charAt(0)) 
            && !Character.isLowerCase(symbol.charAt(0))) {
          System.out.println("Syntax error, expeted position or constant at "
              + sample(compareBegin));
          return null;
        }
        
        if (constant == null) {
          newCondition = new CompareConstantCondition(position1, symbol);
        } else {
          System.out.println("Syntax error, trying to compare 2 constants at "
              + sample(compareBegin));
        }
      }
      
      if (negate) {
        newCondition = new NegateCondition(newCondition);
      }
      
      // assign to result
      if (sequence != null) {
        sequence.add(newCondition);
      } else if (condition == null) {
        condition = newCondition;
      } else {
        // change to sequence
        sequence = new TransformationSequence();
        sequence.add(condition);
        sequence.add(newCondition);
        condition = null;
      }

      // read separator
      String separator = symbol();
      // end after "]"
      if (separator.length() == 1 && separator.charAt(0) == ']') {
        if (sequence != null) {
          return sequence;
        } else if (condition != null){
          return condition;
        } else {
          return new TrueCondition();
        }
      }
      
      // continue after "," 
      if (separator.length() == 1 && separator.charAt(0) == ',') {
        continue;
      }
      
      // incorrect symbol
      System.out.println("Syntax error, expected \",\" or \"]\", got "
          + separator + " at " + sample(compareBegin));
    }
    
    // no closing symbol found
    System.out.println("Syntax error, expected \"]\" after "
        + sample(conditionBegin));
    return null;
  }
  
  // helper methods
  
  private String symbol() {
    skipWhitespace();
    
    // try to match separators
    if (isSeparator(code.charAt(pos))) {
      String result = "" + code.charAt(pos);
      pos++; // char is read
      return result;
    }

    StringBuilder result = new StringBuilder();
    while(pos < code.length()) {
      // end after whitespace
      if (skipWhitespace()) {
        return result.toString();
      }
      
      // end after separator
      if (isSeparator(code.charAt(pos))) {
        // do not read separator (no pos++)
        return result.toString();
      }
      
      // add character
      result.append(code.charAt(pos));
      pos++; // char is read
    }
    
    return result.toString();
  }
  
  private boolean skipWhitespace() {
    boolean skipped = false;
    while ( pos < code.length() && (
            code.charAt(pos) == ' '  ||
            code.charAt(pos) == '\t' ||
            // newline... I think
            Character.hashCode(code.charAt(pos)) == 10 ||
            Character.hashCode(code.charAt(pos)) == 13
          )) {
      pos++;
      skipped = true;
    }
    return skipped;
  }
  
  private boolean expect(String symbol) {
    int begin = pos;
    if (symbol().compareTo(symbol) != 0) {
      System.out.println("Syntax error, expected \"" + symbol 
          + "\" at " + sample(begin));
      return false;
    }
    return true;
  }
  
  private String sample(int pos) {
    if (code.length() - pos <= 20) {
      return "\"" + code.substring(pos).trim() + "\"";
    }
    else {
      return "\"" + code.substring(pos, pos + 17).trim() + "...\"";
    }
  }
  
  private static boolean isNumeric(String strNum) {
    try {
        int d = parseInt(strNum);
    } catch (NumberFormatException | NullPointerException nfe) {
        return false;
    }
    return true;
  }
  
  private static boolean isSeparator(char symbol) {
    for (char separator : separators) {
      if (symbol == separator) {
        return true;
      }
    }
    return false;
  }
  
  private static final char[] separators = 
      {'[', ']', '(', ')', ':', '=', '!', '^', '|', ',', '?'};
}