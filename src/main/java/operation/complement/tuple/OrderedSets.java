package operation.complement.tuple;

import java.util.ArrayList;

import util.ISet;

public class OrderedSets {
    
    protected final ArrayList<ISet> mOSets; // left most are successors of final states
    protected final ArrayList<Color> mColors;
    private final boolean mIsColored;                  // whether in accepting component
    
    public OrderedSets(boolean colored) {
        this.mOSets = new ArrayList<>();
        this.mColors = new ArrayList<>();
        this.mIsColored = colored;
    }
    
    public void addSet(ISet oset, Color color) {
        mOSets.add(oset);
        if(this.mIsColored) {
            mColors.add(color);
        }
    }
    
    public ArrayList<ISet> getOrderedSets() {
        return this.mOSets;
    }
    
    public ArrayList<Color> getColors() {
        return this.mColors;
    }
    
    public boolean isColored() {
        return mIsColored;
    }
    
    public boolean hasTwoColor() {
        for(Color color : mColors) {
            if(color == Color.TWO) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isFinal() {
        if(this.mIsColored) {
            for(final Color color : mColors) {
                if(color == Color.TWO || color == Color.THREE) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public Color getColor(int index) {
        if(mColors.isEmpty()) return Color.ZERO;
        return this.mColors.get(index);
    }
    
    public ISet getSet(int index) {
        assert index < mOSets.size();
        return this.mOSets.get(index);
    }
    
    @Override
    public boolean equals(Object other) {
        if(this == other) return true;
        if(! (other instanceof OrderedSets)) {
            return false;
        }
        OrderedSets otherRuns = (OrderedSets)other;
        if(this.mIsColored != otherRuns.mIsColored) {
            return false;
        }
        return  this.mOSets.equals(otherRuns.mOSets) &&
                this.mColors.equals(otherRuns.mColors);
    }
    
    private int hashValue(ISet set) {
        final int prime = 31;
        int result = 1;
        for(final int n : set) {
            result = prime * result + n;
        }
        return result;
    }
    
    private boolean mHasCode = false;
    private int mHashCode;
    @Override
    public int hashCode() {
        if(mHasCode) return mHashCode;
        mHasCode = true;
        final int prime = 31;
        int result = 1;
        for(int i = 0; i < mOSets.size(); i ++) {
            result = prime * result + hashValue(mOSets.get(i));
            if(this.mIsColored) {
                result = prime * result + mColors.get(i).hashCode();
            }
        }
        mHashCode = result;
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<");
        if(this.mIsColored) {
            for(int i = 0; i < mOSets.size(); i ++) {
                builder.append("(" + mOSets.get(i) + "," + mColors.get(i) + "),");
            }
        }else {
            for(int i = 0; i < mOSets.size(); i ++) {
                builder.append(mOSets.get(i) + ",");
            }
        }
        
        builder.append(">");
        return builder.toString(); 
    }

}
