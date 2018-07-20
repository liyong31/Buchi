package operation.quotient;

import automata.IState;
import automata.State;
import operation.complement.ncsb.NCSB;
import util.ISet;
import util.UtilISet;

public class StateSimple extends State {
    
    QuotientSimple mQuotient;
    IState mRepresentor;
    ISet mEqualStates;
    
    public StateSimple(QuotientSimple quotient, int id, IState state) {
       super(id);
       this.mQuotient = quotient;
       this.mRepresentor = state;
       this.mEqualStates = UtilISet.newISet();
       this.mEqualStates.set(state.getId());
    }
    
    protected void addEqualStates(IState state) {
        this.mEqualStates.set(state.getId());
    }
    
    protected boolean contains(IState state) {
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
        for(final int state : this.mEqualStates) {
            for(final int succ : mQuotient.mOperand.getState(state).getSuccessors(letter)) {
                StateSimple stateSucc = mQuotient.getOrAddState(mQuotient.mOperand.getState(succ));
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
        if(!(obj instanceof StateSimple)){
            return false;
        }
        StateSimple other = (StateSimple)obj;
        //&& hasSameInEdges(other.mRepresentor)
        if(hasSameOutEdges(other.mRepresentor) ) {
            return true;
        }
        return this.mRepresentor.getId() == other.mRepresentor.getId();
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
//        for(int letter = 0; letter < mQuotient.getAlphabetSize(); letter ++) {
//            mHashCode = prime * mHashCode + NCSB.hashValue(mQuotient.mExplore.getPredecessors(mRepresentor.getId(), letter));
//        }
        return mHashCode;
    }
    
    private boolean hasSameOutEdges(IState other) {
        for(int letter = 0; letter < mQuotient.getAlphabetSize(); letter ++) {
            if(! mRepresentor.getSuccessors(letter).equals(other.getSuccessors(letter))) {
                return false;
            }
        }
        return true;
    }
    
//    private boolean hasSameInEdges(IState other) {
//        for(int letter = 0; letter < mQuotient.getAlphabetSize(); letter ++) {
//            if(! mQuotient.mExplore.getPredecessors(mRepresentor.getId(), letter)
//                    .equals(mQuotient.mExplore.getPredecessors(other.getId(), letter))) {
//                return false;
//            }
//        }
//        return true;
//    }

}
