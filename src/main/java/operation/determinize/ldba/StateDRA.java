package operation.determinize.ldba;

import automata.IBuchi;
import automata.StateDA;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import util.ISet;
import util.UtilISet;

public class StateDRA extends StateDA {
    
    private ParallelRuns mRuns;
    private final IBuchi mOperand;
    private final LDBA2DRA mDeterminized;
    
    public StateDRA(LDBA2DRA determinized, int id, ParallelRuns runs) {
        super(id);
        this.mOperand = determinized.getOperand();
        this.mDeterminized = determinized;
        this.mRuns = runs;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();
    
    @Override
    public int getSuccessor(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessor(letter);
        }
        mVisitedLetters.set(letter);
        // computing successors
        ISet nSuccs = UtilISet.newISet();
        ISet jSuccs = UtilISet.newISet();
        
        // compute successors of nondeterministic part
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
        ISet usedLabels = UtilISet.newISet();
        /**
         * compute the (smallest) label for every successor of deterministic part
         * */
        for(final int stateId : mRuns.getDetStates()) {
            int label = mRuns.getLabel(stateId);
            usedLabels.set(label);
            if(mDeterminized.mMaxLabel < label) {
                mDeterminized.mMaxLabel = label; 
            }
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
        // now for jSuccs, pick a label not being used
        int label = 0;
        while(usedLabels.get(label)) {
            label ++;
        }
        for(final int succId : jSuccs) {
            // ignore those successors that are already in map
            if(dSuccs.get(succId)) continue;
            map.put(succId, label);
            dSuccs.set(succId);
        }
        nSuccs.andNot(dSuccs);
        // now we compute the successor
        ParallelRuns nextRuns = new ParallelRuns(nSuccs);
        nextRuns.addLabel(map);
        //nextRuns.checkConsistency();
        StateDRA succ = mDeterminized.getOrAddState(nextRuns);
        System.out.println(getId() + " "+ mRuns + " -> " + succ.getId() + " "+ nextRuns + ": " + letter);
        super.addSuccessor(letter, succ.getId());
        return succ.getId();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof StateDRA)) {
            return false;
        }
        StateDRA other = (StateDRA)obj;
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
