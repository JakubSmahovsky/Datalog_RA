package datalog_ra.base.database;

import datalog_ra.base.operator.Union;
import datalog_ra.base.relation.Relation;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public class Database {
    private HashMap<String,Relation> relations = new HashMap(); 
    
    public Database(){
    }
    
    public boolean init(File directory){
        if (!directory.exists()) {
            System.out.println("Directory " + directory.getPath() + 
                " does not exist.");
            return false;
        }  
        else {
            relations = new HashMap();
            File[] relationFiles = directory.listFiles(
                    (File dir, String name) -> name.endsWith(".txt"));

            for (File f : relationFiles) {
                try {
                    String relation_name = f.getName().substring(0, f.getName().lastIndexOf('.'));
                    relations.put(relation_name,Initialization.loadRelation(f));
                } catch (IOException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return true;
        }
    }
    
    public boolean snapshot(File directory, boolean Overwrite){
        if (directory.exists() && !Overwrite) {
            System.out.println("Directory " + directory.getPath() 
                    + " already exists. Overwrite? Y/N");
            return false;
        }
        
        directory.mkdirs();

        for (String relation_name : relations.keySet()){
            try {
                FileWriter fw = new FileWriter(directory.getPath()+ "\\" + relation_name + ".txt");
                fw.write(relations.get(relation_name).toString());
                fw.close();
            }
            catch (IOException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Database successfully saved to " 
                + directory.getPath() + ".");
        return true;
    }
    
    public Relation get(String relation_name) {
        return relations.get(relation_name);
    }
    
    public Set<String> getNames() {
        return relations.keySet();
    }
    
    /** Adds Relation newRelation. If a relation called name is allready present
     * it is NOT replaced.
     * @return true if Relation was added and false otherwise
     */
    public boolean add(String name, Relation newRelation) {
        if (relations.containsKey(name))
            return false;
        else {
            relations.put(name, newRelation);
            return true;
        }
    }

    /**
     * Replaces the Relation called name with Relation relation.
     * Does not add Relation relation if no Relation called name existed before.
     * @return true if a relation was changed and false otherwise
     */
    public boolean replace(String name, Relation relation) {
        if (!relations.containsKey(name))
            return false;
        else {
            relations.put(name, relation.copy());
            return true;
        }
    }
    
    /**
     * Extends the relation called name by provided relation. Or adds a new
     * relation called name to the database if it did not exist before.
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
    
    public boolean updateFrom(Database database) {
        for (String relation : relations.keySet()) {
            Relation newRelation = database.get(relation);
            if (newRelation == null) {
                System.out.println("Unable to update relation " + relation 
                        + ". Not present in the new database.");
                return false;
            }
            replace(relation, newRelation);
        }
        return true;
    }
    
    public Database copy(){
        Database result = new Database();
        for (String relation : relations.keySet()) {
            result.add(relation, relations.get(relation).copy());
        }
        return result;
    }
    
    public boolean compareTo(Database anotherDatabase){
        if (relations.keySet().size() != anotherDatabase.getNames().size()) {
            return false;
        }
        
        for (String relation : relations.keySet()) {
            if (anotherDatabase.get(relation) == null)
                return false;
        }
        
        for (String relation : relations.keySet()) {
            if (!relations.get(relation).compareTo(anotherDatabase.get(relation)))
                return false;
        }
        return true;
    }
    
    public String toString(){
        String result = new String();
        Iterator<String> it = relations.keySet().iterator();
        
        if (it.hasNext()) { 
            String name = it.next();
            result += name + "\n" + relations.get(name).toString();
        }
        for (; it.hasNext();){
            String name = it.next();
            result +=  "\n" + name + "\n" + relations.get(name).toString();
        }
        
        return result;
    }
}
