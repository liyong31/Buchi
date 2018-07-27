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
        if(letter == 8) {
            System.out.println("Hello");
        }
        mVisitedLetters.set(letter);
        Collection<LevelRanking> lvlRankSuccs;
        // first compute subset state
        if(!mLevelRanking.isRanked()) {
            // subset construction
            //d1
            ISet succs = UtilRank.collectSuccessors(mOperand, mLevelRanking.getS(), letter);
            LevelRanking lvlSucc = new LevelRanking(false, Options.mTurnwise);
            lvlSucc.setS(succs);
            StateRankTight succ = mComplement.getOrAddState(lvlSucc);
            super.addSuccessor(letter, succ.getId());
            LevelRankingConstraint constraint =  getUnRankedConstraint(succs);
            //d2, successors should not be empty
            lvlRankSuccs = getUnRankedSuccessorTightLevelRankings(constraint);
        }else {
            // only tight ranked successors
            //d3, successors should not be empty
            LevelRankingConstraint constraint = UtilRank.getRankedConstraint(mOperand, mLevelRanking, letter);
            lvlRankSuccs = getRankedSuccessorTightLevelRankings(constraint);
        }
        System.out.println("state=" + this.toString() + " letter=" + letter);
        for(LevelRanking lvlRank : lvlRankSuccs) {
            // only allow tight level rankings
            StateRankTight succ = mComplement.getOrAddState(lvlRank);
            super.addSuccessor(letter, succ.getId());
            System.out.println("Successor: " + succ.getId() + " = " + succ);
        }
        
        return super.getSuccessors(letter);
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
    
    /**
     * tight level ranking for successors of unranked states 
     * **/
    private Set<LevelRanking> getUnRankedSuccessorTightLevelRankings(
            LevelRankingConstraint constraint) {
        Set<LevelRanking> result = new HashSet<>();
        if(constraint.getS().isEmpty()) return result;
        LevelRankingGenerator generator = new LevelRankingGenerator(mOperand);
        Collection<LevelRanking> lvlRankSuccs = generator.generateLevelRankings(constraint);
        
        for(final LevelRanking lvlRankSucc : lvlRankSuccs) {
            // ignore non-tight level rankings
            boolean valid = false;
            if(Options.mReduceOutdegree) {
                // d2 only allow maximal tight level ranking
                valid = lvlRankSucc.isMaximallyTight(mOperand);
            }else {
                valid = lvlRankSucc.isTight();
            }
            if(valid) {
                result.add(lvlRankSucc);
            }
        }
        return result;
    }
    /**
     * tight level ranking for successors of ranked states 
     * **/
    private Set<LevelRanking> getRankedSuccessorTightLevelRankings(
            LevelRankingConstraint constraint) {
        Set<LevelRanking> result = new HashSet<>();
        if(constraint.getS().isEmpty()) return result;
        Set<LevelRanking> tightLvlRanks = getTightLevelRankings(constraint);
        for(final LevelRanking tightLvlRank : tightLvlRanks) {
            LevelRanking lvlRank = tightLvlRank;
            if(Options.mTurnwise) {
                lvlRank = getCutpointLevelRanking(tightLvlRank);
            }
            if(lvlRank != null) {
                result.add(lvlRank);
            }
        }
        return result;
    }
    
    /**
     * Gamma 3 transitions, get maximal rankings
     * */
    private Set<LevelRanking> getMaximalSuccessorTightLevelRanking(
            LevelRankingConstraint constraint) {
        Set<LevelRanking> resultLvlRanks = new HashSet<>();
        LevelRanking lvlRank = new LevelRanking(true, Options.mTurnwise);
        for(final int s : constraint.getS()) {
            int rank = constraint.getLevelRank(s);
            lvlRank.addLevelRank(s, rank, LevelRanking.isEven(rank) && constraint.isInO(s));
        }
        // should be tight
        if(lvlRank.isTight()) {
            resultLvlRanks.add(lvlRank);
        }
        return resultLvlRanks;
    }
    
    private Set<LevelRanking> getTightLevelRankings(
            LevelRankingConstraint constraint) {
        if(Options.mReduceOutdegree) {
            // if now it is in ReduceOutdegree, get the maximal 
            return getMaximalSuccessorTightLevelRanking(constraint);
        }else {
            LevelRankingGenerator generator = new LevelRankingGenerator(mOperand);
            // now we have to generate all kinds of tight level rankings
            Collection<LevelRanking> lvlRankSuccs = generator.generateLevelRankings(constraint, Options.mMinusOne);
            Set<LevelRanking> result = new HashSet<>();
            for(LevelRanking lvlRank : lvlRankSuccs) {
                // d3, rank(f) = rank(f')
                boolean valid = lvlRank.getMaximalRank() == mLevelRanking.getMaximalRank();
                // has to be tight level ranking
                valid = valid && lvlRank.isTight();
                if(valid) {
                    result.add(lvlRank);
                }
            }
            return result;
        }
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
        //TODO check
        if(Options.mReduceOutdegree) {
            lvlSucc = getMaximalFinalSuccessorLevelRanking(lvlRank);
        }
        return lvlSucc;
    }
    
    /**
     * Gamma 4 transitions
     * i' /= 0 \/ O is empty, then O' = empty and g'(q) = g(q) - 1 for every state in O
     * 
     * otherwise no successor
     * 
     * */ 
    private LevelRanking getMaximalFinalSuccessorLevelRanking(LevelRanking lvlRank) {
        LevelRanking lvlSucc = lvlRank.clone();
        lvlSucc.getO().clear();
        if(lvlRank.getTurn() != 0 || lvlRank.isOEmpty()) {
            for(int s : lvlRank.getO()) {
                int rank = Integer.max(LevelRanking.ZERO, lvlRank.getLevelRank(s) - 1);
                lvlSucc.addLevelRank(s, rank, false);
            }
        }else {
            lvlSucc = null;
        }
        return lvlRank;
    }


}
