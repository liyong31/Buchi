package util;

import java.util.Iterator;

class PowerSetEmpty implements Iterator<ISet> {
	
	private boolean mHasNext;
	
	public PowerSetEmpty() {
		mHasNext = true;
	}

	@Override
	public boolean hasNext() {
		return mHasNext;
	}

	@Override
	public ISet next() {
		assert hasNext();
		mHasNext = false;
		return UtilISet.newISet();
	}

}
