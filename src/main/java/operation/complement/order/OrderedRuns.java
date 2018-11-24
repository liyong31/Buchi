package operation.complement.order;

import java.util.ArrayList;

import util.ISet;
import util.UtilISet;

public class OrderedRuns {

    protected final ArrayList<Integer> mOrds; // ordered states    
    protected final ISet mTodos; //
    protected final ISet mBreakpoint; //
    protected final boolean mJumped;
    
    public OrderedRuns(boolean jumped) {
        this.mJumped = jumped;
        this.mTodos = UtilISet.newISet();
        this.mBreakpoint = UtilISet.newISet();
        this.mOrds = new ArrayList<>();
    }
    
    public boolean hasJumped() {
        return mJumped;
    }
    
    public void addOrdState(int state) {
        assert this.mOrds != null;
        this.mOrds.add(state);
    }
    
    public void setTodos(ISet set) {
        assert this.mTodos != null;
        this.mTodos.clear();
        this.mTodos.or(set);
    }
    
    public void setBreakpoint(ISet set) {
        assert this.mBreakpoint != null;
        this.mBreakpoint.clear();
        this.mBreakpoint.or(set);
    }
    
    public ISet getBreakpoint() {
        return mBreakpoint;
    }
    
    public ISet getTodos() {
        return mTodos;
    }
    
    public ArrayList<Integer> getOrdDetStates() {
        return mOrds;
    }
    
    protected boolean isFinal() {
        if(!mJumped && mOrds.isEmpty()) {
            return true;
        }else if(mJumped && mBreakpoint.isEmpty()){
            return true;
        }
        return false;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof OrderedRuns)) {
            return false;
        }
        OrderedRuns otherRuns = (OrderedRuns) other;
        return this.mOrds.equals(otherRuns.mOrds) && this.mTodos.equals(otherRuns.mTodos)
                && this.mBreakpoint.equals(otherRuns.mBreakpoint);
    }
    
    public static int hashValue(ISet set) {
        final int prime = 31;
        int result = 1;
        for(final int n : set) {
            result = prime * result + n;
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for(final int state : mOrds) {
            result = prime * result + state;
        }
        result = prime * result + mTodos.hashCode();
        result = prime * result + mBreakpoint.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        if(!mJumped) {
            return this.mOrds + "";
        }else {
            return "<" + this.mOrds + ", " + this.mTodos + ", " + this.mBreakpoint + ">";
        }
    }
}
