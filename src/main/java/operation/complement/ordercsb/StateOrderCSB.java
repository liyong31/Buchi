package operation.complement.ordercsb;

import java.util.ArrayList;

import automata.IBuchi;
import automata.State;
import main.Options;
import operation.complement.ncsb.NCSB;
import operation.complement.ncsb.StateNcsb;
import operation.complement.order.OrderedRuns;
import util.ISet;
import util.PowerSet;
import util.UtilISet;

public class StateOrderCSB extends State {
    protected final ComplementOrderCSB mComplement;
    protected final OrderedCSB mOrderedRuns;
    
    public StateOrderCSB(ComplementOrderCSB complement, int id, OrderedCSB osets) {
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
        StateOrderCSB newState;
        OrderedCSB runSuccs = new OrderedCSB();
        ArrayList<Integer> ordStates = mOrderedRuns.getOrdDetStates();
        ISet fSuccs = UtilISet.newISet();
        ISet leftSuccs = UtilISet.newISet();
        ISet bSuccs = UtilISet.newISet();
        ISet cSuccs = UtilISet.newISet();
        ISet sSuccs = UtilISet.newISet();
        ISet finals = operand.getFinalStates();
        for(int i = 0; i < ordStates.size(); i ++) {
            int s = ordStates.get(i);
            boolean inB = mOrderedRuns.getB().get(s);
            boolean inC = mOrderedRuns.getC().get(s);
            boolean inS = mOrderedRuns.getS().get(s);
            ISet succs = operand.getState(s).getSuccessors(letter).clone();
            // remove all states appeared
            succs.andNot(leftSuccs);
            ISet temp = succs.clone();
            temp.and(finals);
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
            if(inB) {
                bSuccs.or(succs);
            }
            if(inC) {
                cSuccs.or(succs);
            }
            if(inS) {
                sSuccs.or(succs);
            }
            leftSuccs.or(succs);
        }
        
//        // if bSuccs have final states
//        // wrong guess
//        if(sSuccs.overlap(finals)) {
//            return UtilISet.newISet();
//        }
        // should continue rather than being cut
        // wrong guess
//        if(cSuccs.isEmpty()) {
//            return UtilISet.newISet();
//        }
        // now randomly select nonfinal states of cfSuccs
        ISet mayInB = cSuccs.clone();
        mayInB.and(finals);
        // all nonfinal successors
        PowerSet ps = new PowerSet(mayInB);
        while(ps.hasNext()) {
            // now we create initial states
            ISet toB = ps.next();
            ISet CP = cSuccs.clone();
            ISet SP = sSuccs.clone();
            SP.or(toB);
            CP.andNot(toB);
            ISet BP = bSuccs.clone();
            if(mOrderedRuns.getB().isEmpty()) {
                if(Options.mLazyB) {
                   BP.or(CP);
                   BP.andNot(fSuccs);
                }else {
                   BP = CP;
                }
            }else {
                BP.and(CP);
            }
            OrderedCSB newRuns = runSuccs.clone();
            newRuns.setB(BP);
            newRuns.setC(CP);
            newRuns.setS(SP);
            newState = mComplement.getOrAddState(newRuns);
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
            StateOrderCSB other = (StateOrderCSB)obj;
            return mOrderedRuns.equals(other.mOrderedRuns);
        }
        return false;
    }

}
