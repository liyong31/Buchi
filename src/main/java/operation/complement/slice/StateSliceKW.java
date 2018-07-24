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

package operation.complement.slice;

import java.util.ArrayList;

import automata.IBuchi;
import automata.State;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import operation.complement.tuple.Color;
import operation.complement.tuple.OrderedSets;
import operation.complement.tuple.StateTuple;
import util.ISet;
import util.PowerSet;
import util.UtilISet;


//TODO NOT implemented yet
public class StateSliceKW extends State {

    private final ComplementSliceKW mComplement;
    private final Slice mOSets;
    
    public StateSliceKW(ComplementSliceKW complement, int id, Slice osets) {
        super(id);
        this.mComplement = complement;
        this.mOSets = osets;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();

    /**
     * For normal transitions in the complement, see the paper
     *   "Complementation, Disambiguation, and Determinization of Buchi Automata Unified" 
     *     by Detlef Kaehler and Thomas Wilke In ICALP 2008
     * They defined the run trees and divide the states at the same level of the trees as slice
     * TODO
     * **/
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        ISet succs = UtilISet.newISet();
        
        return succs;
    }
    
    @Override
    public int hashCode() {
        return mOSets.hashCode();
    }
    
    @Override
    public String toString() {
        return mOSets.toString() + ((mOSets.isFinal())? "*": "0");
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null) return false;
        if(obj instanceof StateSliceKW) {
            StateSliceKW other = (StateSliceKW)obj;
            return this.mOSets.equals(other.mOSets);
        }
        return false;
    }
}
