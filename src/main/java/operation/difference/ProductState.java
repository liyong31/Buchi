package operation.difference;

import automata.GeneralizedState;
import operation.complement.NCSB;
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
