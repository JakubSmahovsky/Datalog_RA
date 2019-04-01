package datalog_ra.base.dataStructures;

import datalog_ra.base.operator.Union;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;

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
    
    // load file content and split into rule strings
    String inputString = new String(Files.readAllBytes(path), Charset.defaultCharset());
    String[] ruleStrings = inputString.split("\\.");
    
    Relation lastUsed = null;
    
    // init and add all relations
    for (int i = 0; i < ruleStrings.length; i++) {
      String rule = ruleStrings[i];
      
      // parse rule
      String name = rule.substring(0, rule.indexOf("(")).trim();
      String[] values = rule.substring(
          rule.indexOf("(")+1,
          rule.indexOf(")")
      ).split(",");
      int arity = values.length;
      LinkedList<Attribute> attributes = new LinkedList<>();
      for (String value : values) {
        attributes.add(new Attribute(value.trim()));
      }
      
      Relation matchingRelation = null;
      
      // try to add the new tuple to previously used relation
      if (lastUsed != null &&
          lastUsed.getName().compareTo(name) == 0 &&
          lastUsed.getArity() == arity) {
        matchingRelation = lastUsed;
      } // try to add the new tuple to another existing relation
      else if (get(name, arity) != null) {
        matchingRelation = get(name, arity);
      } // make a new relation for the tuple
      else {
        matchingRelation = new Relation(name, arity);
        add(matchingRelation);
      }
      
      matchingRelation.add(new Tuple(attributes));
      lastUsed = matchingRelation;
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
