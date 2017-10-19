package operation.isempty;

import java.util.Comparator;

class SuccessorInfo implements Comparator<SuccessorInfo>{
    final int mState;
    int mPreState;
    int mLetter;
    int mDistance;
    
    SuccessorInfo(int state) {
        mState = state;
        mDistance = Integer.MAX_VALUE;
    }
    
    boolean unreachable() {
        return mDistance == Integer.MAX_VALUE;
    }

    @Override
    public int compare(SuccessorInfo arg0, SuccessorInfo arg1) {
        return arg0.mDistance - arg1.mDistance;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(! (obj instanceof SuccessorInfo)) {
            return false;
        }
        SuccessorInfo other = (SuccessorInfo)obj;
        return other.mState == mState;
    }
    
    @Override
    public String toString() {
        return "<" + mState + "," + mPreState + "," + mLetter + "," + mDistance + ">";
    }
    
}
