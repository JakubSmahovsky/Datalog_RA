package datalog_ra.programRA;

import datalog_ra.base.dataStructures.Instance;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 *
 * @author Jakub
 */
public class programRA {
  private final LinkedList<RASubprogram> body = new LinkedList<>(); 
  private int pos;
  private String code;

  public programRA(String pathString) throws Exception {
    Path path = Paths.get("resources\\" + pathString);
    
    // verify file exists
    if (!Files.exists(path)) {
      System.out.println("File " + pathString
          + " does not exist within the resources directory.");
      return;
    }
    
    // read code from file
    code = new String(Files.readAllBytes(path),Charset.defaultCharset());
    build();
  }
  
  private void build(){
    pos = 0;
    skipWhitespace();
    
    // minimum subprogram length is 5 eg.: "r:=r."
    while (code.length() - pos >= "while".length()) {
      skipWhitespace();
      
      if (code.substring(pos, pos+"while".length()).compareTo("while") == 0) {
        pos += 5; // skip while
        add(whileloop());
      } else if (code.charAt(pos) == '?'){
        pos ++;
        add(query());
      } else {
        add(assignment());
      }
      skipWhitespace();
    }
    
    if (pos < code.length()) {
      System.out.println("Syntax error, not a statement: " + 
          code.substring(pos));
    }
    
    code = null;
  }
  
  private WhileLoop whileloop() {
    int begin = pos;
    skipWhitespace();
    
    if (code.substring(pos, pos+"begin".length()).compareTo("begin") != 0) {
      System.out.println("Syntax error, expected \"begin\" at" + sample(begin));
      return null;
    }
    pos += "beign".length();
    
    WhileLoop loop = new WhileLoop();
    
    // minimum subprogram length is 5 eg.: "r:=r."
    while (pos <= code.length() - "end".length()) {
      skipWhitespace();
      // end at "end"
      if (code.substring(pos, pos+"end".length()).compareTo("end") == 0) {
        pos += "end".length();
        return loop;
      }
      
      if (code.substring(pos, pos+"while".length()).compareTo("while") == 0) {
        pos += 5;
        loop.add(whileloop());
      } else if (code.charAt(pos) == '?'){
        pos ++;
        loop.add(query());
      } else {
        loop.add(assignment());
      }
    }
    
    System.out.println("Syntax error, no end of loop at " + sample(begin));
    return null;
  }
  
  private Assignment assignment() {
    int begin = pos;
    skipWhitespace();
    StringBuilder sourceCode = new StringBuilder();
    
    while (pos < code.length()) {
      // end at "."
      if (code.charAt(pos) == '.') {
        pos++;
        return new Assignment(sourceCode.toString());
      }
      
      sourceCode.append(code.charAt(pos));
      pos++;
    }
    
    System.out.println("Syntax error, invalid expression at " + sample(begin)
        + " did you forget a terminating symbol?");
    
    return null;
  }
  
  private Query query() {
    int begin = pos;
    skipWhitespace();
    StringBuilder sourceCode = new StringBuilder();
    
    while (pos < code.length()) {
      // end at "."
      if (code.charAt(pos) == '.') {
        pos++;
        return new Query(sourceCode.toString());
      }
      
      sourceCode.append(code.charAt(pos));
      pos++;
    }
    
    System.out.println("Syntax error, invalid expression at " + sample(begin)
        + " did you forget a terminating symbol?");
    
    return null;
  }
  
  public void run(Instance source) {
    for (RASubprogram subprogram : body) {
      subprogram.run(source);
    }
  }
  
  public void add(RASubprogram subprogram) {
    this.body.add(subprogram);
  }
  
  private void skipWhitespace() {
    while ( pos < code.length() && (
            code.charAt(pos) == ' '  ||
            code.charAt(pos) == '\t' ||
            // newline... I think
            Character.hashCode(code.charAt(pos)) == 10 ||
            Character.hashCode(code.charAt(pos)) == 13
          )) {
      pos++;
    }
  }
  
  private String sample(int pos) {
    if (code.length() - pos <= 20) {
      return "\"" + code.substring(pos).trim() + "\"";
    }
    else {
      return "\"" + code.substring(pos, 17).trim() + "...\"";
    }
  }
}
