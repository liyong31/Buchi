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

package operation.complement.rank;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import main.Options;
import util.ISet;
import util.UtilISet;

public class StateRankTight extends StateRank<ComplementRankTight> {
    
    public StateRankTight(ComplementRankTight complement, int id, LevelRanking lvlRank) {
        super(complement, id, lvlRank);
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();

    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        LevelRankingConstraint constraint = null;
        LevelRankingGenerator generator = new LevelRankingGenerator(mOperand);
        Collection<LevelRanking> lvlRanks;
        // first compute subset state
        if(!mLevelRanking.isRanked()) {
            // subset construction
            //d1
            ISet succs = UtilRank.collectSuccessors(mOperand, mLevelRanking.getS(), letter);
            LevelRanking lvlSucc = new LevelRanking(false, Options.mTurnwise);
            lvlSucc.setS(succs);
            StateRankTight succ = mComplement.getOrAddState(lvlSucc);
            super.addSuccessor(letter, succ.getId());
            constraint =  getUnRankedConstraint(succs);
            lvlRanks = generator.generateLevelRankings(constraint);
        }else {
            constraint = UtilRank.getRankedConstraint(mOperand, mLevelRanking, letter);
            if(Options.mReduceOutdegree) {
                Set<LevelRanking> tempLvlRanks = new HashSet<>();
                LevelRanking lvlRank = new LevelRanking(true, Options.mTurnwise);
                for(final int s : constraint.getS()) {
                    int rank = constraint.getLevelRank(s);
                    lvlRank.addLevelRank(s, rank, LevelRanking.isEven(rank) && constraint.isInO(s));
                }
                tempLvlRanks.add(lvlRank);
                lvlRanks = tempLvlRanks;
            }else {
                lvlRanks = generator.generateLevelRankings(constraint, Options.mMinusOne);
            }
        }
        
        System.out.println("state=" + this.toString() + " letter=" + letter);
        
        for(LevelRanking lvlRank : lvlRanks) {
            // only allow tight level rankings
            if(! lvlRank.isTight()) {
                continue;
            }
            if(mLevelRanking.isRanked()) {
                // d3, rank(f) = rank(f')
                if((lvlRank.getMaximalRank() != mLevelRanking.getMaximalRank())) {
                    continue;
                }else {
                    if(Options.mTurnwise) {
                        lvlRank = getCutpointLevelRanking(lvlRank);
                    }
                }
            }else if(Options.mReduceOutdegree && ! lvlRank.isMaximallyTight(mOperand)) {
                // d2 only allow maximal tight level ranking
                continue;
            }
            StateRankTight succ = mComplement.getOrAddState(lvlRank);
            super.addSuccessor(letter, succ.getId());
            System.out.println("Successor: " + succ.getId() + " = " + succ);
        }
        
        return super.getSuccessors(letter);
    }
    
    /**
     * firstly the successor has to be tight and secondly maximal rank does not change
     * **/
    private boolean isValidLevelRanking(LevelRanking lvlRank) {
        // tight, either predecessor is not ranked or the maximum rank should be the same
        
        return lvlRank.isTight() && (!mLevelRanking.isRanked() || (lvlRank.getMaximalRank() == mLevelRanking.getMaximalRank())); 
    }
    
    private LevelRankingConstraint getUnRankedConstraint(ISet succs) {
        // second compute ranking state
        int maxRank = succs.cardinality();
        for(final int s : succs) {
            if(mOperand.isFinal(s)) {
                maxRank --;
            }
        }
        maxRank = Math.max(0, maxRank * 2 - 1);
        LevelRankingConstraint constraint = new LevelRankingConstraint();
        for(final int s : succs) {
            if(mOperand.isFinal(s)) {
                constraint.addConstraint(s, Math.max(0, maxRank - 1), false, false);
            }else {
                constraint.addConstraint(s, maxRank, false, false);
            }
        }
        return constraint;
    }
    
    
    private LevelRanking getCutpointLevelRanking(LevelRanking lvlRank) {
        // rank(f) = rank(f') already satisfied
        assert mLevelRanking.getMaximalRank() == lvlRank.getMaximalRank();
        int iprime ;
        if(mLevelRanking.isOEmpty()) {
            // O is empty, i' = (i + 2) mod (rank(f') + 1), O' = f'^{-1}(i')
            iprime = (mLevelRanking.getTurn() + 2) % (lvlRank.getMaximalRank() + 1);
        }else {
            // O is not empty, i' = i, O' = d(O) /\ f'^{-1}(i')
            iprime = mLevelRanking.getTurn();
        }
        LevelRanking lvlSucc = new LevelRanking(true, true);
        lvlSucc.setTurn(iprime);
        for(final int s : lvlRank.getS()) {
            boolean isInO = false;
            if(mLevelRanking.isOEmpty()) {
                // O is empty, i' = (i + 2) mod (rank(f') + 1), O' = f'^{-1}(i')
                isInO = lvlRank.getLevelRank(s) == iprime;
            }else {
                // O is not empty, i' = i, O' = d(O) /\ f'^{-1}(i')
                isInO = lvlRank.getLevelRank(s) == iprime && lvlRank.getO().get(s);
            }
            lvlSucc.addLevelRank(s, lvlRank.getLevelRank(s), isInO);
        }
        if(Options.mReduceOutdegree) {
            // do some thing here
        }
        return lvlSucc;
    }


}
