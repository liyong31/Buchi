package operation.determinize.sdba;

import automata.IBuchi;
import automata.State;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import util.ISet;
import util.UtilISet;

public class StateDet extends State {
    
    private ParallelRuns mRuns;
    private final IBuchi mOperand;
    private final DeterminizeSDBA mDeterminized;
    
    public StateDet(DeterminizeSDBA determinized, int id, ParallelRuns runs) {
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
        for(final int stateId : mRuns.getNondetStates()) {
            for(final int succId : mOperand.getState(stateId).getSuccessors(letter)) {
                if(mOperand.isFinal(succId)) {
                    jSuccs.set(succId);
                }else {
                    nSuccs.set(succId);
                }
            }
        }
        // now the nSuccs has been fixed, we have to compute the successors of D
        ISet dSuccs = UtilISet.newISet();
        TIntIntMap map = new TIntIntHashMap();
        ISet labels = UtilISet.newISet();
        for(final int stateId : mRuns.getDetStates()) {
            int label = mRuns.getLabel(stateId);
            labels.set(label);
            for(final int succId : mOperand.getState(stateId).getSuccessors(letter)) {
                // set the smallest label
                if(map.containsKey(succId)) {
                    int oldLabel = map.get(succId);
                    if(oldLabel > label) {
                        map.adjustValue(succId, label-oldLabel);
                    }
                }else {
                    map.put(succId, label);
                }
                dSuccs.set(succId);
            }
        }

        // now for jSuccs, every time it will be the same
        for(final int succId : jSuccs) {
            // ignore those successors that are already in map
            if(dSuccs.get(succId)) continue;
            int label = 0;
            while(labels.get(label)) {
                label ++;
            }
            map.put(succId, label);
            dSuccs.set(succId);
            labels.set(label);
        }
        nSuccs.andNot(dSuccs);
        // now we compute the successor
        ParallelRuns nextRuns = new ParallelRuns(nSuccs);
        nextRuns.addLabel(map);
        StateDet succ = mDeterminized.getOrAddState(nextRuns);
        
        System.out.println(getId() + " "+ mRuns + " -> " + succ.getId() + " "+ nextRuns + ": " + letter);
        super.addSuccessor(letter, succ.getId());
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
