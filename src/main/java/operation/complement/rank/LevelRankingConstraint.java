package operation.complement.rank;

public class LevelRankingConstraint extends LevelRankingState {
    
    public LevelRankingConstraint() {
        
    }
    
    
    protected void addConstraint(final int state, final int predRank, final boolean predIsInO
            , final boolean predOIsEmpty) {
        final int oldRank = mLevelRankings.get(state);
        if (oldRank == mLevelRankings.getNoEntryValue() || oldRank > predRank) {
            mLevelRankings.put(state, predRank);
        }
        if (mMaxRank < predRank) {
            mMaxRank = predRank;
        }
        
        boolean isInO = predIsInO || predOIsEmpty;
        if(isInO) {
            addToO(state);
        }
    }

    

}
