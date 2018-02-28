package main;

import operation.complement.Complement;
import util.UtilISet;

public class TaskComplement extends GenericUnaryTask {
	
	private Complement mComplement;
	
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
		mResultStateSize = mComplement.getStateSize();
		mResultTransSize = mComplement.getTransitionSize();
	}
	
	public void setOperation(Complement complement) {
		mComplement = complement;
		this.mOperationName = "Complement";
		this.mOperationName += "+" + UtilISet.getSetType() + (Options.mLazyS ? "+lazyS" : "")
				                                         + (Options.mLazyB ? "+lazyB" : "")
				                                         + (Options.mGBA ? "+GBA" : "+BA")
				                                         + (Options.mOE? "+antichain" : "");
	}

}
