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

package main;

import automata.IBuchi;
import operation.complement.Complement;
import operation.complement.nsbc.ComplementNsbc;
import operation.complement.nsbc.QuotientNsbc;
import operation.explore.Explore;
import operation.quotient.QuotientSimple;
import operation.removal.Remove;
import util.UtilISet;

public class TaskComplement extends GenericUnaryTask {
	
	private Complement mComplement;
	private IBuchi mResult;
	
	public TaskComplement(String file) {
		mFileName = file;
	}

	@Override
	public void runTask() {
		mResultValue = ResultValue.EXE_UNKNOWN;
		mComplement.explore();
		mResultValue = ResultValue.OK;
		// get sizes
		mOpStateNum = mComplement.getOperand().getStateSize();
		mOpTransNum = mComplement.getOperand().getTransitionSize();
		mAlphabetSize = mComplement.getOperand().getAlphabetSize();
		mResult = mComplement.getResult();
//		if(Options.mDirectSimulation && (mComplement instanceof ComplementNsbc)) {
//		    ComplementNsbc result = (ComplementNsbc)mResult;
//            QuotientNsbc quotient = new QuotientNsbc(result);
//            new Explore(quotient);
//            mResult = quotient;
//        }else 
        if(Options.mDirectSimulation) {
            QuotientSimple quotient = new QuotientSimple(mResult);
            new Explore(quotient);
            mResult = quotient;
        }
		mResultStateSize = mResult.getStateSize();
		mResultTransSize = mResult.getTransitionSize();
		if(Options.mRemoveDead) {
		    IBuchi buchi = (new Remove(mComplement)).getResult();
	        mRmResultStateSize = buchi.getStateSize();
	        mRmResultTransSize = buchi.getTransitionSize();    
		}
	}
	
	public void setOperation(Complement complement) {
		mComplement = complement;
		this.mOperationName = complement.getName();
		this.mOperationName += "+" + UtilISet.getSetType() + (Options.mLazyS ? "+lazyS" : "")
				                                         + (Options.mLazyB ? "+lazyB" : "")
				                                         + (Options.mGBA ? "+GBA" : "+BA")
				                                         + (Options.mOE? "+antichain" : "");
	}
	
	public IBuchi getResult() {
	    return mResult;
	}

}
