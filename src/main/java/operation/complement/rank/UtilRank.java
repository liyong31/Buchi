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
    
    public static LevelRankingConstraint getRankedConstraint(IBuchi buchi, LevelRanking lvlRank, int letter) {
        LevelRankingConstraint constraint = new LevelRankingConstraint();
        for (final int s : lvlRank.getS()) {
            for (final int t : buchi.getState(s).getSuccessors(letter)) {
                constraint.addConstraint(t, lvlRank.getLevelRank(s), lvlRank.isInO(s),
                        lvlRank.isOEmpty());
            }
        }
        return constraint;
    }

}
