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
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
import util.ISet;
import util.UtilISet;

/**
 * representation for (S, O, f)
 * **/

public class LevelRankingState {

    protected final TIntIntMap mLevelRankings;
    protected static final int TWO = 2;
    protected static final int ONE = 1;
    protected static final int ZERO = 0;
    protected int mMaxRank;
    protected final ISet mOSet;
    
    public LevelRankingState() {
        mLevelRankings = new TIntIntHashMap();
        mMaxRank = -1;
        this.mOSet = UtilISet.newISet();
    }
    
    
    public void addLevelRank(int state, int rank, boolean isInO) {
        mLevelRankings.put(state, rank);
        if (mMaxRank < rank) {
            mMaxRank = rank;
        }
        if(isInO) {
            mOSet.set(state);
        }
    }
    
    public void addToO(int state) {
        assert mLevelRankings.containsKey(state);
        mOSet.set(state);
    }

    
    public int getLevelRank(int state) {
        if(mLevelRankings.containsKey(state)) {
            return mLevelRankings.get(state);
        }
        return -1;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(this == obj) return true;
        if(obj instanceof LevelRankingState) {
            LevelRankingState other = (LevelRankingState)obj;
            ISet S = getS();
            ISet otherS = other.getS();
            if(S.cardinality() != otherS.cardinality()) return false;
            if(!S.equals(otherS)) return false;
            for(final int state : S) {
                if(mLevelRankings.get(state)
                  != other.mLevelRankings.get(state)) {
                    return false;
                }
            }
            return mOSet.equals(other.mOSet);
        }
        return false;
    }
    
    @Override
    public LevelRankingState clone() {
        LevelRankingState copy = new LevelRankingState();
        TIntIntProcedure procedure = new TIntIntProcedure() {
            @Override
            public boolean execute(int state, int rank) {
                copy.addLevelRank(state, rank, mOSet.get(state));
                return true;
            }
        };
        mLevelRankings.forEachEntry(procedure);
        return copy;
    }

    public ISet getS() {
        ISet keys = UtilISet.newISet();
        TIntProcedure procedure = new TIntProcedure() {
            @Override
            public boolean execute(int state) {
                keys.set(state);
                return true;
            }
        };
        mLevelRankings.forEachKey(procedure);
        return keys;
    }
    
    public ISet getO() {
        return mOSet.clone();
    }
    
    boolean isInO(int state) {
        return mOSet.get(state);
    }
    
    boolean isOEmpty() {
        return mOSet.isEmpty();
    }
    
    public int getMaximalRank() {
        return mMaxRank;
    }
    
    @Override
    public String toString() {
        return "S=" + mLevelRankings + " : O =" + mOSet;
    }
    
    @Override
    public int hashCode() {
        int p = mLevelRankings.hashCode();
        return p * 31 + hashValue(mOSet);
    }
    
    public static int hashValue(ISet set) {
        final int prime = 31;
        int result = 1;
        for(final int n : set) {
            result = prime * result + n;
        }
        return result;
    }
    
    
    
    /**
     * Count the number of ranks occurred in the function
     * */
    private int[] countRankNumbers() {
        assert mMaxRank >= 0;
        assert mMaxRank < Integer.MAX_VALUE : "ERROR RANKS";
        final int[] ranks = new int[mMaxRank + 1];
        TIntProcedure procedure = new TIntProcedure() {
            @Override
            public boolean execute(int rank) {
                ranks[rank]++;
                return true;
            }
        };
        mLevelRankings.forEachValue(procedure);
        return ranks;
    } 
    
    /**
     * the tight definition is in FKV paper
     * 
     * **/
    boolean isTight() {
        assert mMaxRank >= 0;
        assert mMaxRank < Integer.MAX_VALUE : "ERROR RANKS";
        // maximal rank should be odd
        if (isEven(mMaxRank)) {
            return false;
        }
        final int[] ranks = countRankNumbers();
        for (int i = 1; i <= mMaxRank; i += TWO) {
            if (ranks[i] == 0) {
                return false;
            }
        }
        // all odd ranks less than maximal rank should exist
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
