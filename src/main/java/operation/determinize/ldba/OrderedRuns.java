package operation.determinize.ldba;

import java.util.ArrayList;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;

import util.ISet;
import util.UtilISet;

/**
 * TODO: in order to make it unmodifiable
 * */
class OrderedRuns {
	
    protected final ISet mNondets; // subset construction
    protected final ISet mDets; //
    protected final ArrayList<Integer> mOrds;
    protected final TIntIntMap mIndices;  // state to index
        
    public OrderedRuns(ISet nondets) {
        this.mNondets = nondets;
        this.mDets = UtilISet.newISet();
        this.mOrds = new ArrayList<>();
        this.mIndices = new TIntIntHashMap();
    }
    
    public ISet getNondetStates() {
        return mNondets;
    }
    
    public ArrayList<Integer> getOrdDetStates() {
        return mOrds;
    }
    
    public int getIndex(int state) {
        assert mDets.get(state);
        if(mIndices.containsKey(state)) {
            return mIndices.get(state);
        }
        return -1;
    }
    
    public void addDetState(int state) {
        int index = mOrds.size();
        mOrds.add(state);
        mIndices.put(state, index);
    }
    
    @Override
    public boolean equals(Object other) {
        if(this == other) return true;
        if(! (other instanceof OrderedRuns)) {
            return false;
        }
        OrderedRuns otherRuns = (OrderedRuns)other;
        return  this.mNondets.equals(otherRuns.mNondets) && 
                this.mOrds.equals(otherRuns.mOrds);
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
        for(final int state : mOrds) {
            result = prime * result + state;
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "<" + mNondets + ", " + this.mOrds + ">"; 
    }

}
