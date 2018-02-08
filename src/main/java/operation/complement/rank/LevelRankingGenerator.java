package operation.complement.rank;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import automata.IBuchi;
import main.Options;
import util.ISet;
import util.UtilISet;

public class LevelRankingGenerator extends LevelRankingConstraint {
    
    protected IBuchi mOperand;
    
    public LevelRankingGenerator(ComplementNBA complement) {
        mOperand = complement.getOperand();
    }
    
    public Collection<LevelRankingState> generateLevelRankings(LevelRankingConstraint constraint) {
        //
        ISet succs = null;
        ISet S = constraint.getS();
        ISet O = constraint.getO();
        if(!Options.mLazyS) {
            succs = S;
        }else {
            // only keep guessing the successors in O
            succs = O;
        }
        
        ISet[] succRanks = new ISet[succs.cardinality()];
        int [] succStates = new int[succs.cardinality()];
        int i = 0;
        for(final int succ : succs) {
            succStates[i] = succ;
            succRanks[i] = getPotentialRanks(constraint.getLevelRank(succ), mOperand.isFinal(succ));
            i ++;
        }
        
        Set<LevelRankingState> states = new HashSet<>();
        generateLevelRankings(constraint, states, 0, succStates, succRanks);
        if(!Options.mLazyS) {
            return states;
        }else {
            // should also set elements not in O
            S.andNot(O);
            if(!S.isEmpty() && states.isEmpty()) {
                // we donot do nondeterministic guessing here
                LevelRankingState state = new LevelRankingState();
                for(final int s : S) {
                    state.addLevelRank(s, constraint.getLevelRank(s), false);
                }
                states.add(state);
            }else {
                for(LevelRankingState state : states) {
                    for(final int s : S) {
                        state.addLevelRank(s, constraint.getLevelRank(s), false);
                    }
                }
            }
            
            return states;
        }
        
    }
    
    private void generateLevelRankings(LevelRankingConstraint constraint
            , Set<LevelRankingState> states, int i, int[] succStates, ISet[] listOfRanks) {
        if(i >= listOfRanks.length) return ;
        if(i == 0) {
            states.clear();
            for(final int rank : listOfRanks[i]) {
                LevelRankingState state = new LevelRankingState();
                state.addLevelRank(succStates[i], rank, (isEven(rank) && constraint.isInO(succStates[i])));
                states.add(state);
            }
        }else {
            Set<LevelRankingState> result = new HashSet<>();
            for(final int rank : listOfRanks[i]) {
                for(LevelRankingState state : states) {
                    LevelRankingState newState = state.clone();
                    newState.addLevelRank(succStates[i], rank, (isEven(rank) && constraint.isInO(succStates[i])));
                    result.add(newState);
                }
            }
            states.clear();
            states.addAll(result);
        }
        
        generateLevelRankings(constraint, states, i + 1, succStates, listOfRanks);
    }
    
    private ISet getPotentialRanks(int maxRank, boolean isFinal) {
        ISet ranks = UtilISet.newISet();
        int low;
        if(Options.mMinusOne) {
           low = maxRank - 1;
        }else {
           low = 0;
        }
        low = Integer.max(low, LevelRankingState.ZERO);
        for(int r = maxRank; r >= low; r --) {
            if(isFinal && isOdd(r)) continue;
            ranks.set(r);
        }
        return ranks;
    }

}
