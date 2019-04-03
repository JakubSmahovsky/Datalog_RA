package datalog_ra.programRA;

import datalog_ra.base.dataStructures.Instance;
import datalog_ra.base.dataStructures.Relation;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 *
 * @author Jakub
 */
public class WhileLoop {
  private Instance newInstance;
  private Instance oldInstance;
  
  private LinkedList<Assignment> body = new LinkedList<>(); 
  public WhileLoop(String pathString, Instance source) throws Exception {    
    this.newInstance = source.copy();
    
    Path path = Paths.get("resources\\" + pathString);
    
    // verify file exists
    if (!Files.exists(path)) {
      System.out.println("File " + pathString
          + " does not exist within the resources directory.");
      return;
    }
    
    // load file content and split into rule strings
    String inputString = new String(Files.readAllBytes(path), Charset.defaultCharset());
    String[] assignmenetStrings = inputString.split("\\.");
    
    // init and add all relations
    for (int i = 0; i < assignmenetStrings.length; i++) {
      Assignment a = new Assignment(assignmenetStrings[i], source);
      body.add(a);
      newInstance.add(new Relation(a.getRelationName(), a.getRelationArity()));
    }
    
    while (!newInstance.compareTo(oldInstance)) {
      oldInstance = newInstance.copy();
      for (Assignment a : body) {
        a.rebuild(oldInstance);
        Relation r = new Relation(a.getOperator(), a.getRelationName());
        if (!newInstance.replace(r)){
          newInstance.add(r);
        }
      }
    }
    
    System.out.println(newInstance);
  }
}
