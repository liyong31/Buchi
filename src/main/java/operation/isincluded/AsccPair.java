package operation.isincluded;

import operation.complement.Complement;
import operation.complement.StateNCSB;

class AsccPair {
    
    protected int mFstState;
    protected int mSndState;
    protected Complement mSndComplement;
    protected int mDfsnum;
    protected boolean mCurrent;
    
    AsccPair(int fstState, int sndState, Complement sndComplement) {
        mFstState = fstState;
        mSndState = sndState;
        mSndComplement = sndComplement;
        mDfsnum = 0;
        mCurrent = false;
    }
    
    protected int getFstState() {
        return mFstState;
    }
    
    protected int getSndState() {
        return mSndState;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof AsccPair)) {
            return false;
        }
        AsccPair other = (AsccPair)obj;
        return mFstState == other.mFstState
            && mSndState == other.mSndState;
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + mFstState;
        result = 31 * result + mSndState;
        return result;
    }
    
    protected StateNCSB getSndComplementState() {
        return (StateNCSB) mSndComplement.getState(mSndState);
    }
    
    protected boolean coveredBy(AsccPair other) {
        if(mFstState != other.mFstState) return false;
        StateNCSB state = this.getSndComplementState();
        StateNCSB otherState = other.getSndComplementState();
        return state.getNCSB().coveredBy(otherState.getNCSB());
    }
    

}
