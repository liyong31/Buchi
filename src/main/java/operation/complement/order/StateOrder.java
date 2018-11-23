package operation.complement.order;

import java.util.ArrayList;
import java.util.List;

import automata.IBuchi;
import automata.State;
import main.Options;
import operation.complement.tuple.OrderedSets;
import util.ISet;
import util.UtilISet;

public class StateOrder extends State {
    protected final ComplementOrder mComplement;
    protected final OrderedRuns mOrderedRuns;
    
    public StateOrder(ComplementOrder complement, int id, OrderedRuns osets) {
        super(id);
        this.mComplement = complement;
        this.mOrderedRuns = osets;
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
        boolean jumped = this.mOrderedRuns.hasJumped();
        StateOrder newState;
        OrderedRuns runSuccs;
        if(!jumped) {
            ISet succs = UtilISet.newISet();
            
            for(int s : this.mOrderedRuns.getNondetStates()) {
                succs.or(operand.getState(s).getSuccessors(letter));
            }
            // now we create states
            runSuccs = new OrderedRuns(succs);
            newState = mComplement.getOrAddState(runSuccs);
            super.addSuccessor(letter, newState.getId());
            if(!this.mOrderedRuns.getNondetStates().isEmpty()) {
                // ordered states for nonempty set
                ISet temp = succs.clone();
                temp.andNot(operand.getFinalStates());
                runSuccs = new OrderedRuns();
                // only those states
                for(int s : temp) {
                    runSuccs.addOrdState(s);
                }
                temp = succs.clone();
                temp.and(operand.getFinalStates());
                runSuccs.setBreakpoint(temp);
                newState = mComplement.getOrAddState(runSuccs);
                super.addSuccessor(letter, newState.getId());
            }
        }else {
            // first ordered states
            ArrayList<Integer> ordStates = mOrderedRuns.getOrdDetStates();
            ISet fSuccs = UtilISet.newISet();
            ISet leftSuccs = UtilISet.newISet();
            runSuccs = new OrderedRuns();
            for(int i = 0; i < ordStates.size(); i ++) {
                int s = ordStates.get(i);
                for(int t : operand.getState(s).getSuccessors(letter)) {
                    if(operand.isFinal(t)) {
                        fSuccs.set(t);
                    }else if(!leftSuccs.get(t)) {
                        leftSuccs.set(t);
                        runSuccs.addOrdState(t);
                    }
                }
            }
            // now breakpoint states
            ISet bkSuccs = UtilISet.newISet();
            for(int s : mOrderedRuns.getBreakpoint()) {
                for(int t : operand.getState(s).getSuccessors(letter)) {
                    // not occured before
                    if(!leftSuccs.get(t)) {
                        leftSuccs.set(t);
                        bkSuccs.set(t);
                    }
                }
            }
            
            // now todos
            ISet todoSuccs = UtilISet.newISet();
            for(int s : mOrderedRuns.getTodos()) {
                for(int t : operand.getState(s).getSuccessors(letter)) {
                    // not occured before
                    if(!leftSuccs.get(t)) {
                        leftSuccs.set(t);
                        todoSuccs.set(t);
                    }
                }
            }
            // decide to set breakpoint and other things
            if(mOrderedRuns.getBreakpoint().isEmpty()) {
                if(Options.mLazyB) {
                   fSuccs.andNot(todoSuccs);
                   runSuccs.setTodos(fSuccs);
                }else {
                   todoSuccs.or(fSuccs);
                }
                runSuccs.setBreakpoint(todoSuccs);
            }else {
                runSuccs.setBreakpoint(bkSuccs);
                fSuccs.andNot(bkSuccs);
                // add new comers
                todoSuccs.or(fSuccs);
                runSuccs.setTodos(todoSuccs);
            }
            newState = mComplement.getOrAddState(runSuccs);
            super.addSuccessor(letter, newState.getId());
        }
        return super.getSuccessors(letter);
    }
    
    @Override
    public String toString() {
        return mOrderedRuns.toString();
    }
    
    @Override
    public int hashCode() {
        return mOrderedRuns.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(this.getClass().isInstance(obj)) {
            StateOrder other = (StateOrder)obj;
            return mOrderedRuns.equals(other.mOrderedRuns);
        }
        return false;
    }

}
