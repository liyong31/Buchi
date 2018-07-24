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

package operation.complement.dba;

import automata.IBuchi;
import automata.State;
import util.ISet;
import util.UtilISet;

public class StateDBA extends State {
    
    private int mState;
    private boolean mLabel; //
    private final ComplementDBA mComplement;
    private final IBuchi mOperand;
    
    public StateDBA(ComplementDBA complement, int id, int state, boolean label) {
        super(id);
        this.mComplement = complement;
        this.mOperand = complement.getOperand();
        this.mState = state;
        this.mLabel = label;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        // computing successors
        ISet succs = mOperand.getState(mState).getSuccessors(letter);
        if(succs.cardinality() > 1) {
            throw new UnsupportedOperationException("Not a DBA: state " + mState 
                    + " has more than one successors " + succs.toString());
        }
        if(succs.cardinality() == 0) {
            return succs;
        }
        int succ = succs.iterator().next();
        StateDBA qP;
        succs = UtilISet.newISet();
        if(mLabel) {
            // c = 1
            if(mOperand.isFinal(succ)) {
                //empty
            }else {
                // q' \notin F
                qP = mComplement.getOrAddState(succ, true);
                super.addSuccessor(letter, qP.getId());
                succs.set(qP.getId());
            }
        }else {
            // c = 0
            if(mOperand.isFinal(succ)) {
                // q' \in F
                qP = mComplement.getOrAddState(succ, false);
                super.addSuccessor(letter, qP.getId());
                succs.set(qP.getId());
            }else {
                // q' \notin F
                qP = mComplement.getOrAddState(succ, false);
                super.addSuccessor(letter, qP.getId());
                succs.set(qP.getId());
                qP = mComplement.getOrAddState(succ, true);
                super.addSuccessor(letter, qP.getId());
                succs.set(qP.getId());
            }
        }
        return succs;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof StateDBA)) {
            return false;
        }
        StateDBA other = (StateDBA)obj;
        return  mState == other.mState
              && mLabel == other.mLabel;
    }
    
    @Override
    public int hashCode() {
        return mState * 31 + (mLabel ? 1 : 0);
    }
    

}
