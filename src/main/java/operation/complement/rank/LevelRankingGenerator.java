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

import automata.Buchi;
import automata.IBuchi;
import main.Options;
import util.ISet;
import util.UtilISet;

public class LevelRankingGenerator extends LevelRankingConstraint {
    
    protected IBuchi mOperand;
    
    public LevelRankingGenerator(IBuchi operand) {
        mOperand = operand;
    }
    
    public Set<LevelRanking> generateLevelRankings(LevelRankingConstraint constraint) {
        return generateLevelRankings(constraint, false);
    }
    
    /**
     * only decrease by one
     * */
    public Set<LevelRanking> generateLevelRankings(LevelRankingConstraint constraint, boolean minusOne) {
        ISet set = null;
        ISet S = constraint.copyS();
        ISet O = constraint.getO();
        boolean decreaseInS = !Options.mLazyS || O.isEmpty();
        if(decreaseInS) {
            set = S;
        }else {
            // only keep guessing the successors in O
            set = O;
        }
        LevelRankingConstraint lvlConstraint = new LevelRankingConstraint();
        for(final int s : set) {
            lvlConstraint.addConstraint(s, constraint.getLevelRank(s), constraint.isInO(s), false);
        }
        Set<LevelRanking> states = generateLevelRankingsInner(lvlConstraint, minusOne);
        if(decreaseInS) {
            return states;
        }else {
            // should also set elements not in O
            S.andNot(O);
            if(!S.isEmpty() && states.isEmpty()) {
                // we donot do nondeterministic guessing here
                LevelRanking state = new LevelRanking(true, Options.mTurnwise);
                for(final int s : S) {
                    state.addLevelRank(s, constraint.getLevelRank(s), false);
                }
                states.add(state);
            }else {
                for(LevelRanking state : states) {
                    for(final int s : S) {
                        state.addLevelRank(s, constraint.getLevelRank(s), false);
                    }
                }
            }
            
            return states;
        }
        
    }
    
    // NOT USED any more
    private void generateLevelRankings(LevelRankingConstraint constraint
            , Set<LevelRanking> states, int i, int[] succStates, ISet[] listOfRanks) {
        if(i >= listOfRanks.length) return ;
        if(i == 0) {
            states.clear();
            for(final int rank : listOfRanks[i]) {
                LevelRanking state = new LevelRanking(true, Options.mTurnwise);
                state.addLevelRank(succStates[i], rank, (isEven(rank) && constraint.isInO(succStates[i])));
                states.add(state);
            }
        }else {
            Set<LevelRanking> result = new HashSet<>();
            for(final int rank : listOfRanks[i]) {
                for(LevelRanking state : states) {
                    LevelRanking newState = state.clone();
                    newState.addLevelRank(succStates[i], rank, (isEven(rank) && constraint.isInO(succStates[i])));
                    result.add(newState);
                }
            }
            states.clear();
            states.addAll(result);
        }
        
        generateLevelRankings(constraint, states, i + 1, succStates, listOfRanks);
    }
    
    /**
     * given a maxRank, return all possible rankings
     * note that final state only have even ranks
     * **/
    private ISet getPotentialRanks(int maxRank, boolean isFinal, boolean minusOne) {
        ISet ranks = UtilISet.newISet();
        int low;
        if(minusOne) {
           low = maxRank - 1;
        }else {
           low = 0;
        }
        low = Integer.max(low, LevelRanking.ZERO);
        for(int r = maxRank; r >= low; r --) {
            if(isFinal && isOdd(r)) continue;
            ranks.set(r);
        }
        return ranks;
    }
    
    // NOT USED
    private ISet getPotentialRanks(int maxRank, boolean isFinal) {
        return getPotentialRanks(maxRank, isFinal, false);
    }
    
    private Set<LevelRanking> generateLevelRankingsInner(LevelRankingConstraint constraint, boolean minusOne) {
        Set<LevelRanking> result = new HashSet<>();
        result.add(new LevelRanking(true, Options.mTurnwise));
        for (final int state : constraint.getS()) {
            // result has no rank definition for state s yet
            Set<LevelRanking> temp = new HashSet<>();
            // add possible ranks for state s
            ISet ranks = getPotentialRanks(constraint.getLevelRank(state), mOperand.isFinal(state), minusOne);
            for (final int rank : ranks) {
                for (final LevelRanking lvlRank : result) {
                    // for every level ranking, add the rank of s
                    LevelRanking copyLvlRank = lvlRank.clone();
                    copyLvlRank.addLevelRank(state, rank, isEven(rank) && constraint.isInO(state));
                    temp.add(copyLvlRank);
                }
            }
            result = temp;
        }
        return result;
    }
    
    /**
     * Sven's STACS paper 
     * 
     * S-tight ranking function f with rank mMaxRank maximal with respect to S
     * 
     * 1. all final states in S have rank mMaxRank - 1
     * 2. exactly one state to every odd rank o < mMaxRank
     * 3. remaining states have rank mMaxRank 
     * */
    public Set<LevelRanking> generateMaximalLevelRankings(LevelRankingConstraint constraint) {
        // first fix a max rank and then add 
        Set<LevelRanking> result = new HashSet<>();
        for(int maxRank = 1; maxRank <= constraint.getMaximalRank(); maxRank += 2) {
            int evenRank = maxRank - 1;
            int numStatesSelected = (maxRank + 1) / 2;
            
        }
        return result;
    }
    
    
    public static void main(String[] args) {
        Buchi buchi = new Buchi(2);
        
        buchi.addState();
        buchi.addState();
        buchi.addState();
        
        buchi.setInitial(0);
        buchi.setFinal(1);
        
        LevelRankingGenerator generator = new LevelRankingGenerator(buchi);
        
        LevelRankingConstraint constraint = new LevelRankingConstraint();
        constraint.addConstraint(0, 5, false, false);
        constraint.addConstraint(1, 5, true, false);
        constraint.addConstraint(2, 5, false, false);
        
        Collection<LevelRanking> hhh = generator.generateLevelRankings(constraint);
        System.out.println(hhh);
        Set<LevelRanking> set2 = generator.generateLevelRankings(constraint, false);
        System.out.println(generator.generateLevelRankings(constraint, false));
        System.out.println(set2.size() == hhh.size());
        for(LevelRanking s : hhh) {
            if(!set2.contains(s)) System.out.println("false");
        }
    }

}
