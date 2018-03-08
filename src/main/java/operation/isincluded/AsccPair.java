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

package operation.isincluded;

import operation.complement.ncsb.ComplementNcsb;
import operation.complement.ncsb.StateNCSB;

class AsccPair {
    
    protected int mFstState;
    protected int mSndState;
    protected ComplementNcsb mSndComplement;
    protected int mDfsnum;
    protected boolean mCurrent;
    
    AsccPair(int fstState, int sndState, ComplementNcsb sndComplement) {
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
