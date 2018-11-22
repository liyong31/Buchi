package operation.complement.breakpoint;

import java.util.List;

import operation.complement.tuple.Color;
import operation.complement.tuple.OrderedSets;
import util.ISet;
import util.UtilISet;

public class OrderedSetsBreakpoint {
    
    protected final OrderedSets mOrdSets; // left most are successors of final states
    protected ISet mBotSets;        //
    private final boolean mJumped;        // 
    
    public OrderedSetsBreakpoint(boolean jumped) {
        this.mJumped = jumped;
        if(this.mJumped) {
            this.mBotSets = UtilISet.newISet();
        }else {
            this.mBotSets = null; 
        }
        this.mOrdSets = new OrderedSets(false);
    }
    
    public void addSet(ISet oset) {
        mOrdSets.addSet(oset, Color.NONE);
    }
    
    public void setBreakpoint(ISet set) {
        mBotSets = set.clone();
    }
    
    protected boolean hasJumped() {
        return this.mJumped;
    }
    
    public boolean isFinal() {
        return this.mJumped && mBotSets.isEmpty();
    }
    
    public OrderedSets getOrderedSets() {
        return mOrdSets;
    }
    
    public ISet getBreakpoint() {
        return mBotSets;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(" + mOrdSets.toString());
        if(this.mJumped) {
            builder.append(", " + mBotSets.toString());
        }
        builder.append(")");
        return builder.toString();
    }
    
    @Override
    public int hashCode() {
        if(this.mJumped) {
            // "jumpted"
            final int prime = 31;
            int result = prime  + mOrdSets.hashCode();
            result = result * prime + mBotSets.hashCode();
            return result;
        }else {
            return mOrdSets.hashCode();
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(this.getClass().isInstance(obj)) {
            OrderedSetsBreakpoint other = (OrderedSetsBreakpoint)obj;
            if(this.hasJumped() != other.hasJumped()) {
                return false;
            }
            boolean eq = mOrdSets.equals(other.mOrdSets);
            if(this.hasJumped()) {
                return eq && mBotSets.equals(other.mBotSets);
            }else {
                return eq;
            }
        }
        return false;
    }
    

}
