package operation.determinize;

import util.ISet;
import util.UtilISet;

/**
 * NDB tuple 
 * TODO: in order to make it unmodifiable
 * */
public class ND {
	
	private ISet mNSet;
	private ISet mDSet;
	
	public ND(ISet N, ISet D) {
		this.mNSet = N;
		this.mDSet = D;
	}
	
	public ND() {
		this.mNSet = UtilISet.newISet();
		this.mDSet = UtilISet.newISet();
	}
	
	// be aware that we use the same object
	//CLONE object to make modification
	public ISet getNSet() {
		return  mNSet;
	}
	
	public ISet getDSet() {
		return  mDSet;
	}
	
	// Safe operations for (N, C, S, B)
	public ISet copyNSet() {
		return  mNSet.clone();
	}
	
	public ISet copyDSet() {
		return  mDSet.clone();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof ND)) {
			return false;
		}
		ND ncsb = (ND)obj;
		return  contentEqual(ncsb);
	}
	
	private boolean contentEqual(ND ncsb) {
		if(! mNSet.equals(ncsb.mNSet)
		|| ! mDSet.equals(ncsb.mDSet)) {
			return false;
		}
		return true;
	}
	
	@Override
	public ND clone() {
		return new ND(mNSet.clone(), mDSet.clone());
	}
	
	@Override
	public String toString() {
		return "(" + mNSet.toString() + "," 
		           + mDSet.toString() + ")";
	}
	
    private int hashCode;
    private boolean hasCode = false;
	
	@Override
	public int hashCode() {
		if(hasCode) return hashCode;
		else {
			hasCode = true;
			hashCode = 1;
			final int prime = 31;
			hashCode= prime * hashCode + hashValue(mNSet);
			hashCode= prime * hashCode + hashValue(mDSet);
			return hashCode;
		}
	}
	
	public static int hashValue(ISet set) {
		final int prime = 31;
        int result = 1;
        for(final int n : set) {
        	result = prime * result + n;
        }
        return result;
	}
	

}
