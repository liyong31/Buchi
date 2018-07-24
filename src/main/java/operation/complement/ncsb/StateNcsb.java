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

package operation.complement.ncsb;

import automata.IBuchi;
import automata.State;
import util.ISet;
import util.PowerSet;
import util.UtilISet;

public class StateNcsb extends State {

    protected final NCSB mNCSB;
    protected final IBuchi mOperand;
    protected final ComplementNcsb mComplement;
    
    public StateNcsb(ComplementNcsb complement, int id, NCSB ncsb) {
        super(id);
        this.mComplement = complement;
        this.mOperand = complement.getOperand();
        this.mNCSB = ncsb;
    }
    
    public NCSB getNCSB() {
        return  mNCSB;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        
        // S
        SuccessorResult succResult = UtilNcsb.collectSuccessors(mOperand, mNCSB.getSSet(), letter, false);
        ISet mustInS = succResult.mSuccs;             // d(S, a)
        if(UtilNcsb.hasFinalStates(mOperand, mustInS)) return UtilISet.newISet();
        
        // B
        succResult = UtilNcsb.collectSuccessors(mOperand, mNCSB.getBSet(), letter, true);
        //If q in C\F or (B\F), then tr(q, a) should not be not empty
        if(!succResult.hasSuccessor) return UtilISet.newISet();
        ISet mayInB = succResult.mSuccs;
        ISet mustInC = succResult.mMinusFSuccs;       // d(B\F, a)
        ISet interFSuccs = succResult.mInterFSuccs;   // d(B/\F, a)

        // C\B
        ISet cMinusB = mNCSB.copyCSet();
        cMinusB.andNot(mNCSB.getBSet());
        succResult = UtilNcsb.collectSuccessors(mOperand, cMinusB, letter, true);
        //If q in C\F or (B\F), then tr(q, a) should not be not empty
        if(!succResult.hasSuccessor) return UtilISet.newISet();
        mustInC.or(succResult.mMinusFSuccs);        // d(C\F, a)
        interFSuccs.or(succResult.mInterFSuccs);    // d(C/\F, a)
        
        // C must not overlap with S
        if(mustInC.overlap(mustInS)) {
            return UtilISet.newISet(); 
        }
        
        // N
        succResult = UtilNcsb.collectSuccessors(mOperand, mNCSB.getNSet(), letter, false);
        ISet mustInN = succResult.mSuccs.clone();
        mustInN.and(mComplement.mNondetStates);  // d(N, a) /\ Q1
        ISet mayInC = succResult.mSuccs;
        mayInC.andNot(mustInN);                  // d(N, a) /\ Q2
        
        mayInC.or(interFSuccs);                  // (d(N, a) /\ Q2) \/ (d(C/\F, a))
        ISet mayInS = mayInC.clone();
        mayInS.andNot(mOperand.getFinalStates());   // nondeterministic successors
        mustInC.or(mayInC);
        
        PowerSet ps = new PowerSet(mayInS);
        ISet succs = UtilISet.newISet();
        while(ps.hasNext()) {
            // now we create initial states
            ISet toS = ps.next();
            ISet C = mustInC.clone();
            C.andNot(toS);
            toS.or(mustInS);
            NCSB ncsb = null;
            if(mNCSB.getBSet().isEmpty()) {
                // B' = C'
                ncsb = new NCSB(mustInN, C, toS, C);
            }else {
                // B' = d(B, a) /\ C'
                ISet Bp = mayInB.clone();
                Bp.and(C); 
                ncsb = new NCSB(mustInN, C, toS, Bp);
            }
            if(C.overlap(toS)) {
                // do not add this state
                continue;
            }
            StateNcsb state = mComplement.getOrAddState(ncsb);
            succs.set(state.getId());
            super.addSuccessor(letter, state.getId());
        }
        
        return succs;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(!(obj instanceof StateNcsb)) {
            return false;
        }
        StateNcsb other = (StateNcsb)obj;
        return  mNCSB.equals(other.mNCSB);
    }
    
    @Override
    public String toString() {
        return mNCSB.toString();
    }
    

    @Override
    public int hashCode() {
        return mNCSB.hashCode();
    }
}
