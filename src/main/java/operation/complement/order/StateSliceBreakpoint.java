package operation.complement.order;

import java.util.List;

import automata.IBuchi;
import automata.State;

import operation.complement.tuple.Color;

import operation.complement.tuple.StateTuple;
import util.ISet;
import util.UtilISet;

public class StateSliceBreakpoint extends State {
    
    private final ComplementSliceBreakpoint mComplement;
    private final SliceBreakpoint mOSets;
    
    public StateSliceBreakpoint(ComplementSliceBreakpoint complement, int id, SliceBreakpoint osets) {
        super(id);
        this.mComplement = complement;
        this.mOSets = osets;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();
    
    private Color doBreakpoint(Color predColor) {
        return predColor;
    }
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        ISet succs = UtilISet.newISet();
        IBuchi operand = mComplement.getOperand();
        List<ISet> ordSets = mOSets.getOrderedSets();
        ISet todoSuccs = UtilISet.newISet();
        ISet breakSuccs = UtilISet.newISet();
        ISet newTodos = UtilISet.newISet();
        for(int i = 0; i < ordSets.size(); i ++) {
            // compute successors
            Color color = mOSets.getColor(i);
            
        }
        StateTuple nextState;
        //1. non-colored states compute successor
//        if(!mOSets.isColored()) {
//            OrderedSets osets = new OrderedSets(false);
//            for(int i = 0; i < nextOrdSets.size(); i ++) {
//                osets.addSet(nextOrdSets.get(i), Color.NONE);
//            }
//            nextState = mComplement.getOrAddState(osets);
//            super.addSuccessor(letter, nextState.getId());
//            succs.set(nextState.getId());
//            if(Options.mDebug) System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + osets + " : " + letter);
//        }
//        //2. every state compute colors
//        {
//            OrderedSets osets = new OrderedSets(true);
//            ISet fset = operand.getFinalStates();
//            for(int i = 0; i < nextOrdSets.size(); i ++) {
//                osets.addSet(nextOrdSets.get(i)
//                        , decideColor(nextOrdSets.get(i), predMap.get(i), fset));
//            }
//            // merge 1-colored followed by a 2-colored
//            if(Options.mMergeAdjacentColoredSets) {
//                osets.mergeAdjacentColoredSets();
//            }
//            nextState = mComplement.getOrAddState(osets);
//            super.addSuccessor(letter, nextState.getId());
//            succs.set(nextState.getId());
//            if(Options.mDebug) System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + osets + " : " + letter);
//        }
//        
        return succs;
    }
    
    @Override
    public int hashCode() {
        return mOSets.hashCode();
    }
    
    @Override
    public String toString() {
        return mOSets.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null) return false;
        if(obj instanceof StateSliceBreakpoint) {
            StateSliceBreakpoint other = (StateSliceBreakpoint)obj;
            return this.mOSets.equals(other.mOSets);
        }
        return false;
    }


}
