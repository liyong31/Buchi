package operation.explore;

import java.util.Collection;
import java.util.LinkedList;

import automata.Buchi;
import automata.IBuchi;
import automata.IState;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import util.ISet;
import util.UtilISet;

public class ExploreBuchi extends Buchi {
    
    private final IBuchi mOperand;
    private final TIntObjectMap<TIntObjectMap<ISet>> mPredMap;
    private boolean mExplored = false;
    
    public ExploreBuchi(IBuchi operand) {
        super(operand.getAlphabetSize());
        this.mOperand = operand;
        this.mPredMap = new TIntObjectHashMap<>();
    }
    
    public ISet getPredecessors(int state, int letter) {
        TIntObjectMap<ISet> letterMap = mPredMap.get(state);
        if(letterMap == null) {
            return UtilISet.newISet();
        }
        ISet preds = letterMap.get(letter);
        if(preds == null) {
            return UtilISet.newISet();
        }
        return preds;
    }
    
    public void explore() {
        if(mExplored) return ;
        mExplored = true;
        
        LinkedList<IState> walkList = new LinkedList<>();
        for(int init : mOperand.getInitialStates()) {
            walkList.addFirst(mOperand.getState(init));
        }
        
        ISet visited = UtilISet.newISet();
        
        while(! walkList.isEmpty()) {
            IState s = walkList.remove();
            if(visited.get(s.getId())) continue;
            visited.set(s.getId());
            
            for(int letter = 0; letter < mOperand.getAlphabetSize(); letter ++) {
                for(int succ : s.getSuccessors(letter)) {
                    TIntObjectMap<ISet> letterPreds = mPredMap.get(succ);
                    ISet preds = null;
                    if(letterPreds == null) {
                        letterPreds = new TIntObjectHashMap<>();
                        preds = UtilISet.newISet();
                        letterPreds.put(letter, preds);
                        mPredMap.put(succ, letterPreds);
                    }else {
                        preds = letterPreds.get(letter);
                        preds = preds == null ? UtilISet.newISet() : preds;
                        letterPreds.put(letter, preds);
                    }
                    preds.set(s.getId());
                    if(! visited.get(succ)) {
                        walkList.addFirst(mOperand.getState(succ));
                    }
                }
            }
        }
    }
    
    @Override
    public IState addState() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public IState makeState(int id) {
        throw new UnsupportedOperationException();
    }
    
    /** should keep it safe */
    @Override
    public int addState(IState state) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IState getState(int id) {
        return mOperand.getState(id);
    }

    @Override
    public ISet getInitialStates() {
        return mOperand.getInitialStates();
    }

    @Override
    public boolean isInitial(int id) {
        return mOperand.isInitial(id);
    }

    @Override
    public boolean isFinal(int id) {
        return mOperand.isFinal(id);
    }

    @Override
    public void setInitial(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFinal(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<IState> getStates() {
        return mOperand.getStates();
    }

    @Override
    public ISet getFinalStates() {
        return mOperand.getFinalStates();
    }

    @Override
    public int getStateSize() {
        return mOperand.getStateSize();
    }

}
