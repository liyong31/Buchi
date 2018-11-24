package operation.complement.ordercsb;

import java.util.ArrayList;

import util.ISet;
import util.UtilISet;

public class OrderedCSB {

    protected final ArrayList<Integer> mOrds; // ordered runs/states    
    protected final ISet mC;                  // runs waiting to be moved to breakpoint
    protected final ISet mS;                  // runs which should be safe
    protected final ISet mB;                  // runs which should die out
    
    public OrderedCSB() {
        this.mC = UtilISet.newISet();
        this.mB = UtilISet.newISet();
        this.mS = UtilISet.newISet();
        this.mOrds = new ArrayList<>();
    }
    
    public void addOrdState(int state) {
        assert this.mOrds != null;
        this.mOrds.add(state);
    }
    
    private OrderedCSB(ArrayList<Integer> ordRuns) {
        this.mC = UtilISet.newISet();
        this.mB = UtilISet.newISet();
        this.mS = UtilISet.newISet();
        this.mOrds = ordRuns;
    }
    
    public void setC(ISet set) {
        assert this.mC != null;
        this.mC.clear();
        this.mC.or(set);
    }
    
    public void setB(ISet set) {
        assert this.mB != null;
        this.mB.clear();
        this.mB.or(set);
    }
    
    public void setS(ISet set) {
        assert this.mS != null;
        this.mS.clear();
        this.mS.or(set);
    }
    
    public ISet getB() {
        return mB;
    }
    
    public ISet getC() {
        return mC;
    }
    
    public ISet getS() {
        return mS;
    }
    
    @Override
    public OrderedCSB clone() {
        OrderedCSB other = new OrderedCSB(mOrds);
        other.setB(mB);
        other.setB(mC);
        other.setB(mS);
        return other;
    }
    
    public ArrayList<Integer> getOrdDetStates() {
        return mOrds;
    }
    
    protected boolean isFinal() {
        if(mC.isEmpty() && mB.isEmpty()){
            return true;
        }
        return false;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof OrderedCSB)) {
            return false;
        }
        OrderedCSB otherRuns = (OrderedCSB) other;
        return this.mOrds.equals(otherRuns.mOrds) && this.mC.equals(otherRuns.mC)
                && this.mS.equals(otherRuns.mS)
                && this.mB.equals(otherRuns.mB);
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
        result = prime * result + mC.hashCode();
        result = prime * result + mS.hashCode();
        result = prime * result + mB.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "<" + this.mOrds + ", " + this.mC + ", " + this.mS + ", " + this.mB + ">";
    }
}
