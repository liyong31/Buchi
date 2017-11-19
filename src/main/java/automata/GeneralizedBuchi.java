package automata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import util.ISet;
import util.UtilISet;

public class GeneralizedBuchi implements IGeneralizedBuchi {
    
    protected int mAccSize;
    protected final ISet mInitialStates;
    protected final ISet mFinalStates;
    protected final List<IGeneralizedState> mStates;
    private final int mAlphabetSize;
    
    public GeneralizedBuchi(int alphabetSize) {
        mAlphabetSize = alphabetSize;
        mInitialStates = UtilISet.newISet();
        mFinalStates = UtilISet.newISet();
        mStates = new ArrayList<>();
    }

    @Override
    public int getStateSize() {
        return mStates.size();
    }

    @Override
    public int getAlphabetSize() {
        return mAlphabetSize;
    }

    @Override
    public IState addState() {
        IGeneralizedState state = makeState(getStateSize());
        mStates.add(state);
        return state;
    }

    @Override
    public IGeneralizedState makeState(int id) {
        return new GeneralizedState(id);
    }

    @Override
    public int addState(IState state) {
        assert state.getId() == getStateSize();
        assert state instanceof IGeneralizedState;
        IGeneralizedState gbaState = (IGeneralizedState)state;
        mStates.add(gbaState);
        return state.getId();
    }

    @Override
    public IGeneralizedState getState(int id) {
        assertValidState(id);
        return mStates.get(id);
    }

    @Override
    public ISet getInitialStates() {
        return mInitialStates;
    }

    @Override
    public ISet getFinalStates() {
        return mFinalStates;
    }

    @Override
    public boolean isInitial(int id) {
        assertValidState(id);
        return mInitialStates.get(id);
    }

    @Override
    public void setInitial(int id) {
        assertValidState(id);
        mInitialStates.set(id);
    }
    
    private void assertValidState(int id) {
        assert id >= 0 && id < getStateSize();
    }

    @Override
    public Collection<IState> getStates() {
        return Collections.unmodifiableList(mStates);
    }

    @Override
    public int getAccSize() {
        return mAccSize;
    }
    
    @Override
    public void setFinal(int state, int index) {
        assertValidState(state);
        getState(state).setFinal(index);
    }
    
    @Override
    public void setAccSize(int size) {
        mAccSize = size;
    }

    @Override
    public ISet getAccSet(int state) {
        assertValidState(state);
        return getState(state).getAccSet();
    }

}
