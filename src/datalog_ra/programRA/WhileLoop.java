package datalog_ra.programRA;

import datalog_ra.base.dataStructures.Instance;
import java.util.LinkedList;

/**
 *
 * @author Jakub
 */
public class WhileLoop extends RASubprogram{
  private Instance oldInstance;
  private final LinkedList<RASubprogram> body = new LinkedList<>(); 
  
  public WhileLoop(){}

  public void add(RASubprogram subprogram) {
    this.body.add(subprogram);
  }

  @Override
  public void run(Instance target) {    
    do {
      oldInstance = target.copy();
      
      for (RASubprogram subprogram : body) {
        subprogram.run(target);
      }
    } while (!oldInstance.compareTo(target));
    oldInstance = null;
  }
}
