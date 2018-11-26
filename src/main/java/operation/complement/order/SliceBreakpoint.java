package operation.complement.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.Options;
import operation.complement.tuple.Color;
import operation.complement.tuple.OrderedSets;
import util.ISet;
import util.UtilISet;

public class SliceBreakpoint {

    protected final ArrayList<ISet> mOSets; // left most are successors of final states
    protected final ISet mTodos;            // Color ONE
    protected final ISet mBreakpoint;       // Color TWO
    private final boolean mJumped;          // whether in accepting component
    
    public SliceBreakpoint(boolean jumped) {
        this.mOSets = new ArrayList<>();
        this.mTodos = UtilISet.newISet();
        this.mBreakpoint = UtilISet.newISet();
        this.mJumped = jumped;
    }
    
    public int addSet(ISet oset) {
        int index = mOSets.size();
        mOSets.add(oset);
        return index;
    }
    
    public void setTodo(int index) {
        mTodos.set(index);
    }
    
    public void setBreakpoint(int index) {
        mBreakpoint.set(index);
    }
    
    protected SliceBreakpoint mergeAdjacentSetsWithSameColor() {
        assert this.mJumped : "Not jumped states";
        SliceBreakpoint merged = new SliceBreakpoint(true);
        Color predColor = Color.NONE;
        for(int index = 0; index < mOSets.size(); index ++) {
            Color currColor = getColor(index);
            ISet set = mOSets.get(index);
            if(currColor == Color.NONE) {
                merged.addSet(set);
                continue;
            }else {
                if(currColor != predColor) {
                    int otherIndex = merged.addSet(set);
                    if(mTodos.get(index)) {
                        merged.setTodo(otherIndex);
                    }else if(mBreakpoint.get(index)) {
                        merged.setBreakpoint(otherIndex);
                    }
                    predColor = currColor;
                }else {
                    int otherIndex = merged.mOSets.size() - 1;
                    ISet mergedSet = merged.mOSets.get(otherIndex);
                    mergedSet.or(set);
                    merged.mOSets.set(otherIndex, mergedSet);
                }
            }
        }
        return merged;
    }
    
    public List<ISet> getOrderedSets() {
        return Collections.unmodifiableList(this.mOSets);
    }
    
    public boolean isColored() {
        return mJumped;
    }
    
    public boolean hasTwoColor() {
        return !mBreakpoint.isEmpty();
    }
    
    public boolean isFinal() {
        if(this.mJumped) {
            return mBreakpoint.isEmpty();
        }
        return false;
    }
    
    public Color getColor(int index) {
        if(!mJumped) return Color.ZERO;
        if(mTodos.get(index)) {
            return Color.ONE;
        }else if(mBreakpoint.get(index)) {
            return Color.TWO;
        }else {
            return Color.ZERO;
        }
    }
    
    public ISet getSet(int index) {
        assert index < mOSets.size();
        return this.mOSets.get(index);
    }
    
    @Override
    public boolean equals(Object other) {
        if(this == other) return true;
        if(other == null) return false;
        if(! (other instanceof SliceBreakpoint)) {
            return false;
        }
        SliceBreakpoint otherRuns = (SliceBreakpoint)other;
        if(this.mJumped != otherRuns.mJumped) {
            return false;
        }
        return  this.mOSets.equals(otherRuns.mOSets) &&
                this.mTodos.equals(otherRuns.mTodos) &&
                this.mBreakpoint.equals(otherRuns.mBreakpoint);
    }
   
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (mJumped ? 1 : 0);
        for(int i = 0; i < mOSets.size(); i ++) {
            result = prime * result + mOSets.get(i).hashCode();
        }
        result = prime * result + mTodos.hashCode();
        result = prime * result + mBreakpoint.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<" + mOSets + ", " + mTodos + ", " + mBreakpoint + ">");
        return builder.toString(); 
    }
    
    protected boolean isMergeableColor(Color color) {
        return color == Color.ONE || color == Color.TWO;
    }
    
    /**
     * merge 2-colored sets preceded by 1-colored
     * **/
    protected SliceBreakpoint mergeAdjacentColoredSets() {
//        SliceBreakpoint merged = new SliceBreakpoint(true);
//        if(this.mJumped) {
//            ArrayList<Color> newColors = new ArrayList<>();
//            ArrayList<ISet> newOSets = new ArrayList<>();
//            int index = 0;
//            Color lastColor = Color.NONE;
//            while(index < this.mOSets.size()) {
//                Color color = this.mColors.get(index);
//                // 1-colored set followed by 2-colored set
//                if(lastColor == Color.ONE && color == Color.TWO) {
//                    ISet jointSet = newOSets.get(index);
//                    jointSet.or(this.mOSets.get(index)); // same object
//                    newOSets.set(index, jointSet);
//                    newColors.set(index, color);
//                    lastColor = color;
//                 }else {
//                    newOSets.add(this.mOSets.get(index));
//                    newColors.add(color);
//                    lastColor = color;
//                }
//                index ++;
//            }
//            this.mOSets.clear();
//            this.mColors.clear();
//            this.mColors.addAll(newColors);
//            this.mOSets.addAll(newOSets);
        
//        }
        return null;
    }
    
}
