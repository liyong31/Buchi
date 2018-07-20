package operation.complement.nsbc;

import automata.State;
import operation.complement.ncsb.NCSB;
import util.ISet;
import util.UtilISet;

public class StateNsbcSimilar extends State {

    QuotientNsbc mQuotient;
    StateNsbc mRepresentor;
    
    ISet mEqualStates;
    
    public StateNsbcSimilar(QuotientNsbc quotient, int id, StateNsbc state) {
       super(id);
       this.mQuotient = quotient;
       this.mRepresentor = state;
       this.mEqualStates = UtilISet.newISet();
       this.mEqualStates.set(state.getId());
    }
    
    protected void addEqualStates(StateNsbc state) {
        this.mEqualStates.set(state.getId());
    }
    
    protected boolean contains(StateNsbc state) {
        return this.mEqualStates.get(state.getId());
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        ISet succs = UtilISet.newISet();
        ComplementNsbc complement = mQuotient.mComplement;
        for(final int state : this.mEqualStates) {
            for(final int succ : complement.getState(state).getSuccessors(letter)) {
                StateNsbcSimilar stateSucc = mQuotient.getOrAddState(complement.getStateNsbc(succ));
                super.addSuccessor(letter, stateSucc.getId());
                succs.set(stateSucc.getId());
            }
        }
        
        return succs;
    }
    
    @Override
    public String toString() {
        return mRepresentor.toString() + ":" + mEqualStates;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(!(obj instanceof StateNsbcSimilar)){
            return false;
        }
        StateNsbcSimilar other = (StateNsbcSimilar)obj;
        if(mRepresentor.isColored() != other.mRepresentor.isColored()) {
            return false;
        }
        if(mQuotient.mComplement.isFinal(mRepresentor.getId()) != mQuotient.mComplement.isFinal(other.mRepresentor.getId())) {
            return false;
        }
        if(hasSameOutEdges(other.mRepresentor)) {
            return true;
        }
        return this.mRepresentor.equals(other.mRepresentor);
    }
    
    protected boolean hasCode = false;
    protected int mHashCode;
    @Override
    public int hashCode() {
        if(hasCode) return mHashCode;
        hasCode = true;
        final int prime = 31;
        mHashCode = 1;
        for(int letter = 0; letter < mQuotient.getAlphabetSize(); letter ++) {
            mHashCode = prime * mHashCode + NCSB.hashValue(mRepresentor.getSuccessors(letter));
        }
        return mHashCode;
    }
    
    private boolean hasSameOutEdges(StateNsbc other) {
        for(int letter = 0; letter < mQuotient.getAlphabetSize(); letter ++) {
            if(! mRepresentor.getSuccessors(letter).equals(other.getSuccessors(letter))) {
                return false;
            }
        }
        return true;
    }

}