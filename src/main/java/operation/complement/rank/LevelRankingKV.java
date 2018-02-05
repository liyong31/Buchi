package operation.complement.rank;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

public class LevelRankingKV implements LevelRanking {

    private final TIntIntMap mFunction;
    
    public LevelRankingKV() {
        mFunction = new TIntIntHashMap();
    }
    
    @Override
    public void addLevelRank(int state, int rank) {
        mFunction.put(state, rank);
    }

    @Override
    public int getLevelRank(int state) {
        if(mFunction.containsKey(state)) {
            return mFunction.get(state);
        }
        return -1;
    }
    
}
