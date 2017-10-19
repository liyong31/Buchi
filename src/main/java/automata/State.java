package automata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import util.ISet;
import util.UtilISet;


public class State implements IState, Comparable<State> {

    private final int mId;
    private final Map<Integer, ISet> mSuccessors;
    public State(int id) {
        this.mId = id;
        this.mSuccessors = new HashMap<>();
    }
    
    @Override
    public int getId() {
        return mId;
    }

    @Override
    public void addSuccessor(int letter, int state) {
        ISet succs = mSuccessors.get(letter);
        if(succs == null) {
            succs =  UtilISet.newISet();
        }
        succs.set(state);
        mSuccessors.put(letter, succs);
    }

    @Override
    public ISet getSuccessors(int letter) {
        ISet succs = mSuccessors.get(letter);
        if(succs == null) { // transition function may not be complete
            return UtilISet.newISet();
        }
        return succs.clone();
    }

    @Override
    public Set<Integer> getEnabledLetters() {
        return Collections.unmodifiableSet(mSuccessors.keySet());
    }

    @Override
    public int compareTo(State other) {
        return mId - other.mId;
    }
    
    @Override
    public boolean equals(Object other) {
        if(this == other) return true;
        if(!(other instanceof State)) {
            return false;
        }
        State otherState = (State)other;
        return otherState.mId == this.mId;
    }
    
    @Override
    public int hashCode() {
        return mId;
    }
    
    @Override
    public String toString() {
        return "s" + mId;
    }

}
