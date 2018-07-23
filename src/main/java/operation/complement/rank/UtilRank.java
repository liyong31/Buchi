package operation.complement.rank;

import automata.IBuchi;
import util.ISet;
import util.UtilISet;

public class UtilRank {
    
    private UtilRank() {
        
    }
    
    public static ISet collectSuccessors(IBuchi buchi, ISet set, int letter) {
        ISet succs = UtilISet.newISet();
        for(final int s :  set) {
            succs.or(buchi.getState(s).getSuccessors(letter));
        }
        return succs;
    }

}
