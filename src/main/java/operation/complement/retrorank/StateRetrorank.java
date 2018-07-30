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

package operation.complement.retrorank;

import java.util.ArrayList;

import automata.IBuchi;
import automata.State;
import operation.complement.rank.LevelRanking;
import operation.complement.tuple.OrderedSetsGenerator;
import util.ISet;
import util.UtilISet;

public class StateRetrorank extends State {

    protected final ComplementRetrorank mComplement;
    protected final RetrospectiveRank mRetroRank;
    
    public StateRetrorank(ComplementRetrorank complement, int id, RetrospectiveRank retroRank) {
        super(id);
        this.mComplement = complement;
        this.mRetroRank = retroRank;
        this.mVisitedLetters = UtilISet.newISet();
    }
    
    protected final ISet mVisitedLetters;
    
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        IBuchi operand = mComplement.getOperand();
        // compute successors
        if(!mRetroRank.hasRanked()) {
            OrderedSetsGenerator generator = new OrderedSetsGenerator(operand, mRetroRank.mOrdSets, letter);
            ArrayList<ISet> nextOrdSets = generator.getResult().mNextOrdSets;
            RetrospectiveRank nextRetroRank = new RetrospectiveRank(false);
            for(int i = 0; i < nextOrdSets.size(); i ++) {
                nextRetroRank.add(nextOrdSets.get(i));
            }
            StateRetrorank nextState = mComplement.getOrAddState(nextRetroRank);
            super.addSuccessor(letter, nextState.getId());
            // now we compute ranked state
            nextRetroRank = nextRetroRank.toRank(operand.getFinalStates());
            nextState = mComplement.getOrAddState(nextRetroRank);
            super.addSuccessor(letter, nextState.getId());
        }else {
            // now we compute the successor of ranked states
            RetrospectiveRank nextRetroRank = new RetrospectiveRank(true);
            for (final int s : mRetroRank.mLvlRank.getS()) {
                for (final int t : operand.getState(s).getSuccessors(letter)) {
                    int rank = mRetroRank.mLvlRank.getLevelRank(s);
                    // final state should have even rank
                    if(LevelRanking.isOdd(rank) && operand.isFinal(t)) {
                        rank = Math.max(LevelRanking.ZERO, rank - 1);
                    }
                    nextRetroRank.addRank(t, rank, mRetroRank.mLvlRank.isInO(s) || mRetroRank.mLvlRank.isOEmpty());
                }
            }
            nextRetroRank = nextRetroRank.tighten(operand.getFinalStates());
            StateRetrorank nextState = mComplement.getOrAddState(nextRetroRank);
            super.addSuccessor(letter, nextState.getId());
        }
        
        return super.getSuccessors(letter);
    }

    @Override
    public String toString() {
        return  mRetroRank + "";
    }
    
    @Override
    public int hashCode() {
        return mRetroRank.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(this.getClass().isInstance(obj)) {
            StateRetrorank other = (StateRetrorank)obj;
            return mRetroRank.equals(other.mRetroRank);
        }
        return false;
    }
}
