package test.random;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import automata.IBuchi;
import util.parser.gff.GFFFileParser;

public class FilterSemideterminism {
    
    private static final String EXT = ".gff";
    private static final String NAME = "rand-aut20";
    private static String det = "/home/liyong/workspace-neon/Buchi/"+ NAME + "/";
    private static int numBA = 0;
    
    public static void main(String[] args) {
        
//      String dir = "/home/liyong/workspace-neon/SemiBuchi/src/main/resources/benchmarks/" + NAME;
        String dir = "/home/liyong/Downloads/ciaa2010_automata/automata-size-20";
        
        File fileDir = new File(dir);
        List<String> nonSemiDetFiles = new ArrayList<>();
        List<IBuchi> buchis = new ArrayList<>();
        
        int numSemiDets = 0;
        for(File f : fileDir.listFiles( )) {
            if(! f.getName().contains(EXT)) continue;
            numBA ++;
            if(!isSemiDeterministic(f)) {
                nonSemiDetFiles.add(f.getName());
            }else {
                numSemiDets ++;
            }
        }
        System.out.println("#BAFile=" + numBA);
        System.out.println("#NonSemiDet=" + nonSemiDetFiles.size());
        System.out.println("#SemiDet=" + numSemiDets);
        for(int i = 0; i < buchis.size(); i ++) {
            System.out.println(nonSemiDetFiles.get(i));
//          System.out.println(buchis.get(i).toDot());
        }
        
        
    }
    
    private static void copy(File file)  {
        String name = det + file.getName();
        FileInputStream fis;
        FileOutputStream fos;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(name);
            int b;
            try {
                while  ((b = fis.read()) != -1)
                    fos.write(b);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    private static boolean isSemiDeterministic(File file) {
        GFFFileParser gffParser =  new GFFFileParser();
        gffParser.parse(file.getAbsolutePath());
        IBuchi buchi = gffParser.getBuchi();
        boolean isSemiDet = false;
        isSemiDet = buchi.isSemiDeterministic();
        if(isSemiDet) {
            copy(file);
        }
        return isSemiDet;
    }

}