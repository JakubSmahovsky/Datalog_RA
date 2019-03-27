package datalog_ra.Test;

import datalog_ra.base.instance.Instance;
import datalog_ra.evaluation.Query;

/**
 *
 * @author Jakub
 */
public abstract class Test {

  public static Query test(String s, Instance db) {
    switch (s) {
      case "winmove":
        return new winmove(db);
      case "wellf":
        return new wellf(db);
    }
    return null;
  }
}
