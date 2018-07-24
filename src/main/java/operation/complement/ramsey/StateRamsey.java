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

package operation.complement.ramsey;

import automata.State;
import util.ISet;
import util.UtilISet;

/**
 * <state, label>
 * state is a state in the deterministic automaton
 * label is like the indication of different accepting components
 * */
public class StateRamsey extends State {

    private final StateDABg mState;
    private final int mLabel;
    private final ComplementRamsey mComplement;
    private final DABg mDA;
    
    public StateRamsey(ComplementRamsey complement, int id, StateDABg state, int label) {
        super(id);
        this.mComplement = complement;
        this.mState = state;
        this.mLabel = label;
        this.mDA = complement.getDA();
    }
    
    private final ISet mVisitedLetters = UtilISet.newISet();
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        ISet succs = UtilISet.newISet();
        StateDABg nextDAState = mDA.getStateDAProfile(mState.getSuccessor(letter));
        StateRamsey nextState;
        if(mLabel == 0) {
            // add one state 
            nextState = mComplement.getOrAddState(nextDAState, 0);
            succs.set(nextState.getId());
            super.addSuccessor(letter, nextState.getId());
            // try epsilon transitions
            for(int j = 0; j < mDA.getStateSize(); j ++) {
                if(mDA.isInitial(j)) continue;
                boolean valid = mDA.isDisjointWith(nextDAState.getId(), j) 
                        && mDA.isProper(nextDAState.getId(), j);
                if(valid) {
                    nextState = mComplement.getOrAddState(mDA.getStateDAProfile(0), j);
                    succs.set(nextState.getId());
                    super.addSuccessor(letter, nextState.getId());
                }
            }
            
            // X_i (X_j)^\omega empty
            // p-> q, and q -> acc for accepting epsilon transition
            // so we just add p -> acc to remove epsilon transitions
        }else {
            // label is not 0 and it is in accepting component
            // (p, i) -> (q, )
            int j = nextDAState.getId();
            // add transition to the successor copy
            nextState = mComplement.getOrAddState(nextDAState, mLabel);
            succs.set(nextState.getId());
            super.addSuccessor(letter, nextState.getId());
            if(mLabel == j) {
                // current successor is the accepting state, and there is an
                // epsilon transition to (init, j) state
                // add transition to (init, j) to remove epsilon transition
                nextState = mComplement.getOrAddState(mDA.getStateDAProfile(0), j);
                succs.set(nextState.getId());
                super.addSuccessor(letter, nextState.getId());
            }
        }
        return succs;
    }
    
    @Override
    public int hashCode() {
        return 31 * mState.hashCode() + mLabel;
    }
    
    @Override
    public String toString() {
        return mLabel + ":" + mState;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(obj instanceof StateRamsey) {
            StateRamsey other = (StateRamsey)obj;
            return  mState.equals(other.mState)
                 && mLabel == other.mLabel;
        }
        return false;
    }

}
