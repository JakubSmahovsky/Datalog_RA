package datalog_ra;

import datalog_ra.Test.Test;
import datalog_ra.base.instance.Instance;
import java.util.Scanner;
import java.io.File;

/**
 *
 * @author Jakub
 */
public class Datalog_RA {

  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_GREEN = "\u001B[32m";

  public static void main(String[] args) throws Exception {

    Scanner cin = new Scanner(System.in);
    Instance EDB = new Instance();
    while (true) {
      if (cin.hasNext()) {
        String input = cin.nextLine();
        String command[] = input.split(" +");
        if (command.length < 1) {
          System.out.println("?");
        } else {
          switch (command[0]) {
            // load command, loads the contents of directory in command[1]
            case "load":
              if (command.length < 2) {
                System.out.println("Directory name is required!");
                break;
              }
              EDB = new Instance(command[1]);
              System.out.println(EDB);
              break;
            // save command, saves the current EDB to directory in command[1]    
            case "save":
              if (command.length < 2) {
                System.out.println("Directory name is required!");
                break;
              }
              File out = new File("resources/" + command[1]);
              if (!EDB.snapshot(out, false)) {
                input = cin.nextLine();
                System.out.println(input.trim().toUpperCase());
                if (input.trim().toUpperCase().compareTo("Y") == 0) {
                  EDB.snapshot(out, true);
                }
              }
              break;
            case "test":
              if (command[1] == null) {
                System.out.println("Test name is required!");
                break;
              }
              Test.test(command[1], EDB);
              break;
            case "quit":
              return;
            case "q":
              return;
            default:
              System.out.println(command[0] + " is not a valid command.");
          }
        }
      }
    }
  }
}
