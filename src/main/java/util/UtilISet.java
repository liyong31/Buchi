package util;

public class UtilISet {
    private UtilISet() {
        
    }
    
    public static ISet newISet() {
        return new ISetTreeSet();
    }

}
