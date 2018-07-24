/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi.
 * 
 * Buchi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Buchi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Buchi. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
