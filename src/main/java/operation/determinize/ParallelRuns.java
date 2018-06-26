package operation.determinize;

import java.util.HashMap;
import java.util.Map;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;
import util.ISet;
import util.UtilISet;

/**
 * There is a mLabel for every run (mState)
 * every state only has one label
 * */
public class ParallelRuns {
    private final ISet mStates; // subset construction
    private final ISet mLabels; // all labels
    private final Map<Integer, ISet> mRuns;
    private final TIntIntMap mLabelMap;
        
    public ParallelRuns(ISet states) {
        this.mStates = states;
        this.mLabels = UtilISet.newISet();
        this.mRuns = new HashMap<>();
        this.mLabelMap = new TIntIntHashMap();
    }
    
    public ISet getStates() {
        return mStates;
    }
    
    public ISet getLabels() {
        return mLabels;
    }
    
    public int getLabel(int state) {
        if(mLabelMap.containsKey(state)) {
            return mLabelMap.get(state);
        }
        return -1;
    }
    
    public ISet getStates(int label) {
        ISet states = mRuns.get(label);
        if(states == null) {
            return UtilISet.newISet();
        }else {
            return states;
        }
    }
    
    public void addLabel(int state, int label) {
        assert mStates.get(state);
        mLabels.set(label);
        mLabelMap.put(state, label);
        if(mRuns.containsKey(label)) {
            ISet states = mRuns.get(label);
            states.set(state);
        }else {
            ISet states = UtilISet.newISet();
            states.set(state);
            mRuns.put(label, states);
        }
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
        return  this.mStates.equals(otherRuns.mStates) && 
                this.mLabelMap.equals(otherRuns.mLabelMap);
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
        result = prime * result + hashValue(mStates);
        for(final int label : mLabels) {
            result = prime * result + label;
            ISet set = mRuns.get(label);
            if(set != null) result = prime * result + hashValue(set);
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "<" + mStates + ", " + this.mRuns + ">"; 
    }
    
    public static final int EMPTY_DOWN_STATE = -1;

}

