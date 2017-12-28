package datalog_ra;

import datalog_ra.base.TupleTransformation.ProjectionTransformation;
import datalog_ra.base.TupleTransformation.TupleTransformation;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import datalog_ra.base.operator.*;
import datalog_ra.base.relation.*;
import java.util.Arrays;

/**
 *
 * @author c3po
 */
public class Datalog_RA {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_GREEN = "\u001B[32m";
    
    
    static Relation loadDB(String fileName) throws FileNotFoundException, IOException{
        Relation r = new Relation();
        String line;      // aktualne citany riadok
        String splitBy = " ";  // oddelovnik hodnot
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            Tuple t;
            while ((line = reader.readLine()) != null) { // citam, kym nie je koniec riadka
                String[] splitedLine = line.split(splitBy); // rozdel riadok podla oddelovnikov
                t = new Tuple();
                // vloz hodnoty do objektu Row
                for (String s : splitedLine) {
                    t.add(new Attribute(s));
                }
                r.add(t);
            }
            reader.close();
        } 
            System.out.println(r.toString());
            return r;
    }
    
    public static void main(String[] args) throws IOException {
        
        System.out.println(ANSI_RED + "capuje" + ANSI_RESET);
        Relation capuje = loadDB("capuje.txt");
        System.out.println(ANSI_RED + "lubi" + ANSI_RESET);
        Relation lubi = loadDB("lubi.txt");
        System.out.println(ANSI_RED + "navstivil" + ANSI_RESET);
        Relation navstivil = loadDB("navstivil.txt");
        System.out.println(ANSI_RED + "vypil" + ANSI_RESET);
        Relation vypil = loadDB("vypil.txt");
        
        //test 1
            
        Join j1 = new Join(navstivil.operator(),vypil.operator(),new NavstivilVypilNaturalCondition());   
        Selection_Projection sp1 = new Selection_Projection(j1, new ProjectionTransformation(Arrays.asList(true, true, true, false, true, true)));
        System.out.println("");
        System.out.println(ANSI_BLUE +"//////navstivil \'NATURAL\'JOIN vypil//////" + ANSI_RESET);
        for(Tuple t = new Tuple(); t !=null; t = sp1.next()){
            System.out.print(ANSI_GREEN + t.toString() + ANSI_RESET);
        } 
        
        //test 2
        
        Selection_Projection sp2 = new Selection_Projection(capuje.operator(),new CapujeIneAkoPivo());
        AntiJoin aj2 = new AntiJoin(capuje.operator(), sp2, new CapujeANTIJOINCapujeCondition());
        
        System.out.println("");
        System.out.println(ANSI_BLUE +"//////krcmy, kde sa capuje iba pivo//////" + ANSI_RESET);
        System.out.println(ANSI_BLUE +"//////capuje ANTIJOIN (SEL[!pivo] capuje)//////" + ANSI_RESET);
        for(Tuple t = new Tuple(); t !=null; t = aj2.next()){
            System.out.print(ANSI_GREEN + t.toString() + ANSI_RESET);
        }
        
        //test 3
            
        Join j3 = new Join(navstivil.operator(),vypil.operator(),new NavstivilVypilNaturalCondition());   
        Selection_Projection s3 = new Selection_Projection(j3, new PijeSaIneAkoPivo());
        Selection_Projection p3 = new Selection_Projection(s3, new ProjectionTransformation(Arrays.asList(false, false, true)));
        System.out.println("");
        System.out.println(ANSI_BLUE +"//////krcmy, kde sa pije ine ako pivo//////" + ANSI_RESET);
        System.out.println(ANSI_BLUE +"//////PROJECTION(alkohol != pivo, iba krcmy) (navstivil NATURALJOIN vypil)//////" + ANSI_RESET);
        for(Tuple t = new Tuple(); t !=null; t = s3.next()){
            System.out.print(ANSI_GREEN + t.toString() + ANSI_RESET);
        } 
        
        //test 3.5
        p3.reset();
        AntiJoin aj3 = new AntiJoin(capuje.operator(),p3, new CapujeSaIbaPivoANTIJOINCondition());
        System.out.println("");
        System.out.println(ANSI_BLUE +"//////krcmy, kde sa pije iba pivo//////" + ANSI_RESET);
        System.out.println(ANSI_BLUE +"//////capuje ANTIJOIN test3//////" + ANSI_RESET);
        for(Tuple t = new Tuple(); t !=null; t = aj3.next()){
            System.out.print(ANSI_GREEN + t.toString() + ANSI_RESET);
        }
        
    } /* main */
}
class CapujeSaIbaPivoANTIJOINCondition implements TupleTransformation{
    @Override
    public Tuple transform(Tuple tuple) {
        if (tuple.get(0).equals(tuple.get(3)))
            return tuple;
        else return null;
    }
}

class NavstivilVypilNaturalCondition implements TupleTransformation{
    @Override
    public Tuple transform(Tuple tuple) {
        if (tuple.get(0).equals(tuple.get(3)))
            return tuple;
        else return null;
    }
}

class PijeSaIneAkoPivo implements TupleTransformation{
    @Override
    public Tuple transform(Tuple tuple) {
        if (!tuple.get(4).equals(new Attribute("pivo")))
            return tuple;
        else return null;
    }
}

class CapujeIneAkoPivo implements TupleTransformation{
    @Override
    public Tuple transform(Tuple tuple) {
        if (!tuple.get(1).equals(new Attribute("pivo")))
            return tuple;
        else return null;
    }
}

class CapujeANTIJOINCapujeCondition implements TupleTransformation{
    @Override
    public Tuple transform(Tuple tuple) {
        if (tuple.get(0).equals(tuple.get(3)))
            return tuple;
        else return null;
    }
}