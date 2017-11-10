package automata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import util.ISet;
import util.UtilISet;

public class Gba implements IGba {
    
    protected int mAccSize;
    protected final ISet mInitialStates;
    protected final ISet mFinalStates;
    protected final List<IGbaState> mStates;
    private final int mAlphabetSize;
    
    public Gba(int alphabetSize) {
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
        IGbaState state = makeState(getStateSize());
        mStates.add(state);
        return state;
    }

    @Override
    public IGbaState makeState(int id) {
        return new GbaState(id);
    }

    @Override
    public int addState(IState state) {
        assert state.getId() == getStateSize();
        assert state instanceof IGbaState;
        IGbaState gbaState = (IGbaState)state;
        mStates.add(gbaState);
        return state.getId();
    }

    @Override
    public IGbaState getState(int id) {
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
    
    public void setFinal(int state, int index) {
        getState(state).setFinal(index);
    }
    
    public void setAccSize(int size) {
        mAccSize = size;
    }

    @Override
    public ISet getAccSet(int state) {
        return getState(state).getAccSet();
    }

}
