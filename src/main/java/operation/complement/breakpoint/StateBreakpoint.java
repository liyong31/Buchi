package operation.complement.breakpoint;

import java.util.List;

import automata.IBuchi;
import automata.State;
import operation.complement.tuple.OrderedSets;
import util.ISet;
import util.UtilISet;

public class StateBreakpoint extends State {
    protected final ComplementBreakpoint mComplement;
    protected final OrderedSetsBreakpoint mOSetBkpoint;
    
    public StateBreakpoint(ComplementBreakpoint complement, int id, OrderedSetsBreakpoint osets) {
        super(id);
        this.mComplement = complement;
        this.mOSetBkpoint = osets;
    }
    
    private final ISet mVisitedLetters = UtilISet.newISet();
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        ISet succs = UtilISet.newISet();
        IBuchi operand = mComplement.getOperand();
        OrderedSets ordSets = mOSetBkpoint.getOrderedSets();
        List<ISet> listOfordSets = ordSets.getOrderedSets();
        boolean jumped = mOSetBkpoint.hasJumped();
        ISet leftSuccs = UtilISet.newISet();
        ISet indexPreds = mOSetBkpoint.getBreakpoint();
        ISet indexSuccs = UtilISet.newISet(); 
        // successors
        boolean hasFinalsInPreds = false;
        // two possible successors
        OrderedSetsBreakpoint ordSetsBkSuccNotJumped = null, ordSetsBkSuccJumped;
        // jumped successor
        ordSetsBkSuccJumped = new OrderedSetsBreakpoint(true);
        if(!jumped) {
            // not jumped one
            ordSetsBkSuccNotJumped = new OrderedSetsBreakpoint(false);
        }
        int index = 0;
        ISet indexFinalsSuccs = UtilISet.newISet();
        for(int i = 0; i < listOfordSets.size(); i ++) {
            // take current set from the list
            ISet Si = listOfordSets.get(i);
            ISet finalSuccs = UtilISet.newISet();
            ISet nonFinalSuccs = UtilISet.newISet();
            // get the successors
            boolean hasFinalInPredLocal = false;
            for(final int p : Si) {
                // check whether there is a final state
                if(!hasFinalInPredLocal && operand.isFinal(p)) {
                    hasFinalInPredLocal = true;
                }
                for(final int q : operand.getState(p).getSuccessors(letter)) {
                    // ignore successors already have been visited
                    if(leftSuccs.get(q)) continue;
                    if(operand.isFinal(q)) {
                        finalSuccs.set(q);
                    }else {
                        nonFinalSuccs.set(q);
                    }
                    leftSuccs.set(q);
                }
            }
            
            // have final states
            if(!finalSuccs.isEmpty()) {
                // either it is in indicePreds (in accepting component)
                // or in initial component (finals are current sets) 
                if((jumped && indexPreds.get(i)) || (!jumped && hasFinalInPredLocal)) {
                    indexSuccs.set(index);
                }
                // final index for successors
                indexFinalsSuccs.set(index);
                ordSetsBkSuccJumped.addSet(finalSuccs);
                if(!jumped) {
                    ordSetsBkSuccNotJumped.addSet(finalSuccs);
                }
                index ++;
            }
            if(!nonFinalSuccs.isEmpty()) {
                if((jumped && indexPreds.get(i)) || (!jumped && hasFinalInPredLocal)) {
                      indexSuccs.set(index);
                }
                ordSetsBkSuccJumped.addSet(nonFinalSuccs);
                if(!jumped) {
                    ordSetsBkSuccNotJumped.addSet(nonFinalSuccs);
                }
                index ++;
            }
            if(!hasFinalsInPreds && hasFinalInPredLocal) {
                hasFinalsInPreds = true;
            }
        }
        
        // now if it is in the initial component
        StateBreakpoint newState;
        if(!jumped) {
            newState = mComplement.getOrAddState(ordSetsBkSuccNotJumped);
            super.addSuccessor(letter, newState.getId());
            // 
            if(hasFinalsInPreds) {
                ordSetsBkSuccJumped.setBreakpoint(indexSuccs);
            }else {
                ordSetsBkSuccJumped.setBreakpoint(indexFinalsSuccs);
            }
            newState = mComplement.getOrAddState(ordSetsBkSuccJumped);
            super.addSuccessor(letter, newState.getId());
        }else {
            // jumped, breakpoint construction
            if(!indexPreds.isEmpty()) {
                ordSetsBkSuccJumped.setBreakpoint(indexSuccs);
            }else {
                ordSetsBkSuccJumped.setBreakpoint(indexFinalsSuccs);
            }
            newState = mComplement.getOrAddState(ordSetsBkSuccJumped);
            super.addSuccessor(letter, newState.getId());
        }
        return super.getSuccessors(letter);
    }
    
    @Override
    public String toString() {
        return mOSetBkpoint.toString();
    }
    
    @Override
    public int hashCode() {
        return mOSetBkpoint.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(this.getClass().isInstance(obj)) {
            StateBreakpoint other = (StateBreakpoint)obj;
            return mOSetBkpoint.equals(other.mOSetBkpoint);
        }
        return false;
    }

}
