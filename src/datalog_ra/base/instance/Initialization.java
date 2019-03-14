package datalog_ra.base.instance;
import datalog_ra.base.relation.Attribute;
import datalog_ra.base.relation.Relation;
import datalog_ra.base.relation.Tuple;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Jakub
 */
public interface Initialization {
    public static Relation loadRelation(File file) throws FileNotFoundException, IOException{
        Relation r = new Relation();
        String line;      // aktualne citany riadok
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Tuple t;
            while ((line = reader.readLine()) != null) { // citam, kym nie je koniec riadka
                String[] splitLine = line.split("\\|"); // rozdel riadok podla oddelovnikov
                t = new Tuple();
                // vloz hodnoty do objektu Row
                for (String s : splitLine) {
                    t.add(new Attribute(s));
                }
                r.add(t);
            }
            reader.close();
        }
        return r;
    }
}
