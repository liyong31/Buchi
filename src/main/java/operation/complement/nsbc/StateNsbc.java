package operation.complement.nsbc;

import automata.IBuchi;
import automata.State;
import main.Options;
import util.ISet;
import util.PairXX;
import util.PairXY;
import util.UtilISet;

/**
 * */

public class StateNsbc extends State {

    protected final ComplementNsbc mComplement;
    protected final NSBC mNsbc;
    
    public StateNsbc(ComplementNsbc complement, int id, NSBC nsbc) {
        super(id);
        this.mComplement = complement;
        this.mNsbc = nsbc;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();
    
    private PairXY<ISet, PairXX<ISet>> computeSuccessors(ISet R, int letter) {
        IBuchi operand = mComplement.getOperand();
        ISet RP = UtilISet.newISet();
        ISet finalSuccs = UtilISet.newISet();
        ISet nonfinalSuccs = UtilISet.newISet();
        for(final int pred : R) {
            for(final int succ : operand.getState(pred).getSuccessors(letter)) {
                if(operand.isFinal(succ)) {
                    finalSuccs.set(succ);
                }else {
                    nonfinalSuccs.set(succ);
                }
                RP.set(succ);
            }
        }
        
        return new PairXY<>(RP, new PairXX<>(finalSuccs, nonfinalSuccs));
        
    }
    
    /**
     * Currently we do not allow new runs enter S set
     * possible optimizations
     *   1. S is empty and B contains sink state, then no need to explore its successors
     *   
     *   **/
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        IBuchi operand = this.mComplement.getOperand();
        StateNsbc nextState;
        ISet nextSuccs = UtilISet.newISet();
        ISet fset = operand.getFinalStates();
        ISet N , S, B, C;
        PairXY<ISet, PairXX<ISet>> result;
        NSBC nextNsbc;
        if(! mNsbc.isColored()) {
            result = computeSuccessors(mNsbc.getNSet(), letter);
            ISet NP = result.getFirst();
            nextNsbc = new NSBC(NP);
            nextState = mComplement.getOrAddState(nextNsbc);
            super.addSuccessor(letter, nextState.getId());
            nextSuccs.set(nextState.getId());     
            // since current state is not colored, we will see it as
            N = mNsbc.copyNSet();
            N.and(mComplement.mNondetStates);
            
            // not F
            S = mComplement.mDetStates.clone();
            S.and(mNsbc.getNSet());
            B = S.clone();
            S.andNot(fset);
            
            // inter F
            B.and(fset);
            
            C = UtilISet.newISet();
        }else {
            N = mNsbc.getNSet();
            S = mNsbc.getSSet();
            B = mNsbc.getBSet();
            C = mNsbc.getCSet();
        }
        
        // Q1 is nondeterministic state set, Q2 is deterministic state set
        // consider successors of N
        result = computeSuccessors(N, letter);
        ISet NP = result.getFirst().clone(); //all successors
        NP.and(mComplement.mNondetStates); // get only nondeterministic part
        
        // d(N, a) /\ Q2
        ISet NPInterQ2 = result.getFirst();
        NPInterQ2.and(mComplement.mDetStates);
        
        //now we compute successors of S
        result = computeSuccessors(S, letter);
        // only keep nonfinal states in SP
        ISet SP = result.getSecond().getSecond();
        ISet SPInterF = result.getSecond().getFirst();
        
        // now consider successors of C
        result = computeSuccessors(C, letter);
        ISet CP = result.getFirst().clone();
        CP.or(NPInterQ2);
        CP.or(SPInterF);
        CP.andNot(SP); // remove successors of S
        
        ISet BP;
        if(B.isEmpty()) { // breakpoint construction
            if(Options.mLazyB) {
                BP = result.getFirst();
                BP.andNot(SP);
                CP.andNot(BP);
            }else {
                BP = CP;
                CP = UtilISet.newISet();
            }
        }else {
            // now consider successors of B
            result = computeSuccessors(B, letter);
            BP = result.getFirst();
            BP.andNot(SP); // remove successors of S 
            CP.andNot(BP); // remove successors of B
        }
        
        nextNsbc = new NSBC(NP, SP, BP, CP);
        nextState = mComplement.getOrAddState(nextNsbc);
        super.addSuccessor(letter, nextState.getId());
        nextSuccs.set(nextState.getId());   
        
        return nextSuccs;
    }
    
    @Override
    public int hashCode() {
        return mNsbc.hashCode();
    }
    
    @Override
    public String toString() {
        return mNsbc.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null) return false;
        if(obj instanceof StateNsbc) {
            StateNsbc other = (StateNsbc)obj;
            return this.mNsbc.equals(other.mNsbc);
        }
        return false;
    }
    
    public boolean isColored() {
        return this.mNsbc.mColored;
    }

}
