package operation.complement.nsbc;

import automata.State;
import operation.complement.ncsb.NCSB;
import util.ISet;
import util.UtilISet;

public class StateNsbcSimilar extends State {

    SimulatorNsbc mSimulator;
    StateNsbc mState;
    
    public StateNsbcSimilar(SimulatorNsbc simulator, int id, StateNsbc state) {
       super(id);
       this.mSimulator = simulator;
       this.mState = state;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        ISet succs = UtilISet.newISet();
        ComplementNsbc complement = mSimulator.mComplement;
        for(final int succ : complement.getState(mState.getId()).getSuccessors(letter)) {
            StateNsbcSimilar stateSucc = mSimulator.getOrAddState(complement.getStateNsbc(succ));
            super.addSuccessor(letter, stateSucc.getId());
            succs.set(stateSucc.getId());
        }
        return succs;
    }
    
    @Override
    public String toString() {
        return mState.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        
        if(this == obj) return true;
        if(obj == null) return false;
        if(!(obj instanceof StateNsbcSimilar)){
            return false;
        }
        StateNsbcSimilar other = (StateNsbcSimilar)obj;
        if(mState.isColored() != other.mState.isColored()) {
            return false;
        }
        if(! mState.isColored()) {
            return this.mState.equals(other.mState);
        }else {
            if(!mState.mNsbc.getNSet().equals(other.mState.mNsbc.getNSet())) {
                return false;
            }
            if(!mState.mNsbc.getSSet().equals(other.mState.mNsbc.getSSet())) {
                return false;
            }
            if(mState.mNsbc.isFinal() != other.mState.mNsbc.isFinal()) {
                return false;
            }
            ISet union1 = mState.mNsbc.copyBSet();
            union1.or(mState.mNsbc.getCSet());
            ISet union2 = other.mState.mNsbc.copyBSet();
            union2.or(other.mState.mNsbc.getCSet());
            return union1.equals(union2);
        }
    }
    
    protected boolean hasCode = false;
    protected int mHashCode;
    @Override
    public int hashCode() {
        if(!this.mState.isColored()) {
            return mState.hashCode();
        }else {
            if(hasCode) return mHashCode;
            else {
                hasCode = true;
                mHashCode = 1;
                final int prime = 31;
                mHashCode = prime * mHashCode + NCSB.hashValue(mState.mNsbc.getNSet());
                mHashCode = prime * mHashCode + NCSB.hashValue(mState.mNsbc.getSSet());
                ISet union = mState.mNsbc.copyBSet();
                union.or(mState.mNsbc.getCSet());
                mHashCode = prime * mHashCode + NCSB.hashValue(union);
                mHashCode = prime * mHashCode + (mState.mNsbc.isFinal()? 1 : 0);
                return mHashCode;
            }
        }
    }

}
