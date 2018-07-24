/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi.
 * 
 * Buchi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Buchi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Buchi. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package operation.complement.tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.Options;
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
        if(! this.mIsColored) {
            mOSets.add(oset);
        }else {
            addOrMergeAdjacentSets(oset, color);
        }
    }
    
    protected void addOrMergeAdjacentSets(ISet oset, Color color) {
        // have to merge states
        int index = mColors.size() - 1;
        Color lastColor = getColor(index);
        boolean canMerge = false;
        // we have same mergeable colored sets
        if(isMergeableColor(color) && lastColor == color) {
            canMerge = true;
        }
        
        canMerge = canMerge && Options.mMergeAdjacentSets;
        if(canMerge) {
            assert index == this.mOSets.size() - 1;
            ISet mergeSet = this.mOSets.get(index);
            mergeSet.or(oset);
            this.mOSets.set(index, mergeSet);
            this.mColors.set(index, color);
        }else {
            this.mOSets.add(oset);
            this.mColors.add(color);
        }
    }
    
    public List<ISet> getOrderedSets() {
        return Collections.unmodifiableList(this.mOSets);
    }
    
    public List<Color> getColors() {
        return (ArrayList<Color>) Collections.unmodifiableList(this.mColors);
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
        for (int i = 0; i < mOSets.size(); i++) {
            if (this.mIsColored) {
                builder.append("(" + mOSets.get(i) + "," + mColors.get(i) + ")");
            } else {
                builder.append(mOSets.get(i) + "");
            }
            if(i != mOSets.size() - 1) {
                builder.append(",");
            }
        }
        builder.append(">");
        return builder.toString(); 
    }
    
    protected boolean isMergeableColor(Color color) {
        return color == Color.ONE || color == Color.TWO;
    }
    
    /**
     * merge 2-colored sets preceded by 1-colored
     * **/
    public void mergeAdjacentColoredSets() {
        if(this.mIsColored) {
            ArrayList<Color> newColors = new ArrayList<>();
            ArrayList<ISet> newOSets = new ArrayList<>();
            int index = 0;
            Color lastColor = Color.NONE;
            while(index < this.mOSets.size()) {
                Color color = this.mColors.get(index);
                // 1-colored set followed by 2-colored set
                if(lastColor == Color.ONE && color == Color.TWO) {
                    ISet jointSet = newOSets.get(index);
                    jointSet.or(this.mOSets.get(index)); // same object
                    newOSets.set(index, jointSet);
                    newColors.set(index, color);
                    lastColor = color;
                 }else {
                    newOSets.add(this.mOSets.get(index));
                    newColors.add(color);
                    lastColor = color;
                }
                index ++;
            }
            this.mOSets.clear();
            this.mColors.clear();
            this.mColors.addAll(newColors);
            this.mOSets.addAll(newOSets);
        }
    }
   
}
