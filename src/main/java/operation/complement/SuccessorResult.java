package operation.complement;

import util.ISet;
import util.UtilISet;

public class SuccessorResult {
	
	public ISet mSuccs ;
	public ISet mMinusFSuccs ;
	public ISet mInterFSuccs ;
	public boolean hasSuccessor ;
	
	public SuccessorResult() {
		mSuccs = UtilISet.newISet();
		mMinusFSuccs = UtilISet.newISet();
		mInterFSuccs = UtilISet.newISet();
		hasSuccessor = true;
	}
	
	@Override
	public String toString() {
		return "[" + mSuccs.toString() + ":" + mMinusFSuccs.toString() + ":"
				   + mInterFSuccs.toString() + ":" + hasSuccessor + "]";
	}

}
