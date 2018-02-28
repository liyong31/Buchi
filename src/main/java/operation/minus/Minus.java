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

package operation.minus;

import automata.IBuchi;
import operation.complement.ncsb.ComplementSDBA;
import operation.intersect.Intersect;

public class Minus implements IMminus{

    private final IBuchi mFstOperand;
    private final IBuchi mSndOperand;
    private final IBuchi mSndComplement;
    private final IBuchi mResult;
    
    public Minus(IBuchi fstOperand, IBuchi sndOperand) {
        if(fstOperand.getAlphabetSize() != sndOperand.getAlphabetSize()) {
            throw new UnsupportedOperationException("Minus: different alphabets");
        }
        mFstOperand = fstOperand;
        mSndOperand = sndOperand;
        mSndComplement = new ComplementSDBA(sndOperand);
        mResult = new Intersect(mFstOperand, mSndComplement);
    }

    @Override
    public String getName() {
        return "Minus";
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
        return mResult;
    }

    @Override
    public IBuchi getSecondComplement() {
        return mSndComplement;
    }

}
