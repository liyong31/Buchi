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

package operation.complement.rank;

import java.util.Collection;

import automata.IBuchi;

import automata.State;
import util.ISet;
import util.UtilISet;

/**
 * a representation for a state in rank-based
 * complementation algorithm (S, O, f)
 * */

public class StateRankKV extends State {
 
    private final ComplementRankKV mComplement;
    private final IBuchi mOperand;
    private final LevelRankingState mLevelRanking; // (S, O, f)
    
    public StateRankKV(ComplementRankKV complement, int id, LevelRankingState lvlRank) {
        super(id);
        this.mComplement = complement;
        this.mOperand = complement.getOperand();
        this.mLevelRanking = lvlRank;
    }
    
    public LevelRankingState getLevelRanking() {
        return mLevelRanking;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();

    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        LevelRankingConstraint constraint = new LevelRankingConstraint();
        ISet S = mLevelRanking.getS();
        for(final int s : S) {
            for(final int t : mOperand.getState(s).getSuccessors(letter)) {
                constraint.addConstraint(t, mLevelRanking.getLevelRank(s), mLevelRanking.isInO(s), mLevelRanking.isOEmpty());
            }
        }
        LevelRankingGenerator generator = new LevelRankingGenerator(mComplement);
        System.out.println("state=" + this.toString() + " letter=" + letter);
        Collection<LevelRankingState> lvlRanks = generator.generateLevelRankings(constraint);
        
        for(LevelRankingState lvlRank : lvlRanks) {
            StateRankKV succ = mComplement.getOrAddState(lvlRank);
            super.addSuccessor(letter, succ.getId());
            System.out.println("Successor: " + succ.getId() + " = " + succ);
        }
        
        return super.getSuccessors(letter);
    }
    
    @Override
    public String toString() {
        return  mLevelRanking + "";
    }
    
    @Override
    public int hashCode() {
        return mLevelRanking.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(obj instanceof StateRankKV) {
            StateRankKV other = (StateRankKV)obj;
            return mLevelRanking.equals(other.mLevelRanking);
        }
        return false;
    }

}
