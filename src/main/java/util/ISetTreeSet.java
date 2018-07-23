package util;

import java.util.Iterator;
import java.util.TreeSet;


public class ISetTreeSet implements ISet {
	
	private final TreeSet<Integer> mSet;
	
	public ISetTreeSet() {
		mSet = new TreeSet<>();
	}

	@Override
	public ISet clone() {
		ISetTreeSet copy = new ISetTreeSet();
		copy.mSet.addAll(mSet);
		return copy;
	}

	@Override
	public void andNot(ISet set) {
		if(! (set instanceof ISetTreeSet)) {
		    throw new UnsupportedOperationException("OPERAND should be TreeSet: " + set.getClass());
		}
		ISetTreeSet temp = (ISetTreeSet)set;
		this.mSet.removeAll(temp.mSet);
	}

	@Override
	public void and(ISet set) {
		if(! (set instanceof ISetTreeSet)) {
		    throw new UnsupportedOperationException("OPERAND should be TreeSet: " + set.getClass());
		}
		ISetTreeSet temp = (ISetTreeSet)set;
		this.mSet.retainAll(temp.mSet);
	}

	@Override
	public void or(ISet set) {
		if(! (set instanceof ISetTreeSet)) {
		    throw new UnsupportedOperationException("OPERAND should be TreeSet: " + set.getClass());
		}
		ISetTreeSet temp = (ISetTreeSet)set;
		this.mSet.addAll(temp.mSet);
	}

	@Override
	public boolean get(int value) {
		return mSet.contains(value);
	}

	@Override
	public void clear(int value) {
		mSet.remove(value);
	}
	
	@Override
	public String toString() {
		return mSet.toString();
	}
	
	@Override
	public void clear() {
		mSet.clear();
	}
	
	@Override
	public void set(int value) {
		mSet.add(value);
	}
	
    @Override
    public void set(int from, int to) {
        for(int i = from; i <= to; i ++) {
            mSet.add(i);
        }
    }

	@Override
	public boolean isEmpty() {
		return mSet.isEmpty();
	}

	@Override
	public int cardinality() {
		return mSet.size();
	}

	@Override
	public boolean subsetOf(ISet set) {
		if(! (set instanceof ISetTreeSet)) {
		    throw new UnsupportedOperationException("OPERAND should be TreeSet: " + set.getClass());
		}
		ISetTreeSet temp = (ISetTreeSet)set;
		return temp.mSet.containsAll(this.mSet);
	}

	@Override
	public boolean contentEq(ISet set) {
		if(! (set instanceof ISetTreeSet)) {
		    throw new UnsupportedOperationException("OPERAND should be TreeSet: " + set.getClass());
		}
		ISetTreeSet temp = (ISetTreeSet)set;
		return this.mSet.equals(temp.mSet);
	}

	@Override
	public Object get() {
		return mSet;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof ISetTreeSet)) {
		    throw new UnsupportedOperationException("OPERAND should be TreeSet: " + obj.getClass());
		}
		ISetTreeSet temp = (ISetTreeSet)obj;
		return this.contentEq(temp);
	}

    @Override
    public Iterator<Integer> iterator() {
        return mSet.iterator();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for(final int n : mSet) {
            result = prime * result + n;
        }
        return result;
    }
}
