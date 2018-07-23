package operation.complement.rank;

import java.util.Collection;

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
        // first compute subset state
        if(!mLevelRanking.isRanked()) {
            // subset construction
            ISet succs = UtilRank.collectSuccessors(mOperand, mLevelRanking.getS(), letter);
            LevelRanking lvlSucc = new LevelRanking(false);
            lvlSucc.setS(succs);
            StateRankTight succ = mComplement.getOrAddState(lvlSucc);
            super.addSuccessor(letter, succ.getId());
        }
        // add ranked states
        LevelRankingConstraint constraint = null;
        if(mLevelRanking.isRanked()) {
            constraint = getRankedConstraint(letter);
        }else {
            ISet succs = UtilRank.collectSuccessors(mOperand, mLevelRanking.getS(), letter);
            constraint =  getUnRankedConstraint(succs);
        }
        
        LevelRankingGenerator generator = new LevelRankingGenerator(mOperand);
        System.out.println("state=" + this.toString() + " letter=" + letter);
        Collection<LevelRanking> lvlRanks = generator.generateLevelRankings(constraint);
        
        for(LevelRanking lvlRank : lvlRanks) {
            if(!lvlRank.isTight()) continue;
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
    
    private LevelRankingConstraint getRankedConstraint(int letter) {
           LevelRankingConstraint constraint = new LevelRankingConstraint();
           for(final int s : mLevelRanking.getS()) {
               for(final int t : mOperand.getState(s).getSuccessors(letter)) {
                   constraint.addConstraint(t, mLevelRanking.getLevelRank(s), mLevelRanking.isInO(s), mLevelRanking.isOEmpty());
               }
           }
           return constraint;
       }


}
