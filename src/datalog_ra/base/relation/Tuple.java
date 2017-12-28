package datalog_ra.base.relation;

import java.util.ArrayList;
import java.util.Iterator;

public class Tuple implements Iterable<Attribute>{
    private final ArrayList<Attribute> attributes;
    
    public Tuple(){
        attributes = new ArrayList<>();
    }
    
    public void add(Attribute attribute){
        attributes.add(attribute);
    }
    
    public boolean subsumed(Tuple tuple){
        if (this.size() != tuple.size())
            return false;
        Iterator<Attribute> it = tuple.iterator();
        for (Attribute a : attributes) {
            //attributes differ
            if (!(it.next().equals(a))) return false;
        }
        return true;
    }
    
    @Override
    public String toString(){
        String result = "";
        for (Attribute attribute : attributes){
            result += attribute.getValue() + " | ";
        }
        result += "\n";
        return result;
    }
    
    @Override
    public Iterator<Attribute> iterator() {
        return new TupleIterator(attributes.iterator());
    }
    
    public int size() {
        return attributes.size();
    }
    
    public Attribute get(int index){
        return attributes.get(index);
    }
    
    /** Iterator of internal Attribute List with restricted access to funcionality.  
    */
    private class TupleIterator implements Iterator<Attribute> {
        private final Iterator<Attribute> it; 
        
        public TupleIterator(Iterator<Attribute> it) {
            this.it = it; 
        }
        
        @Override
        public Attribute next() {
            return it.next();
        }
        
        @Override
        public boolean hasNext() {
            return it.hasNext();
        }
        
        @Override
        public void remove(){
            System.out.println("Unsupported action: remove() from Tuple!");
        }
    }
}
