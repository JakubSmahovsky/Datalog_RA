package datalog_ra.base.dataStructures;

import datalog_ra.base.operator.Union;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

/**
 *
 * @author Jakub
 */
public class Instance {

  private HashSet<Relation> relations = new HashSet();

  public Instance() {
  }
  
  public Instance(String pathString) throws Exception {
    Path path = Paths.get("resources\\" + pathString);
    
    // verify file exists
    if (!Files.exists(path)) {
      System.out.println("File " + pathString
          + " does not exist within the resources directory.");
      return;
    }
    
    // load file content and split into relation strings
    byte[] inputBytes = Files.readAllBytes(path);
    String inputString = new String(inputBytes, Charset.defaultCharset());
    String[] relationStrings = inputString.split("relation");
    
    // init and add all relations
    for (int i = 1; i < relationStrings.length; i++) {
      relations.add(new Relation(relationStrings[i]));
    }
  }

  public Relation get(String relationName, int arity) {
    for (Relation relation : relations) {
      if ((relation.getName().compareTo(relationName) == 0)
          && (relation.getArity() == arity)) {
        return relation;
      }
    }
    return null;
  }
/*
  public Set<String> getNames() {
    return relations.keySet();
  }
*/
  
  /**
   * Adds Relation newRelation. If a relation called name is allready present it
   * is NOT replaced.
   *
   * @return true if Relation was added and false otherwise
   */
  public boolean add(Relation newRelation) {
    if (get(newRelation.getName(), newRelation.getArity()) != null) {
      return false;
    } else {
      relations.add(newRelation);
      return true;
    }
  }

  /**
   * Replaces the Relation called name with Relation relation. Does not add
   * Relation relation if no Relation called name existed before.
   *
   * @return true if a relation was changed and false otherwise
   */
  public boolean replace(Relation relation) {
    Relation existing = get(relation.getName(), relation.getArity());
    if (existing == null) {
      return false;
    }
    
    relations.remove(existing);
    relations.add(relation);
    return true;
    
  }

  /**
   * Extends the relation called name by provided relation. Or adds a new
   * relation called name to the instance if it did not exist before.
   *
   * @return true if an existing relation was changed and false if new one was
   * added
   */
  public boolean append(Relation relation) {    
    Relation existing = get(relation.getName(), relation.getArity());
    if (existing == null) {
      relations.add(relation);
      return false;
    }

    relations.remove(existing);
    relations.add(new Relation(
        new Union(existing.operator(),relation.operator()),
        relation.getName()
    ));
    return true;
  }

  public boolean updateFrom(Instance source) {
    for (Relation relation : relations) {
      Relation newRelation = source.get(relation.getName(), relation.getArity());
      
      if (newRelation == null) {
        System.out.println("Unable to update relation " + relation
                + ". Not present in the new instance.");
        return false;
      }
      
      replace(newRelation);
    }
    return true;
  }

  public Instance copy() {
    Instance result = new Instance();
    for (Relation relation : relations) {
      result.add(relation.copy());
    }
    return result;
  }
  
  public int size() {
    return relations.size();
  }

  public boolean compareTo(Instance anotherInstance) {
    if (relations.size() != anotherInstance.size()) {
      return false;
    }

    for (Relation relation : relations) {
      if (anotherInstance.get(relation.getName(), relation.getArity()) == null) {
        return false;
      }
    }

    for (Relation relation : relations) {
      Relation anotherRelation = anotherInstance.get(
          relation.getName(),
          relation.getArity()
      );
      
      if (!relation.compareTo(anotherRelation)) {
        return false;
      }
    }
    return true;
  }

  public String toString() {
    String result = new String();

    for (Relation relation : relations) {
      result += relation.toString() + "\n";
    }

    return result;
  }
}
