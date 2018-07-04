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
