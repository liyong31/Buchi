package util;

import main.Options;

public class UtilISet {
    private UtilISet() {
        
    }
    
    public static ISet newISet() {
        return new ISetTreeSet();
    }
    
    public static String getSetType() {
        String setType = null;
        switch(Options.mSet) {
        case 1:
            setType = "SparseBitSet";
            break;
        case 2:
            setType = "TIntSet";
            break;
        case 3:
            setType = "TreeSet";
            break;
        case 4:
            setType = "HashSet";
            break;
        default:
            setType = "BitSet";
            break;
        }
        return setType;
    }

}
