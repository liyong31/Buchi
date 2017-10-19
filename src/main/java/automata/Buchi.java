package automata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import util.ISet;
import util.UtilISet;

public class Buchi implements IBuchi {

    private final ISet mInitStates;
    
    private final ISet mFinalStates;
    
    private final List<IState> mStates;
    
    private final int mAlphabetSize;
        
    public Buchi(int alphabetSize) {
        this.mAlphabetSize = alphabetSize;
        this.mInitStates  = UtilISet.newISet();
        this.mFinalStates = UtilISet.newISet();
        this.mStates = new ArrayList<>();
    }
    
    @Override
    public int getAlphabetSize() {
        return mAlphabetSize;
    }

    @Override
    public IState addState() {
        int id = mStates.size();
        mStates.add(makeState(id));
        return mStates.get(id);
    }
    
    @Override
    public IState makeState(int id) {
        return new State(id);
    }
    
    /** should keep it safe */
    @Override
    public int addState(IState state) {
        int id = mStates.size();
        mStates.add(state);
        return id;
    }

    @Override
    public IState getState(int id) {
        assert id < mStates.size();
        if(id < mStates.size()) {
            return mStates.get(id);
        }
        return null;
    }

    @Override
    public ISet getInitialStates() {
        return mInitStates;
    }

    @Override
    public boolean isInitial(int id) {
        return mInitStates.get(id);
    }

    @Override
    public boolean isFinal(int id) {
        return mFinalStates.get(id);
    }

    @Override
    public void setInitial(int id) {
        mInitStates.set(id);
    }

    @Override
    public void setFinal(int id) {
        mFinalStates.set(id);
    }

    @Override
    public Collection<IState> getStates() {
        return Collections.unmodifiableList(mStates);
    }

    @Override
    public ISet getFinalStates() {
        return mFinalStates;
    }

    @Override
    public int getStateSize() {
        return mStates.size();
    }
    
    public String toString() {
        return toDot();
    }

}
