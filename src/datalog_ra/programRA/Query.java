package datalog_ra.programRA;

import datalog_ra.base.dataStructures.Instance;
import static java.lang.Integer.parseInt;

/**
 *
 * @author Jakub
 */
public class Query extends RASubprogram { 
  private String code;
  private int pos;
  
  private String name;
  private int arity;
  
  public Query(String sourceCode) {
    this.code = sourceCode;
    this.pos = 0;
    
    name = symbol();
    expect("|");
    String symbol = symbol();
    if (!isNumeric(symbol)) {
      System.out.println("Syntax error, expected relation arity after \"|\" at "
          + sample(0));
      return;
    }
    arity = parseInt(symbol);
  }
  
  public void run(Instance source) {
    System.out.println(source.forceGet(name, arity));
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
