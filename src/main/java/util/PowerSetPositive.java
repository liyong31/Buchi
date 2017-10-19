package util;

import java.util.Iterator;

class PowerSetPositive implements Iterator<ISet> {

	private EnumeratorBitSet mEnumerator;
	
	private final ISet mSet;
	private final int[] mIntArr;
	
	public PowerSetPositive(ISet set) {
		assert ! set.isEmpty();
		this.mSet = set;
		mIntArr = new int[mSet.cardinality()];
		int index = 0;
		for(int elem : mSet) {
			mIntArr[index ++] = elem;
		}
		this.mEnumerator = new EnumeratorBitSet(mSet.cardinality());
	}

	@Override
	public boolean hasNext() {
		int index = mEnumerator.nextSetBit(0); // whether we have got out of the array
		return index < mEnumerator.size();
	}

	@Override
	public ISet next() {
		assert hasNext();
		EnumeratorBitSet val = mEnumerator.clone();
		mEnumerator.nextBitSet();
		ISet bits = UtilISet.newISet();
		for(int n = val.nextSetBit(0); n >= 0 ; n = val.nextSetBit(n + 1)) {
			bits.set(mIntArr[n]);
		}
		return bits;
	}

}
