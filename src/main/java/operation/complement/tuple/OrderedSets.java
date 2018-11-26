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
    
    public int addSet(ISet oset, Color color) {
        int index = mOSets.size();
        if(! this.mIsColored) {
            mOSets.add(oset);
        }else {
            this.mOSets.add(oset);
            this.mColors.add(color);
        }
        return index;
    }
    
    /**
     * 
     * (..., (Si, 1), (Sj, 1), ...) -> (..., (Si U Sj, 1), ...)
     * (..., (Si, 2), (Sj, 2), ...) -> (..., (Si U Sj, 2), ...)
     * 
     * merge adjacent sets which have same color
     * **/
    protected OrderedSets mergeAdjacentSets() {
        // we have same mergeable colored sets
        OrderedSets merged = new OrderedSets(true);
        Color predColor = Color.NONE;
        for(int index = 0; index < mOSets.size(); index ++) {
            Color currColor = getColor(index);
            ISet set = mOSets.get(index);
            if(currColor == Color.NONE) {
                merged.addSet(set, Color.NONE);
                continue;
            }else {
                if(currColor != predColor) {
                    merged.addSet(set, currColor);
                    predColor = currColor;
                }else {
                    int otherIndex = merged.mOSets.size() - 1;
                    // must clone this set, other wise may affect other states
                    ISet mergedSet = merged.mOSets.get(otherIndex).clone();
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
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.mIsColored ? 1 : 0);
        for(int i = 0; i < mOSets.size(); i ++) {
            result = prime * result + hashValue(mOSets.get(i));
            result = prime * result + (this.mIsColored ? mColors.get(i).hashCode() : 0);
        }
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
     * 
     * (..., (S[j], 2), (S[j+1], 1), ... ,(S[j+n], 1), (S[j+n+1], not 1)...) -> (..., (S[j] U .. U S[j+n], 2), ...)
     * 
     * merge 1-colored sets directly following a 2-colored set
     * **/
    protected OrderedSets mergeAdjacentColoredSets() {
        OrderedSets merged = new OrderedSets(true);
        Color predColor = Color.NONE;
        for (int index = 0; index < mOSets.size(); index++) {
            Color currColor = getColor(index);
            ISet set = mOSets.get(index);
            if(predColor == Color.TWO && currColor == Color.ONE){
                // merge 2-colored sets with the following 1-colored sets
                int otherIndex = merged.mOSets.size() - 1;
                // must clone this set, other wise may affect other states
                ISet mergedSet = merged.getSet(otherIndex).clone();
                mergedSet.or(set);
                merged.mOSets.set(otherIndex, mergedSet);
            }else {
                merged.addSet(set, currColor);
                predColor = currColor;
            }
        }
        return merged;
    }
   
}
