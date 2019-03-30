package datalog_ra.base.instance;

import datalog_ra.base.operator.Union;
import datalog_ra.base.relation.Relation;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public class Instance {

  private HashMap<String, Relation> relations = new HashMap();

  public Instance() {
  }
  
  public Instance(String pathString) throws Exception {
    Path path = Paths.get("resources\\" + pathString);
    
    // verify file exists
    if (!Files.exists(path)) {
      System.out.println("File " + pathString
          + "does not exist within the resources directory.");
      return;
    }
    
    // load file content and split into relation strings
    byte[] inputBytes = Files.readAllBytes(path);
    String inputString = new String(inputBytes, Charset.defaultCharset());
    String[] relationStrings = inputString.split("relation");
    
    // init and add all relations
    relations = new HashMap();
    for (int i = 1; i < relationStrings.length; i++) {
      String[] relationStringParts = relationStrings[i].split(":");
      
      if (relationStringParts.length > 2) {
        System.out.println("Syntax error: expected \"relation\" found \":\"");
        return;
      }
      
      relations.put(relationStringParts[0].trim(),
          new Relation(relationStringParts[1]));
    }
  }

  public boolean snapshot(File directory, boolean Overwrite) {
    if (directory.exists() && !Overwrite) {
      System.out.println("Directory " + directory.getPath()
              + " already exists. Overwrite? Y/N");
      return false;
    }

    directory.mkdirs();

    for (String relation_name : relations.keySet()) {
      try {
        FileWriter fw = new FileWriter(directory.getPath() + "\\" + relation_name + ".txt");
        fw.write(relations.get(relation_name).toString());
        fw.close();
      } catch (IOException ex) {
        Logger.getLogger(Instance.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    System.out.println("Instance successfully saved to "
            + directory.getPath() + ".");
    return true;
  }

  public Relation get(String relation_name) {
    return relations.get(relation_name);
  }

  public Set<String> getNames() {
    return relations.keySet();
  }

  /**
   * Adds Relation newRelation. If a relation called name is allready present it
   * is NOT replaced.
   *
   * @return true if Relation was added and false otherwise
   */
  public boolean add(String name, Relation newRelation) {
    if (relations.containsKey(name)) {
      return false;
    } else {
      relations.put(name, newRelation);
      return true;
    }
  }

  /**
   * Replaces the Relation called name with Relation relation. Does not add
   * Relation relation if no Relation called name existed before.
   *
   * @return true if a relation was changed and false otherwise
   */
  public boolean replace(String name, Relation relation) {
    if (!relations.containsKey(name)) {
      return false;
    } else {
      relations.put(name, relation.copy());
      return true;
    }
  }

  /**
   * Extends the relation called name by provided relation. Or adds a new
   * relation called name to the instance if it did not exist before.
   *
   * @return true if an existing relation was changed and false if new one was
   * added
   */
  public boolean append(String name, Relation relation) {
    if (!relations.containsKey(name)) {
      relations.put(name, relation);
      return false;
    }

    relations.put(name, new Relation( //replacing by new Relation
            new Union(relation.operator(), //which is Union of provided rel.
                    relations.get(name).operator()))); //and existing rel. 
    return true;
  }

  public boolean updateFrom(Instance source) {
    for (String relation : relations.keySet()) {
      Relation newRelation = source.get(relation);
      if (newRelation == null) {
        System.out.println("Unable to update relation " + relation
                + ". Not present in the new instance.");
        return false;
      }
      replace(relation, newRelation);
    }
    return true;
  }

  public Instance copy() {
    Instance result = new Instance();
    for (String relation : relations.keySet()) {
      result.add(relation, relations.get(relation).copy());
    }
    return result;
  }

  public boolean compareTo(Instance anotherInstance) {
    if (relations.keySet().size() != anotherInstance.getNames().size()) {
      return false;
    }

    for (String relation : relations.keySet()) {
      if (anotherInstance.get(relation) == null) {
        return false;
      }
    }

    for (String relation : relations.keySet()) {
      if (!relations.get(relation).compareTo(anotherInstance.get(relation))) {
        return false;
      }
    }
    return true;
  }

  public String toString() {
    String result = new String();
    Iterator<String> it = relations.keySet().iterator();

    if (it.hasNext()) {
      String name = it.next();
      result += name + "\n" + relations.get(name).toString();
    }
    for (; it.hasNext();) {
      String name = it.next();
      result += "\n" + name + "\n" + relations.get(name).toString();
    }

    return result;
  }
}
