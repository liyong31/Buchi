package operation.complement.order;

import java.util.List;

import automata.IBuchi;
import automata.State;
import main.Options;

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
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        
        IBuchi operand = mComplement.getOperand();
        List<ISet> ordSets = mOSets.getOrderedSets();
        ISet todoSuccs = UtilISet.newISet();
        ISet newTodos = UtilISet.newISet();
        ISet breakSuccs = UtilISet.newISet();
        ISet leftSuccs = UtilISet.newISet();
        SliceBreakpoint sliceBkpJumped = new SliceBreakpoint(true), sliceBkpNotJumped = null;
        if(!mOSets.isColored()) {
            sliceBkpNotJumped = new SliceBreakpoint(false);
        }
        for(int i = 0; i < ordSets.size(); i ++) {
            // compute successors
            ISet Si = ordSets.get(i);
            ISet finalSuccs = UtilISet.newISet();
            ISet nonFinalSuccs = UtilISet.newISet();
            for(final int p : Si) {
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
            // now we will record every thing
            boolean inTodo = mOSets.mTodos.get(i);
            boolean inBreakpoint = mOSets.mBreakpoint.get(i);
            
            if(!finalSuccs.isEmpty()) {
                int index = sliceBkpJumped.addSet(finalSuccs);
                if(sliceBkpNotJumped != null) {
                    sliceBkpNotJumped.addSet(finalSuccs);
                }
                if(inTodo) {
                    todoSuccs.set(index);
                }else if(inBreakpoint) {
                    breakSuccs.set(index);
                }else {
                    newTodos.set(index);
                }
            }
            
            if(!nonFinalSuccs.isEmpty()) {
                int index = sliceBkpJumped.addSet(nonFinalSuccs);
                if(sliceBkpNotJumped != null) {
                    sliceBkpNotJumped.addSet(nonFinalSuccs);
                }
                if(inTodo) {
                    todoSuccs.set(index);
                }else if(inBreakpoint) {
                    breakSuccs.set(index);
                }
            }
        }
        StateSliceBreakpoint nextState;
        //1. non-colored states compute successor
        if(!mOSets.isColored()) {
            nextState = mComplement.getOrAddState(sliceBkpNotJumped);
            super.addSuccessor(letter, nextState.getId());
        }
        // no need to compute colored successors for empty set
        if(!mOSets.isColored() && mOSets.mOSets.isEmpty()) {
            return super.getSuccessors(letter);
        }
        //2. every state compute colors
        
        if(mOSets.mBreakpoint.isEmpty()) {
            if(Options.mLazyB) {
                breakSuccs = todoSuccs;
                todoSuccs = newTodos;
            }else {
                todoSuccs.or(newTodos);
                breakSuccs = todoSuccs;
                todoSuccs = UtilISet.newISet();
            }
        }else {
            todoSuccs.or(newTodos);
        }
        sliceBkpJumped.setBreakpoint(breakSuccs);
        sliceBkpJumped.setTodo(todoSuccs);
        if(Options.mMergeAdjacentSets) {
            sliceBkpJumped = sliceBkpJumped.mergeAdjacentSets();
        }
        if(Options.mMergeAdjacentColoredSets) {
            sliceBkpJumped = sliceBkpJumped.mergeAdjacentColoredSets();
        }
        nextState = mComplement.getOrAddState(sliceBkpJumped);
        super.addSuccessor(letter, nextState.getId());
        return super.getSuccessors(letter);
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
