/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi which is a simple version of SemiBuchi.
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
import automata.IState;
import automata.State;
import main.Options;
import util.ISet;
import util.UtilISet;

public class StateNCSB extends State {

	private final NCSB mNCSB;
	
	private final IBuchi mOperand;
	private final ComplementSDBA mComplement;
	
	public StateNCSB(ComplementSDBA complement, int id, NCSB ncsb) {
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
		// B
		SuccessorResult succResult = collectSuccessors(mNCSB.getBSet(), letter, true);
		if(!succResult.hasSuccessor) return UtilISet.newISet();
		ISet BSuccs = succResult.mSuccs;
		ISet minusFSuccs = succResult.mMinusFSuccs;
		ISet interFSuccs = succResult.mInterFSuccs;

		// C\B
		ISet cMinusB = mNCSB.copyCSet();
		cMinusB.andNot(mNCSB.getBSet());
		succResult = collectSuccessors(cMinusB, letter, !Options.mLazyS);
		if(!succResult.hasSuccessor) return UtilISet.newISet();
		ISet CSuccs = succResult.mSuccs;
		CSuccs.or(BSuccs);
		minusFSuccs.or(succResult.mMinusFSuccs);
		interFSuccs.or(succResult.mInterFSuccs);
		
		// N
		succResult = collectSuccessors(mNCSB.getNSet(), letter, false);
		if(!succResult.hasSuccessor) return UtilISet.newISet();
		ISet NSuccs = succResult.mSuccs;

		// S
		succResult = collectSuccessors(mNCSB.getSSet(), letter, false);
		if(!succResult.hasSuccessor) return UtilISet.newISet();
		ISet SSuccs = succResult.mSuccs;
		
		return computeSuccessors(new NCSB(NSuccs, CSuccs, SSuccs, BSuccs), minusFSuccs, interFSuccs, letter);
	}
	
    @Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof StateNCSB)) {
			return false;
		}
		StateNCSB other = (StateNCSB)obj;
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
	// -------------------------------------------------

	/**
	 * If q in C\F or (B\F), then tr(q, a) should not be not empty
	 * */
	private boolean noTransitionAssertion_MinusF(int state, ISet succs) {
//	    if(Options.mLazyS && Options.mAntichain) return true;
		return !mOperand.isFinal(state) && succs.isEmpty();
	}
	
	private SuccessorResult collectSuccessors(ISet states, int letter, boolean testTrans) {
		SuccessorResult result = new SuccessorResult();
		for(final int stateId : states) {
		    IState state = mOperand.getState(stateId);
			ISet succs = state.getSuccessors(letter);
			if (testTrans && noTransitionAssertion_MinusF(stateId, succs)) {
				result.hasSuccessor = false;
				return result;
			}
			result.mSuccs.or(succs);
			if(testTrans) {
				if(mOperand.isFinal(stateId)) {
					result.mInterFSuccs.or(succs);
				}else {
					result.mMinusFSuccs.or(succs);
				}
			}
		}
		return result;
	}
	
	private ISet computeSuccessors(NCSB succNCSB, ISet minusFSuccs
			, ISet interFSuccs, int letter) {
		// check d(S) and d(C)
		if(succNCSB.getSSet().overlap(mOperand.getFinalStates())
		|| minusFSuccs.overlap(succNCSB.getSSet())) {
			return UtilISet.newISet();
		}
		SuccessorGenerator generator = new SuccessorGenerator(mNCSB.getBSet().isEmpty()
															, succNCSB
															, minusFSuccs
															, interFSuccs
															, mOperand.getFinalStates());
		ISet succs = UtilISet.newISet();
		while(generator.hasNext()) {
		    NCSB ncsb = generator.next();
		    if(ncsb == null) continue;
			StateNCSB succ = mComplement.getOrAddState(ncsb);
			super.addSuccessor(letter, succ.getId());
			succs.set(succ.getId());
		}

		return succs;
	}

}
