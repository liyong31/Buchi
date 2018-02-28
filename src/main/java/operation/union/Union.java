/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi which is a simple version of SemiBuchi.
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

package operation.union;

import automata.Buchi;
import automata.IBuchi;
import automata.IState;
import operation.IBinaryOp;

public class Union extends Buchi implements IBinaryOp<IBuchi, IBuchi> {

    private final IBuchi mFstOperand;
    private final IBuchi mSndOperand;
    
    public Union(IBuchi fstOperand, IBuchi sndOperand) {
        super(fstOperand.getAlphabetSize());
        if(fstOperand.getAlphabetSize() != sndOperand.getAlphabetSize()) {
            throw new UnsupportedOperationException("Union: different alphabets");
        }
        mFstOperand = fstOperand;
        mSndOperand = sndOperand;
        computeStateSpace();
    }

    private void computeStateSpace() {
        copyStateSpace(mFstOperand, 0);
        copyStateSpace(mSndOperand, mFstOperand.getStateSize());
    }
    
    private void copyStateSpace(IBuchi buchi, int offset) {
        for(int i = 0; i < buchi.getStateSize(); i ++) {
            IState state = addState();
            for(final int letter : buchi.getState(i).getEnabledLetters()) {
                for(final int succ : buchi.getState(i).getSuccessors(letter)) {
                    state.addSuccessor(letter, succ + offset);
                }
            }
            if(buchi.isInitial(i)) {
                setInitial(state.getId());
            }
            if(buchi.isFinal(i)) {
                setFinal(state.getId());
            }
        }
    }

    @Override
    public String getName() {
        return "Union";
    }

    @Override
    public IBuchi getFirstOperand() {
        return mFstOperand;
    }

    @Override
    public IBuchi getSecondOperand() {
        return mSndOperand;
    }

    @Override
    public IBuchi getResult() {
        return this;
    }
    

}
