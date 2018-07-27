/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi.
 * 
 * Buchi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Buchi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Buchi. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package operation.complement.nsbc;

import java.util.HashSet;
import java.util.Set;

import automata.IBuchi;
import automata.State;
import main.Options;
import util.ISet;
import util.PairXX;
import util.PairXY;
import util.PowerSet;
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
        StateNsbc nextState;
        PairXY<ISet, PairXX<ISet>> result;
        Set<NSBC> nextNsbcs = null;
        if(! mNsbc.isColored()) {
            result = computeSuccessors(mNsbc.getNSet(), letter);
            ISet NP = result.getFirst();
            NSBC nextNsbc = new NSBC(NP);
            nextState = mComplement.getOrAddState(nextNsbc);
            super.addSuccessor(letter, nextState.getId());
            // since current state is not colored, we will see it as
            nextNsbcs = getUncoloredSuccessors(letter);
        }else {
            nextNsbcs = new HashSet<>();
            nextNsbcs.add(getColoredSuccessor(mNsbc, letter));
        }
        
        for(final NSBC nextNsbc : nextNsbcs) {
            nextState = mComplement.getOrAddState(nextNsbc);
            super.addSuccessor(letter, nextState.getId());
        }
        
        return super.getSuccessors(letter);
    }
    
    private Set<NSBC> getUncoloredSuccessors(int letter) {
        Set<NSBC> result = new HashSet<>();
        ISet N, S, B, C;
        ISet fset = mComplement.getOperand().getFinalStates();
        // N
        N = mNsbc.copyNSet();
        N.and(mComplement.mNondetStates);
        
        S = mComplement.mDetStates.clone();
        S.and(mNsbc.getNSet());
        B = S.clone();
        // not F
        S.andNot(fset);
        
        C = UtilISet.newISet();
        if(!Options.mEagerGuess) {
            // inter F
            B.and(fset);
            NSBC succ = getColoredSuccessor(new NSBC(N, S, B, C), letter);
            result.add(succ);
        }else {
            // we make nondeterministic jumps
            N = mNsbc.copyNSet();
            N.and(mComplement.mNondetStates);
            
            ISet mayInS = S;
            ISet mayInB = B;
            PowerSet ps = new PowerSet(mayInS);
            while(ps.hasNext()) {
                S = ps.next();
                B = mayInB.clone();
                B.andNot(S);
                NSBC succ = getColoredSuccessor(new NSBC(N, S, B, C), letter);
                if(succ != null) {
                    result.add(succ);
                }
            }
        }
        return result;
    }
    
    private NSBC getColoredSuccessor(NSBC nsbc, int letter) {
        // Q1 is nondeterministic state set, Q2 is deterministic state set
        // consider successors of N
        ISet N = nsbc.getNSet();
        ISet S = nsbc.getSSet();
        ISet B = nsbc.getBSet();
        ISet C = nsbc.getCSet();
        PairXY<ISet, PairXX<ISet>> result;
        
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
        if(Options.mEagerGuess && (!SPInterF.isEmpty() || 
           SP.cardinality() != S.cardinality())) {
            // successor of S must not contain final states
            // if we have nondeterministic jump from first part 
            // and the number of runs will not decrease
            return null;
        }
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
        
        NSBC nextNsbc = new NSBC(NP, SP, BP, CP);
        return nextNsbc;
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

    protected boolean isFairlyEqual(StateNsbc other) {
        if(mNsbc.isColored() != other.mNsbc.isColored()) {
            return false;
        }
        
        if(!mNsbc.getNSet().equals(other.mNsbc.getNSet())) {
            return false;
        }
        if(mNsbc.isColored()) {
            if(!mNsbc.getSSet().equals(other.mNsbc.getSSet())) {
                return false;
            }
            ISet union1 = mNsbc.copyBSet();
            union1.or(mNsbc.getCSet());
            ISet union2 = other.mNsbc.copyBSet();
            union2.or(other.mNsbc.getCSet());
            return union1.equals(union2);
        }else {
            return true;
        }
        
    }
}
