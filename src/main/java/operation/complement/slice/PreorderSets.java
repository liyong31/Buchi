package operation.complement.slice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.ISet;

public class PreorderSets {
    
    //<S, <>
    protected final ArrayList<ISet> mSets; // left most are successors of final states
    // finite or not
    protected Map<Integer, Boolean> mGuess;
    // O breakpoint construction
    protected ISet mO;
    protected boolean mB;
    
    public PreorderSets(ArrayList<ISet> osets) {
        this.mSets = osets;
    }
    
    public void setGuess(Map<Integer, Boolean> guess) {
        this.mGuess = guess;
    }
    
    public void setO(ISet O) {
        this.mO = O;
    }
    
    public void setB(boolean b) {
        this.mB = b;
    }
    
    public List<ISet> getOrderedSets() {
        return mSets;
    }
    
    public Map<Integer, Boolean> getGuess() {
        return mGuess;
    }
    
    public ISet getO() {
        return mO;
    }
    
    public boolean getB() {
        return mB;
    }
    
    public boolean isAccepting() {
        return mO.isEmpty() && mB;
    }
    
    @Override
    public boolean equals(Object other) {
        if(this == other) return true;
        if(! (other instanceof PreorderSets)) {
            return false;
        }
        PreorderSets otherRuns = (PreorderSets)other;
        return  this.mSets.equals(otherRuns.mSets) && 
                this.mGuess.equals(otherRuns.mGuess) &&
                this.mO.equals(otherRuns.mO) &&
                this.mB == otherRuns.mB;
    }
    
    private int hashValue(ISet set, boolean lambda) {
        final int prime = 31;
        int result = 1;
        for(final int n : set) {
            result = prime * result + n;
            if(lambda) 
                result = prime * result + (mGuess.get(n) ? 1 : 0);
        }
        return result;
    }
    
    private boolean mHasCode = false;
    private int mHashCode;
    @Override
    public int hashCode() {
        if(mHasCode) return mHashCode;
        mHasCode = true;
        final int prime = 31;
        int result = 1;
        for(final ISet set : mSets) {
            result = prime * result + hashValue(set, true);
        }
        result = prime * result + hashValue(mO, false);
        result = prime * result + (mB ? 1 : 0);
        mHashCode = result;
        return result;
    }
    
    @Override
    public String toString() {
        return "<" + mSets + ", " + this.mGuess + ", " + this.mO + "," + mB + ">"; 
    }


}
