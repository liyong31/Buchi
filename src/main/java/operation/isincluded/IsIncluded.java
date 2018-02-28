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

package operation.isincluded;

import automata.IBuchi;
import operation.complement.ncsb.ComplementSDBA;
import operation.complement.ncsb.StateNCSB;

public class IsIncluded implements IIsIncluded {
    
    protected final IBuchi mFstOperand;
    protected final IBuchi mSndOperand;
    protected final ComplementSDBA mSndComplement;
    protected Boolean mResult;
    
    public IsIncluded(IBuchi fstOperand, IBuchi sndOperand) {
        if(fstOperand.getAlphabetSize() != sndOperand.getAlphabetSize()) {
            throw new UnsupportedOperationException("Minus: different alphabets");
        }
        mFstOperand = fstOperand;
        mSndOperand = sndOperand;
        mSndComplement = new ComplementSDBA(sndOperand);
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
    public Boolean getResult() {
        return mResult;
    }

    @Override
    public ComplementSDBA getSecondComplement() {
        return mSndComplement;
    }

    @Override
    public StateNCSB getComplementState(int state) {
        assert state >= 0 && state < mSndComplement.getStateSize();
        return (StateNCSB) mSndComplement.getState(state);
    }

}
