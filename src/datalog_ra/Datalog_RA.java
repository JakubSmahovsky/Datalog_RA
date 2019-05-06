package datalog_ra;

import datalog_ra.Test.Test;
import datalog_ra.base.dataStructures.Instance;
import datalog_ra.programRA.WhileLoop;
import datalog_ra.programRA.programRA;
import java.util.Scanner;

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
              if (command[1] == null) {
                System.out.println("File path is required!");
                break;
              }
              EDB = new Instance(command[1]);
              System.out.println("Load done!");
              break;
            // save command, saves the current EDB to directory in command[1]    
            case "test":
              if (command[1] == null) {
                System.out.println("Test name is required!");
                break;
              }
              Test.test(command[1], EDB);
              break;
            case "ra": 
              if (command[1] == null) {
                System.out.println("File path is required!");
                break;
              }
              programRA prog = new programRA(command[1]);
              long startTime = System.nanoTime();
              prog.run(EDB);
              System.out.println("query time: " 
                  + (System.nanoTime() - startTime )/1000000 );
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
