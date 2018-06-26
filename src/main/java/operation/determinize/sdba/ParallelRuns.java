package operation.determinize.sdba;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;

import util.ISet;
import util.UtilISet;

/**
 * TODO: in order to make it unmodifiable
 * */
public class ParallelRuns {
	
    private final ISet mNondets; // subset construction
    private final ISet mDets; //
    private final TIntIntMap mRuns;  // state to label
        
    public ParallelRuns(ISet nondets) {
        this.mNondets = nondets;
        this.mDets = UtilISet.newISet();
        this.mRuns = new TIntIntHashMap();
    }
    
    public ISet getNondetStates() {
        return mNondets;
    }
    
    public ISet getDetStates() {
        return mDets;
    }
    
    public int getLabel(int state) {
        assert mDets.get(state);
        if(mRuns.containsKey(state)) {
            return mRuns.get(state);
        }
        return -1;
    }
    
    public void addLabel(int state, int label) {
        mDets.set(state);
        mRuns.put(state, label);
    }
    
    // state label
    public void addLabel(TIntIntMap map) {
        TIntIntProcedure procedure = new TIntIntProcedure() {
            @Override
            public boolean execute(int state, int label) {
                addLabel(state, label);
                return true;
            }
        };
        map.forEachEntry(procedure);
    }
    
    @Override
    public boolean equals(Object other) {
        if(this == other) return true;
        if(! (other instanceof ParallelRuns)) {
            return false;
        }
        ParallelRuns otherRuns = (ParallelRuns)other;
        return  this.mNondets.equals(otherRuns.mNondets) && 
                this.mRuns.equals(otherRuns.mRuns);
    }
    
    public static int hashValue(ISet set) {
        final int prime = 31;
        int result = 1;
        for(final int n : set) {
            result = prime * result + n;
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + hashValue(mNondets);
        for(final int state : mDets) {
            result = prime * result + state;
            result = prime * result + mRuns.get(state);
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "<" + mNondets + ", " + this.mRuns + ">"; 
    }

}
