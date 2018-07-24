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

package operation.determinize.ldba;

import java.util.ArrayList;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;

import util.ISet;
import util.UtilISet;

/**
 * TODO: in order to make it unmodifiable
 * */
class OrderedRuns {
	
    protected final ISet mNondets; // subset construction
    protected final ISet mDets; //
    protected final ArrayList<Integer> mOrds;
    protected final TIntIntMap mIndices;  // state to index
    protected int mPriority;    
    
    public OrderedRuns(ISet nondets) {
        this.mNondets = nondets;
        this.mDets = UtilISet.newISet();
        this.mOrds = new ArrayList<>();
        this.mIndices = new TIntIntHashMap();
    }
    
    public ISet getNondetStates() {
        return mNondets;
    }
    
    public ArrayList<Integer> getOrdDetStates() {
        return mOrds;
    }
    
    public int getIndex(int state) {
        assert mDets.get(state);
        if(mIndices.containsKey(state)) {
            return mIndices.get(state);
        }
        return -1;
    }
    
    public int getPriority() {
        return mPriority;
    }
    
    public void setPriority(int prio) {
        this.mPriority = prio;
    }
    
    public void addDetState(int state) {
        int index = mOrds.size();
        mOrds.add(state);
        mIndices.put(state, index);
    }
    
    @Override
    public boolean equals(Object other) {
        if(this == other) return true;
        if(! (other instanceof OrderedRuns)) {
            return false;
        }
        OrderedRuns otherRuns = (OrderedRuns)other;
        return  this.mNondets.equals(otherRuns.mNondets) && 
                this.mOrds.equals(otherRuns.mOrds) &&
                this.mPriority == otherRuns.mPriority;
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
        result = prime * result + hashValue(mNondets);
        for(final int state : mOrds) {
            result = prime * result + state;
        }
        result = prime * result + mPriority;
        return result;
    }
    
    @Override
    public String toString() {
        return "<" + mNondets + ", " + this.mOrds + ", " + this.mPriority +  ">"; 
    }

}
