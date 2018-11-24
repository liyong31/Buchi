package operation.complement.order;

import java.util.ArrayList;

import automata.IBuchi;
import automata.State;
import main.Options;
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
     * BOT labeled components should die out
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
        OrderedRuns runSuccsNotJumped = null, runSuccsJumped = new OrderedRuns(true);
        ArrayList<Integer> ordStates = mOrderedRuns.getOrdDetStates();
        ISet fSuccs = UtilISet.newISet();
        ISet leftSuccs = UtilISet.newISet();
        ISet bkSuccs = UtilISet.newISet();
        ISet todoSuccs = UtilISet.newISet();
        
        if(!jumped) {
            runSuccsNotJumped = new OrderedRuns(false);
        }

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
                runSuccsJumped.addOrdState(t);
                if(!jumped) {
                    runSuccsNotJumped.addOrdState(t);
                }
            }
            // nonfinal
            for(int t : succs) {
                if(temp.get(t)) continue;
                runSuccsJumped.addOrdState(t);
                if(!jumped) {
                    runSuccsNotJumped.addOrdState(t);
                }
            }
            if(inBkpoint) {
                bkSuccs.or(succs);
            }
            if(inTodos) {
                todoSuccs.or(succs);
            }
            leftSuccs.or(succs);
        }
        if(!jumped) {
            newState = mComplement.getOrAddState(runSuccsNotJumped);
            super.addSuccessor(letter, newState.getId());
            //set breakpoint
            runSuccsJumped.setBreakpoint(fSuccs);
            
        }else {
            // decide to set breakpoint and other things
            if(mOrderedRuns.getBreakpoint().isEmpty()) {
                if(Options.mLazyB) {
                   fSuccs.andNot(todoSuccs);
                   runSuccsJumped.setTodos(fSuccs);
                }else {
                   todoSuccs.or(fSuccs);
                }
                runSuccsJumped.setBreakpoint(todoSuccs);
            }else {
                runSuccsJumped.setBreakpoint(bkSuccs);
                fSuccs.andNot(bkSuccs);
                // add new comers
                todoSuccs.or(fSuccs);
                runSuccsJumped.setTodos(todoSuccs);
            }
        }
        newState = mComplement.getOrAddState(runSuccsJumped);
        super.addSuccessor(letter, newState.getId());
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
