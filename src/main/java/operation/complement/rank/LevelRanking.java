/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi which is a simple version of SemiBuchi.
 * 
 * Buchi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Buchi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Buchi. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package operation.complement.rank;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import operation.complement.ncsb.NCSB;
import util.ISet;
import util.UtilISet;

/**
 * representation for (S, O, f, i)
 * 
 * @S the set of states reached from the initial states
 * 
 * @O the set of states for breakpoint construction
 * 
 * @f the level ranking function
 * 
 * @i the turn in Sven's paper for O states
 * 
 * **/

public class LevelRanking {

    protected final TIntIntMap mRanks;   // ranking function
    protected static final int TWO = 2;
    protected static final int ONE = 1;
    protected static final int ZERO = 0;
    protected int mMaxRank;             
    protected final ISet mSSet;
    protected final ISet mOSet;
    protected int mTurn;
    protected final boolean mIsRanked;
    protected final boolean mIsTurnwised;
    
    /**
     * set whether it is ranked and turnwised
     * **/
    public LevelRanking(boolean isRanked, boolean isTurnwised) {
        mIsRanked = isRanked;
        mIsTurnwised = isTurnwised;
        mRanks = new TIntIntHashMap();
        mMaxRank = -1;
        mTurn = 0;                   // by default 0
        mSSet = UtilISet.newISet();
        mOSet = UtilISet.newISet();
    }
    
    // for ranked states only
    public void addLevelRank(int state, int rank, boolean isInO) {
        if(! mIsRanked ) {
            throw new UnsupportedOperationException("addLevelRank for ranked states only");
        }
        mRanks.put(state, rank);
        mSSet.set(state);
        if (mMaxRank < rank) {
            mMaxRank = rank;
        }
        if(isInO) {
            mOSet.set(state);
        }
    }
    
    public void addToO(int state) {
        assert mRanks.containsKey(state);
        mOSet.set(state);
    }
    
    // for unranked states only
    public void setS(ISet s) {
        if(mIsRanked ) {
            throw new UnsupportedOperationException("setS for unranked states only");
        }
        mSSet.clear();
        mSSet.or(s);
    }
    
    public void setTurn(int turn) {
        if(!mIsRanked || !mIsTurnwised) {
            throw new UnsupportedOperationException("setTurn for ranked and turnwised states only");
        }
        mTurn = turn;
    }
    
    public int getTurn() {
        assert mIsTurnwised;
        return mTurn;
    }
    
    public int getLevelRank(int state) {
        if(mRanks.containsKey(state)) {
            return mRanks.get(state);
        }
        return -1;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(this == obj) return true;
        if(obj instanceof LevelRanking) {
            LevelRanking other = (LevelRanking)obj;
            if(mIsRanked != other.isRanked()) {
                return false;
            }
            if(mIsTurnwised != other.isTurnwised()) {
                return false;
            }
            if(mTurn != other.getTurn()) {
                return false;
            }
            ISet S = copyS();
            ISet otherS = other.copyS();
            if(S.cardinality() != otherS.cardinality()) {
                return false;
            }
            if(!S.equals(otherS)) return false;
            if(mIsRanked) {
                for(final int state : S) {
                    if(mRanks.get(state) != other.mRanks.get(state)) {
                        return false;
                    }
                }
                return mOSet.equals(other.mOSet);
            }else {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public LevelRanking clone() {
        LevelRanking copy = new LevelRanking(this.mIsRanked, this.mIsTurnwised);
        if(this.mIsRanked) {
            for(int state : copyS()) {
                copy.addLevelRank(state, mRanks.get(state), isInO(state));
            }
            if(this.isTurnwised()) copy.setTurn(mTurn);
        }else {
            copy.setS(mSSet);
        }
        return copy;
    }
    
    public ISet getS() {
        return mSSet;
    }

    public ISet copyS() {
        return mSSet.clone();
    }
    
    public ISet getO() {
        return mOSet;
    }
    
    public ISet copyO() {
        return mOSet.clone();
    }
    
    public boolean isTurnwised() {
        return mIsTurnwised;
    }
    
    public boolean isFinal() {
        return (isRanked() && isOEmpty())
             || (!isRanked() && mSSet.isEmpty());
    }

    
    boolean isInO(int state) {
        return mOSet.get(state);
    }
    
    boolean isRanked() {
        return mIsRanked;
    }
    
    boolean isOEmpty() {
        return mOSet.isEmpty();
    }
    
    public int getMaximalRank() {
        return mMaxRank;
    }
    
    @Override
    public String toString() {
        if(isRanked()) {
            if(isTurnwised()) {
                return "(f=" + mRanks + " , O=" + mOSet + " "+ mTurn + ")";  
            }else {
                return "(f=" + mRanks + " , O=" + mOSet + ")";                  
            }
        }else {
            return "" + mSSet;
        }
    }
    
    protected boolean mHasCode = false;
    protected int mHashCode ;
    @Override
    public int hashCode() {
        if(mHasCode) {
            return mHashCode;
        }else {
            mHashCode = mRanks.hashCode();
            mHashCode = mHashCode * 31 + mTurn;
            mHashCode = mHashCode * 31 + NCSB.hashValue(mSSet);
            mHashCode = mHashCode * 31 + NCSB.hashValue(mOSet);
            return mHashCode;            
        }
    }
    
    // ------------------------------------------------------------------------
    /**
     * Count the number of ranks occurred in the function
     * */
    private int[] countRankNumbers() {
        assert mMaxRank >= 0;
        assert mMaxRank < Integer.MAX_VALUE : "ERROR RANKS";
        final int[] ranks = new int[mMaxRank + 1];
        for(final int s : getS()) {
            int rank = mRanks.get(s);
            ranks[rank]++;
        }
        return ranks;
    } 
    
    /**
     * the tight definition is in FKV paper
     * **/
    boolean isTight() {
        assert mMaxRank >= 0;
        assert mMaxRank < Integer.MAX_VALUE : "ERROR RANKS";
        // maximal rank should be odd
        if (isEven(mMaxRank)) {
            return false;
        }
        // all odd ranks less than maximal rank should exist
        final int[] ranks = countRankNumbers();
        for (int i = 1; i <= mMaxRank; i += TWO) {
            if (ranks[i] == 0) {
                return false;
            }
        }
        return true;
    }
    
    /*
     * Sven's STACS paper 
     * */
    boolean isMaximallyTight() {
        assert mMaxRank >= 0;
        assert mMaxRank < Integer.MAX_VALUE : "ERROR RANKS";
        // 
        if (isEven(mMaxRank)) {
            return false;
        }
        final int[] ranks = countRankNumbers();
        for (int i = 1; i < mMaxRank; i += TWO) {
            if (ranks[i] != 1) {
                return false;
            }
        }
        
        if (ranks[mMaxRank] == 0) {
            return false;
        }
        for (int i = 0; i < mMaxRank - 1; i += TWO) {
            if (ranks[i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    // static helper functions
    public static boolean isEven(int number) {
        if((number & 1) == 0) {
            return true;
        }
        return false;
    }
    
    public static boolean isOdd(int number) {
        if((number & 1) != 0) {
            return true;
        }
        return false;
    }

    
}
