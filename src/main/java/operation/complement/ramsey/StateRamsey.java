package operation.complement.ramsey;

import automata.State;
import util.ISet;
import util.UtilISet;

/**
 * <state, label>
 * state is a state in the deterministic automaton
 * label is like the indication of different accepting components
 * */
public class StateRamsey extends State {

    private final StateDAProfile mState;
    private final int mLabel;
    private final ComplementRamsey mComplement;
    
    public StateRamsey(ComplementRamsey complement, int id, StateDAProfile state, int label) {
        super(id);
        this.mComplement = complement;
        this.mState = state;
        this.mLabel = label;
    }
    
    private final ISet mVisitedLetters = UtilISet.newISet();
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        return null;
    }
    
    @Override
    public int hashCode() {
        return 31 * mState.hashCode() + mLabel;
    }
    
    @Override
    public String toString() {
        return mLabel + ":" + mState;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(obj instanceof StateRamsey) {
            StateRamsey other = (StateRamsey)obj;
            return  mState.equals(other.mState)
                 && mLabel == other.mLabel;
        }
        return false;
    }

}
