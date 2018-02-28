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


package operation.isempty;

import automata.IBuchi;
import automata.LassoRun;
import operation.UnaryOp;
import operation.explore.AsccExplore;

public class IsEmpty extends UnaryOp<IBuchi, Boolean>{
    private final AsccExplore mExplore;
    private LassoRun mAcceptedRun;
    
    public IsEmpty(IBuchi operand) {
        super(operand);
        mExplore = new AsccExplore(mOperand, true);
        mResult = mExplore.getAcceptedScc() == null;
    }

    @Override
    public String getName() {
        return "IsEmpty";
    }
    
    public LassoRun getAcceptedLassoRun() {
        if(!mResult && mAcceptedRun == null) {
            LassoRunExtractor lre = new LassoRunExtractor(mOperand, mExplore.getAcceptedScc());
            mAcceptedRun = lre.getResult();
        }
        return mAcceptedRun;
    }

}
