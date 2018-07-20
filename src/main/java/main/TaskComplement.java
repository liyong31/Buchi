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
		if(Options.mDirectSimulation && (mComplement instanceof ComplementNsbc)) {
		    ComplementNsbc result = (ComplementNsbc)mResult;
            QuotientNsbc quotient = new QuotientNsbc(result);
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
