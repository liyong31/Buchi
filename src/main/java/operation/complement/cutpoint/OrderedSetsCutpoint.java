package operation.complement.cutpoint;

import operation.complement.tuple.Color;
import operation.complement.tuple.OrderedSets;
import util.ISet;
import util.UtilISet;

public class OrderedSetsCutpoint {
    
    protected final OrderedSets mOrdSets;   // left most are successors of final states
    protected ISet mCutSets;                // runs in cut set should die out eventually 
    protected ISet mTodoSets;               // list of indices of sets need to be cut
    private final boolean mJumped;          // has jumped to the second stage 
    
    public OrderedSetsCutpoint(boolean jumped) {
        this.mJumped = jumped;
        if(this.mJumped) {
            this.mCutSets = UtilISet.newISet();
            this.mTodoSets = UtilISet.newISet();
        }else {
            this.mCutSets = null; 
            this.mTodoSets = null;
        }
        this.mOrdSets = new OrderedSets(false);
    }
    
    public void addSet(ISet oset) {
        mOrdSets.addSet(oset, Color.NONE);
    }
    
    public void setCutpoint(ISet set) {
        mCutSets = set.clone();
    }
    
    public void setTodoSet(ISet set) {
        mTodoSets = set.clone();
    }
    
    protected boolean hasJumped() {
        return this.mJumped;
    }
    
    public boolean isFinal() {
        return this.mJumped && mCutSets.isEmpty();
    }
    
    public OrderedSets getOrderedSets() {
        return mOrdSets;
    }
    
    public ISet getCutpoint() {
        return mCutSets;
    }
    
    public ISet getTodoSets() {
        return mTodoSets;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(" + mOrdSets.toString());
        if(this.mJumped) {
            builder.append(", " + mTodoSets.toString());
            builder.append(", " + mCutSets.toString());
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
            result = result * prime + mTodoSets.hashCode();
            result = result * prime + mCutSets.hashCode();
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
            OrderedSetsCutpoint other = (OrderedSetsCutpoint)obj;
            if(this.hasJumped() != other.hasJumped()) {
                return false;
            }
            boolean eq = mOrdSets.equals(other.mOrdSets);
            if(this.hasJumped()) {
                return eq && mTodoSets.equals(other.mTodoSets) && mCutSets.equals(other.mCutSets);
            }else {
                return eq;
            }
        }
        return false;
    }
    

}
