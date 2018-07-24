package operation.complement.rank;

import java.util.Collection;

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
        // first compute subset state
        if(!mLevelRanking.isRanked()) {
            // subset construction
            ISet succs = UtilRank.collectSuccessors(mOperand, mLevelRanking.getS(), letter);
            LevelRanking lvlSucc = new LevelRanking(false, Options.mTurnwise);
            lvlSucc.setS(succs);
            StateRankTight succ = mComplement.getOrAddState(lvlSucc);
            super.addSuccessor(letter, succ.getId());
            constraint =  getUnRankedConstraint(succs);
        }else {
            constraint = UtilRank.getRankedConstraint(mOperand, mLevelRanking, letter);
        }
        
        LevelRankingGenerator generator = new LevelRankingGenerator(mOperand);
        System.out.println("state=" + this.toString() + " letter=" + letter);
        Collection<LevelRanking> lvlRanks = generator.generateLevelRankings(constraint);
        
        for(LevelRanking lvlRank : lvlRanks) {
            if(!isValidTightLevelRanking(lvlRank)) continue;
            if(Options.mTurnwise ) {
                lvlRank = getCutpointLevelRanking(lvlRank);
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
    private boolean isValidTightLevelRanking(LevelRanking lvlRank) {
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
        if(mLevelRanking.isOEmpty()) {
            // O is empty, i' = (i + 2) mod (rank(f') + 1), O' = f'^{-1}(i')
            int ip = (mLevelRanking.getTurn() + 2) % (lvlRank.getMaximalRank() + 1);
            LevelRanking lvlSucc = new LevelRanking(true, true);
            lvlSucc.setTurn(ip);
            for(final int s : lvlRank.getS()) {
                boolean isInO = false;
                if(lvlRank.getLevelRank(s) == ip) {
                    isInO = true;
                }
                lvlSucc.addLevelRank(s, lvlRank.getLevelRank(s), isInO);
            }
            return lvlSucc;
        }else {
            // O is not empty, i' = i, O' = d(O) /\ f'^{-1}(i')
            int ip = mLevelRanking.getTurn();
            LevelRanking lvlSucc = new LevelRanking(true, true);
            lvlSucc.setTurn(ip);
            for(final int s : lvlRank.getS()) {
                boolean isInO = false;
                if(lvlRank.getLevelRank(s) == ip && lvlRank.getO().get(s)) {
                    isInO = true;
                }
                lvlSucc.addLevelRank(s, lvlRank.getLevelRank(s), isInO);
            }
            return lvlSucc;
        }
    }


}
