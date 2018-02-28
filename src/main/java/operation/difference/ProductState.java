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

package operation.difference;

import automata.GeneralizedState;
import operation.complement.ncsb.NCSB;
import util.ISet;
import util.UtilISet;

class ProductState extends GeneralizedState {

    Difference mDifference;
    int mFstState;
    int mSndState;
    
    public ProductState(Difference difference, int fstState, int sndState, int id) {
        super(id);
        mDifference = difference;
        mFstState = fstState;
        mSndState = sndState;
    }
    
    int getFirstState() {
        return mFstState;
    }
    
    int getSecondState() {
        return mSndState;
    }
    
    private final ISet mVisitedLetters = UtilISet.newISet();
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        // compute successors
        ISet fstSuccs = mDifference.getFirstOperand().getState(mFstState).getSuccessors(letter);
        ISet sndSuccs = mDifference.getSecondComplement().getState(mSndState).getSuccessors(letter);
        final ISet succs = UtilISet.newISet();
        for(final Integer fstSucc : fstSuccs) {
            for(final Integer sndSucc : sndSuccs) {
                // pair (X, Y)
                ProductState succ = mDifference.getOrAddState(fstSucc, sndSucc);                
                this.addSuccessor(letter, succ.getId());
                succs.set(succ.getId());
            }
        }
        return succs;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(this == obj) return true;
        if(!(obj instanceof ProductState)) {
            return false;
        }
        ProductState other = (ProductState)obj;
        return mFstState == other.mFstState
            && mSndState == other.mSndState;
    }
    
    @Override
    public String toString() {
        return "(" + mFstState + "," + mDifference.getSecondComplement().getStateNCSB(mSndState) + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;
        hashCode = prime * hashCode + mFstState;
        hashCode = prime * hashCode + mSndState;
        return hashCode;
    }

    // language-wise
    public boolean coveredBy(ProductState other) {
        if(mFstState != other.mFstState)
            return false;
        NCSB fstNcsb = mDifference.getSecondComplement().getStateNCSB(mSndState).getNCSB();
        NCSB sndNcsb = mDifference.getSecondComplement().getStateNCSB(other.mSndState).getNCSB();
        return fstNcsb.coveredBy(sndNcsb);
    }
}
