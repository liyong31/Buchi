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

import automata.State;
import operation.complement.ncsb.NCSB;
import util.ISet;
import util.UtilISet;

public class StateNsbcSimilar extends State {

    QuotientNsbc mQuotient;
    StateNsbc mRepresentor;
    
    ISet mEqualStates;
    
    public StateNsbcSimilar(QuotientNsbc quotient, int id, StateNsbc state) {
       super(id);
       this.mQuotient = quotient;
       this.mRepresentor = state;
       this.mEqualStates = UtilISet.newISet();
       this.mEqualStates.set(state.getId());
    }
    
    protected void addEqualStates(StateNsbc state) {
        this.mEqualStates.set(state.getId());
    }
    
    protected boolean contains(StateNsbc state) {
        return this.mEqualStates.get(state.getId());
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        ISet succs = UtilISet.newISet();
        ComplementNsbc complement = mQuotient.mComplement;
        for(final int state : this.mEqualStates) {
            for(final int succ : complement.getState(state).getSuccessors(letter)) {
                StateNsbcSimilar stateSucc = mQuotient.getOrAddState(complement.getStateNsbc(succ));
                super.addSuccessor(letter, stateSucc.getId());
                succs.set(stateSucc.getId());
            }
        }
        
        return succs;
    }
    
    @Override
    public String toString() {
        return mRepresentor.toString() + ":" + mEqualStates;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(!(obj instanceof StateNsbcSimilar)){
            return false;
        }
        StateNsbcSimilar other = (StateNsbcSimilar)obj;
        if(mRepresentor.isColored() != other.mRepresentor.isColored()) {
            return false;
        }
        if(mQuotient.mComplement.isFinal(mRepresentor.getId()) != mQuotient.mComplement.isFinal(other.mRepresentor.getId())) {
            return false;
        }
        if(hasEqualOutEdges(other.mRepresentor)) {
            return true;
        }
        return this.mRepresentor.equals(other.mRepresentor);
    }
    
    protected boolean hasCode = false;
    protected int mHashCode;
    @Override
    public int hashCode() {
        if(hasCode) return mHashCode;
        hasCode = true;
        final int prime = 31;
        mHashCode = 1;
//        for(int letter = 0; letter < mQuotient.getAlphabetSize(); letter ++) {
//            mHashCode = prime * mHashCode + NCSB.hashValue(mRepresentor.getSuccessors(letter));
//        }
        return mHashCode;
    }
    
    private boolean hasSameOutEdges(StateNsbc other) {
        for(int letter = 0; letter < mQuotient.getAlphabetSize(); letter ++) {
            if(! mRepresentor.getSuccessors(letter).equals(other.getSuccessors(letter))) {
                return false;
            }
        }
        return true;
    }
    
    private boolean hasEqualOutEdges(StateNsbc other) {
        for(int letter = 0; letter < mQuotient.getAlphabetSize(); letter ++) {
            // first to check whether it has same outgoing states 
            ISet fstSuccs = mRepresentor.getSuccessors(letter);
            ISet sndSuccs = other.getSuccessors(letter);
            boolean result = fstSuccs.equals(sndSuccs);
            
            if(! result && other.isColored()) return false;
            if(! result) {
                // if they do not have same outgoing states, check more
                for(final int fstSucc : fstSuccs) {
                    StateNsbc repSucc = mQuotient.mComplement.getStateNsbc(fstSucc);
                    if(repSucc.isColored()) {
                        result = false;
                        // try to find one state fairly equal to repSucc
                        for(final int sndSucc : sndSuccs) {
                            StateNsbc otherSucc = mQuotient.mComplement.getStateNsbc(sndSucc);
                            if(!otherSucc.isColored()) {
                                continue;
                            }
                            result = repSucc.isFairlyEqual(otherSucc);
                            if(result) break;
                        }
                        // no fairly equal state
                        if(! result) return false;
                    }else if(!sndSuccs.get(fstSucc)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
