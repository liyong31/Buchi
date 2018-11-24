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
                temp.and(operand.getFinalStates());
                runSuccs = new OrderedRuns();
                runSuccs.setBreakpoint(temp);
                // first those states
                for(int s : temp) {
                    runSuccs.addOrdState(s);
                }
                // nonfinal
                for(int s : succs) {
                    if(temp.get(s)) continue;
                    runSuccs.addOrdState(s);
                }
                newState = mComplement.getOrAddState(runSuccs);
                super.addSuccessor(letter, newState.getId());
            }
        }else {
            // first ordered states
            ArrayList<Integer> ordStates = mOrderedRuns.getOrdDetStates();
            ISet fSuccs = UtilISet.newISet();
            ISet leftSuccs = UtilISet.newISet();
            ISet bkSuccs = UtilISet.newISet();
            ISet todoSuccs = UtilISet.newISet();
            runSuccs = new OrderedRuns();
            for(int i = 0; i < ordStates.size(); i ++) {
                int s = ordStates.get(i);
                boolean inBkpoint = mOrderedRuns.getBreakpoint().get(s);
                boolean inTodos = mOrderedRuns.getTodos().get(s);
                ISet succs = operand.getState(s).getSuccessors(letter).clone();
                // remove all states appeared
                succs.andNot(leftSuccs);
                ISet temp = succs.clone();
                temp.and(operand.getFinalStates());
                fSuccs.or(temp);
                // first deal with final states
                for(int t : temp) {
                    runSuccs.addOrdState(t);
                }
                // nonfinal
                for(int t : succs) {
                    if(temp.get(t)) continue;
                    runSuccs.addOrdState(t);
                }
                if(inBkpoint) {
                    bkSuccs.or(succs);
                }
                if(inTodos) {
                    todoSuccs.or(succs);
                }
                leftSuccs.or(succs);
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
