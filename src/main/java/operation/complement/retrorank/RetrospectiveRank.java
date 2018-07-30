package operation.complement.retrorank;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import main.Options;
import operation.complement.rank.LevelRanking;
import operation.complement.tuple.Color;
import operation.complement.tuple.OrderedSets;
import util.ISet;

public class RetrospectiveRank {
    
    protected final OrderedSets mOrdSets; // left most are successors of final states
    protected final LevelRanking mLvlRank; // level rankings
    private final boolean mHasRanked;      // whether in accepting component
    
    public RetrospectiveRank(boolean hasRanked) {
        if(hasRanked) {
            this.mLvlRank = new LevelRanking(true, Options.mTurnwise);
            this.mOrdSets = null;
        }else {
            this.mOrdSets = new OrderedSets(false); // non colored sets
            this.mLvlRank = null;
        }
        this.mHasRanked = hasRanked;
    }
    
    public void add(ISet oset) {
        if(! this.mHasRanked) {
            this.mOrdSets.addSet(oset, Color.NONE);
        }else {
            throw new UnsupportedOperationException("Ranked cannot input oset");
        }
    }
    
    public void addRank(int state, int rank, boolean isInO) {
        if(mLvlRank != null) {
            mLvlRank.addLevelRank(state, rank, isInO);
        }else {
            throw new UnsupportedOperationException("Ordered sets cannot input rank");
        }
    }
    
    public boolean hasRanked() {
        return this.mHasRanked;
    }
    
    public boolean isFinal() {
        return this.hasRanked() && mLvlRank.getO().isEmpty();
    }
    
    /**
     * f(q) = 
     * undefined if q \notin S
     * 2Beta(q) if q \in S /\ F
     * 2Beta(q) + 1 if q \in S \ F
     * 
     * Beta(q) = |{ |v| | v \in S \ F, u < v }|
     *  the number of non-F-classes larger than q or in front of q
     * */
    public RetrospectiveRank toRank(ISet fset) {
        if(! this.mHasRanked) {
            RetrospectiveRank retroRank = new RetrospectiveRank(true);
            int num = 0;
            for(int i = 0; i < mOrdSets.getOrderedSets().size(); i ++) {
                ISet states = mOrdSets.getSet(i);
                int rank;
                if(states.overlap(fset)) {
                    rank = 2 * num;
                }else {
                    rank = 2 * num + 1;
                    num ++;
                }
                for(final int s : states) {
                    retroRank.addRank(s, rank, false);
                }
            }
            return retroRank;
        }else {
            throw new UnsupportedOperationException("Ranked cannot use toRank");
        }
    }
    
    /**
     * f'(q) = 
     * undefined if f(q) is undefined
     * 2Gamma(q) if f(q) is defined and q \in F
     * 2Gamma(q) + 1 if f(q) is defined and q \in S\ F
     * 
     * Gamma(q) be the number of odd ranks in f lower than f(q)
     * **/
    public RetrospectiveRank tighten(ISet fset) {
        if(this.mHasRanked) {
            RetrospectiveRank retroRank = new RetrospectiveRank(true);
            int[] rankNumbers = mLvlRank.countRankNumbers();
            TIntIntMap rankMap = new TIntIntHashMap();
            int num = 0;
            for(int i = 1; i <= mLvlRank.getMaximalRank(); i += 2) {
                // no states with odd rank i
                if(rankNumbers[i] <= 0 ) continue;
                rankMap.put(i, num);
                rankMap.put(i - 1, num); // for even ranks
                num ++;
            }
            for(final int s : mLvlRank.getS()) {
                int rank;
                if(fset.get(s)) {
                    rank = 2 * rankMap.get(mLvlRank.getLevelRank(s));
                }else {
                    rank = 2 * rankMap.get(mLvlRank.getLevelRank(s)) + 1;
                }
                retroRank.addRank(s, rank, LevelRanking.isEven(rank) && mLvlRank.isInO(s));
            }
            return retroRank;
        }else {
            throw new UnsupportedOperationException("Ordered sets cannot use tighten");
        }
    }
    
    @Override
    public String toString() {
        if(this.mHasRanked) {
            return mLvlRank.toString();
        }else {
            return mOrdSets.toString();
        }
    }
    
    @Override
    public int hashCode() {
        if(this.mHasRanked) {
            return mLvlRank.hashCode();
        }else {
            return mOrdSets.hashCode();
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(this.getClass().isInstance(obj)) {
            RetrospectiveRank other = (RetrospectiveRank)obj;
            if(this.hasRanked() != other.hasRanked()) {
                return false;
            }
            if(this.hasRanked()) {
                return mLvlRank.equals(other.mLvlRank);
            }else {
                return mOrdSets.equals(other.mOrdSets);
            }
        }
        return false;
    }
    

}
