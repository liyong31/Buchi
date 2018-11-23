package operation.complement.cutpoint;

import java.util.List;

import automata.IBuchi;
import automata.State;
import operation.complement.tuple.OrderedSets;
import util.ISet;
import util.UtilISet;

public class StateCutpoint extends State {
    protected final ComplementCutpoint mComplement;
    protected final OrderedSetsCutpoint mOSetCutpoint;
    
    public StateCutpoint(ComplementCutpoint complement, int id, OrderedSetsCutpoint osets) {
        super(id);
        this.mComplement = complement;
        this.mOSetCutpoint = osets;
    }
    
    private final ISet mVisitedLetters = UtilISet.newISet();
    
    /**
     * TOP labeled components should last
     * BOT labeled components should be die out
     * **/ 
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        IBuchi operand = mComplement.getOperand();
        OrderedSets ordSets = mOSetCutpoint.getOrderedSets();
        List<ISet> listOfordSets = ordSets.getOrderedSets();
        boolean jumped = mOSetCutpoint.hasJumped();
        ISet leftSuccs = UtilISet.newISet();
        ISet predCutpoint = mOSetCutpoint.getCutpoint();
        ISet predTodo = mOSetCutpoint.getTodoSets();
        ISet cutpointSuccs = UtilISet.newISet(); 
        ISet todoSuccs = UtilISet.newISet();
        // successors
        boolean hasFinalsInPreds = false;
        // two possible successors
        OrderedSetsCutpoint ordSetsCutSuccNotJumped = null, ordSetsCutSuccJumped;
        // jumped successor
        ordSetsCutSuccJumped = new OrderedSetsCutpoint(true);
        if(!jumped) {
            // not jumped one
            ordSetsCutSuccNotJumped = new OrderedSetsCutpoint(false);
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
                if((jumped && predCutpoint.get(i)) || (!jumped && hasFinalInPredLocal)) {
                    cutpointSuccs.set(index);
                }
                if(jumped && predTodo.get(i)) {
                    todoSuccs.set(index);
                }
                // final index for successors
                indexFinalsSuccs.set(index);
                ordSetsCutSuccJumped.addSet(finalSuccs);
                if(!jumped) {
                    ordSetsCutSuccNotJumped.addSet(finalSuccs);
                }
                index ++;
            }
            if(!nonFinalSuccs.isEmpty()) {
                if((jumped && predCutpoint.get(i)) || (!jumped && hasFinalInPredLocal)) {
                      cutpointSuccs.set(index);
                }
                ordSetsCutSuccJumped.addSet(nonFinalSuccs);
                if(!jumped) {
                    ordSetsCutSuccNotJumped.addSet(nonFinalSuccs);
                }
                index ++;
            }
            if(!hasFinalsInPreds && hasFinalInPredLocal) {
                hasFinalsInPreds = true;
            }
        }
        
        // now if it is in the initial component
        StateCutpoint newState;
        if(!jumped) {
            newState = mComplement.getOrAddState(ordSetsCutSuccNotJumped);
            super.addSuccessor(letter, newState.getId());
            // 
            if(hasFinalsInPreds) {
                ordSetsCutSuccJumped.setCutpoint(cutpointSuccs);
            }else {
                ordSetsCutSuccJumped.setCutpoint(indexFinalsSuccs);
            }
            newState = mComplement.getOrAddState(ordSetsCutSuccJumped);
            super.addSuccessor(letter, newState.getId());
        }else {
            // jumped, breakpoint construction
            if(!predCutpoint.isEmpty()) {
                ordSetsCutSuccJumped.setCutpoint(cutpointSuccs);
            }else {
                ordSetsCutSuccJumped.setCutpoint(indexFinalsSuccs);
            }
            newState = mComplement.getOrAddState(ordSetsCutSuccJumped);
            super.addSuccessor(letter, newState.getId());
        }
        return super.getSuccessors(letter);
    }
    
    @Override
    public String toString() {
        return mOSetCutpoint.toString();
    }
    
    @Override
    public int hashCode() {
        return mOSetCutpoint.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(this.getClass().isInstance(obj)) {
            StateCutpoint other = (StateCutpoint)obj;
            return mOSetCutpoint.equals(other.mOSetCutpoint);
        }
        return false;
    }

}
