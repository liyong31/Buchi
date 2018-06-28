package operation.determinize.ldba;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;

import util.ISet;
import util.UtilISet;

/**
 * TODO: in order to make it unmodifiable
 * */
class ParallelRuns {
	
    protected final ISet mNondets; // subset construction
    protected final ISet mDets; //
    protected final TIntIntMap mRuns;  // state to label, one label can be used by multiple states
        
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
    
    protected void checkConsistency() {
        for(final int state : this.getDetStates()) {
            assert this.getLabel(state) != -1;
        }
        TIntIntIterator iter = mRuns.iterator(); 
        while(iter.hasNext()) {
            iter.advance();
            assert mDets.get(iter.key());
        }
    }

}
