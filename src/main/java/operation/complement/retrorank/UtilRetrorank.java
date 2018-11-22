package operation.complement.retrorank;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import main.Options;
import operation.complement.rank.LevelRanking;
import operation.complement.rank.LevelRankingConstraint;
import util.ISet;

public class UtilRetrorank {
    
    private UtilRetrorank() {
        
    }
    
    public static LevelRanking tighten1(LevelRanking lvlRank, ISet fset) {
        int[] rankNumbers = lvlRank.countRankNumbers();
        TIntIntMap rankMap = new TIntIntHashMap();
        int num = 0;
        for(int i = 1; i <= lvlRank.getMaximalRank(); i += 2) {
            // no states with odd rank i
            if(rankNumbers[i] <= 0 ) continue;
            rankMap.put(i, num);
            rankMap.put(i - 1, num); // for even ranks
            num ++;
        }
        LevelRanking result = new LevelRanking(true, Options.mTurnwise);
        for(final int s : lvlRank.getS()) {
            int rank;
            if(fset.get(s)) {
                rank = 2 * rankMap.get(lvlRank.getLevelRank(s));
            }else {
                rank = 2 * rankMap.get(lvlRank.getLevelRank(s)) + 1;
            }
            result.addLevelRank(s, rank, LevelRanking.isEven(rank) && lvlRank.isInO(s));
        }
        return result;
    }
    
    /**
     * tighten the given level ranking,
     * 1. even rank should stay in even rank
     * 2. odd rank should stay in odd rank 
     * */
    public static LevelRanking tighten(LevelRanking lvlRank, ISet fset) {
        int[] rankNumbers = lvlRank.countRankNumbers();
        TIntIntMap rankMap = new TIntIntHashMap();
        int num = 0;
        for(int i = 1; i <= lvlRank.getMaximalRank(); i += 2) {
            // no states with odd rank i
            if(rankNumbers[i] <= 0 ) continue;
            rankMap.put(i, num);
            rankMap.put(i - 1, num); // for even ranks
            num ++;
        }
        LevelRanking result = new LevelRanking(true, Options.mTurnwise);
        for(final int s : lvlRank.getS()) {
            int rank;
            if(LevelRanking.isEven(lvlRank.getLevelRank(s))) {
                rank = 2 * rankMap.get(lvlRank.getLevelRank(s));
            }else {
                rank = 2 * rankMap.get(lvlRank.getLevelRank(s)) + 1;
            }
            result.addLevelRank(s, rank, LevelRanking.isEven(rank) && lvlRank.isInO(s));
        }
        return result;
    }

}
