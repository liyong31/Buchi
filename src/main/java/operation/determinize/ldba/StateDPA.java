package operation.determinize.ldba;

import java.util.ArrayList;

import automata.IBuchi;
import automata.StateDA;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntProcedure;
import util.ISet;
import util.UtilISet;

public class StateDPA extends StateDA {
    
    // its ordering, not labels
    private OrderedRuns mORuns;
    private final IBuchi mOperand;
    private final LDBA2DPA mDeterminized;
    
    public StateDPA(LDBA2DPA determinized, int id, OrderedRuns runs) {
        super(id);
        this.mOperand = determinized.getOperand();
        this.mDeterminized = determinized;
        this.mORuns = runs;
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
        for(final int stateId : mORuns.getNondetStates()) {
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
        /**
         * compute the (smallest) label for every successor of deterministic part
         * */
        ArrayList<Integer> detSuccs = new ArrayList<>();
        for(final int stateId : mORuns.getOrdDetStates()) {
            for(final int succId : mOperand.getState(stateId).getSuccessors(letter)) {
                if(! dSuccs.get(succId)) {
                    dSuccs.set(succId);
                    detSuccs.add(succId);
                }
            }
        }
        // now for jSuccs, pick a label not being used
        nSuccs.andNot(dSuccs);
        for(final int ds : jSuccs ) {
            if(dSuccs.get(ds)) continue;
            detSuccs.add(ds);
        }
        // now we compute the successor
        OrderedRuns nextRuns = new OrderedRuns(nSuccs);
        for(final int state : detSuccs) {
            nextRuns.addDetState(state);
        }
        StateDPA succ = mDeterminized.getOrAddState(nextRuns);
        System.out.println(getId() + " "+ mORuns + " -> " + succ.getId() + " "+ nextRuns + ": " + letter);
        super.addSuccessor(letter, succ.getId());
        return succ.getId();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof StateDPA)) {
            return false;
        }
        StateDPA other = (StateDPA)obj;
        return  mORuns.equals(other.mORuns);
    }
    
    @Override
    public String toString() {
        return mORuns.toString();
    }
    

    @Override
    public int hashCode() {
        return mORuns.hashCode();
    }

}
