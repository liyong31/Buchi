package operation.complement.ncsb;

import automata.IBuchi;
import automata.IState;
import util.ISet;
import util.PairXX;
import util.UtilISet;

public class UtilNcsb {
    
    private UtilNcsb() {
        
    }
    
    public static PairXX<ISet> partitionStates(IBuchi operand) {
        ISet detStates = operand.getDetStatesAfterFinals();
        ISet nondetStates = UtilISet.newISet();
        for(int s = 0; s < operand.getStateSize(); s ++) {
            if(! detStates.get(s)) {
                nondetStates.set(s);
            }
        }
        return new PairXX<>(detStates, nondetStates);
    }
    
    /**
     * If q in C\F or (B\F), then tr(q, a) should not be not empty
     * */
    public static boolean noTransitionAssertionMinusF(IBuchi buchi, int state, ISet succs) {
        return !buchi.isFinal(state) && succs.isEmpty();
    }
    
    /**
     * compute the successor of a set of states, in particular, d(C\F) should not contain be empty
     * **/
    public static SuccessorResult collectSuccessors(IBuchi buchi, ISet states, int letter, boolean testTrans) {
        SuccessorResult result = new SuccessorResult();
        for(final int stateId : states) {
            IState state = buchi.getState(stateId);
            ISet succs = state.getSuccessors(letter);
            if (testTrans && noTransitionAssertionMinusF(buchi, stateId, succs)) {
                result.hasSuccessor = false;
                return result;
            }
            result.mSuccs.or(succs);
            if(testTrans) {
                if(buchi.isFinal(stateId)) {
                    result.mInterFSuccs.or(succs);
                }else {
                    result.mMinusFSuccs.or(succs);
                }
            }
        }
        return result;
    }
    
    public static boolean hasFinalStates(IBuchi buchi, ISet states) {
        return states.overlap(buchi.getFinalStates());
    }
}
