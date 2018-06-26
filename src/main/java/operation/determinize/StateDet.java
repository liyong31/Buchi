package operation.determinize;

import automata.IBuchi;
import automata.State;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;
import util.ISet;
import util.UtilISet;

public class StateDet extends State {
    
    private ParallelRuns mRuns;
    private final IBuchi mOperand;
    private final Determinize mDeterminized;
    
    public StateDet(Determinize determinized, int id, ParallelRuns runs) {
        super(id);
        this.mOperand = determinized.getOperand();
        this.mDeterminized = determinized;
        this.mRuns = runs;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        // computing successors
        ISet nSuccs = UtilISet.newISet();
        ISet jSuccs = UtilISet.newISet();
        TIntIntMap map = new TIntIntHashMap();
        for(final int stateId : mRuns.getStates()) {
            int label = mRuns.getLabel(stateId);
            for(final int succId : mOperand.getState(stateId).getSuccessors(letter)) {
                nSuccs.set(succId);
                // successors from states
                if(mOperand.isFinal(stateId)) {
                    jSuccs.set(succId);
                }
                if(label != -1) {
                    if(map.containsKey(succId)) {
                        // get smaller one
                        int oldLabel = map.get(succId);
                        if(label < oldLabel) {
                            map.adjustValue(succId, label - oldLabel);
                        }
                    }else {
                        map.put(succId, label);
                    }
                }
            }
        }
        // now for jSuccs
        
        // now we compute the successor

//        super.addSuccessor(letter, succ.getId());
        return super.getSuccessors(letter);
    }

    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof StateDet)) {
            return false;
        }
        StateDet other = (StateDet)obj;
        return  mRuns.equals(other.mRuns);
    }
    
    @Override
    public String toString() {
        return mRuns.toString();
    }
    

    @Override
    public int hashCode() {
        return mRuns.hashCode();
    }

}
