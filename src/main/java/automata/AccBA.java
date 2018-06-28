package automata;

import util.ISet;
import util.UtilISet;

// Buechi automata

public class AccBA implements IAcc {
    
    private final ISet mFinalStates;
    
    public AccBA() {
        this.mFinalStates = UtilISet.newISet();
    }

    @Override
    public AccType getType() {
        return AccType.BUCHI;
    }
    
    public void setFinal(int state) {
        mFinalStates.set(state);
    }
    
    public boolean isFinal(int state) {
        return mFinalStates.get(state);
    }
    
    public ISet getFinalStates() {
        return mFinalStates;
    }

    @Override
    public void simplify() {
        
    }

}
