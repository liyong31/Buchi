package automata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StateDA implements IS, Comparable<StateDA>{

    private final int mId;
    private final Map<Integer, Integer> mSuccessors;
    public StateDA(int id) {
        this.mId = id;
        this.mSuccessors = new HashMap<>();
    }
    
    @Override
    public int compareTo(StateDA other) {
        return mId - other.mId;
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public void addSuccessor(int letter, int state) {
        mSuccessors.put(letter, state);
    }
    
    public int getSuccessor(int letter) {
        Integer state = mSuccessors.get(letter);
        if(state == null) {
            return -1;
        }
        return state;
    }
    
    @Override
    public Set<Integer> getEnabledLetters() {
        return Collections.unmodifiableSet(mSuccessors.keySet());
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
