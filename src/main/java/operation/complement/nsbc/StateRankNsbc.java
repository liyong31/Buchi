package operation.complement.nsbc;

import automata.IBuchi;
import main.Options;
import operation.complement.rank.LevelRanking;
import operation.complement.rank.LevelRankingConstraint;
import operation.complement.rank.StateRank;
import operation.complement.rank.UtilRank;
import util.ISet;

public class StateRankNsbc extends StateRank<ComplementRankNsbc> {

    public StateRankNsbc(ComplementRankNsbc complement, int id, LevelRanking lvlRank) {
        super(complement, id, lvlRank);
    }
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        System.out.println("Computing successors for letter " + letter);
        // first compute subset state
        if(!mLevelRanking.isRanked()) {
            // subset construction
            //d1
            ISet succs = UtilRank.collectSuccessors(mOperand, mLevelRanking.getS(), letter);
            LevelRanking lvlSucc = new LevelRanking(false, Options.mTurnwise);
            lvlSucc.setS(succs);
            StateRankNsbc succ = mComplement.getOrAddState(lvlSucc);
            super.addSuccessor(letter, succ.getId());
            //d2
            lvlSucc = getUnRankedSuccessor(succs);
            if(lvlSucc.isTight()) {
                succ = mComplement.getOrAddState(lvlSucc);
                super.addSuccessor(letter, succ.getId());
            }
        }else {
            // only tight ranked successors
            //d3, successors should not be empty
            LevelRanking lvlSucc = getRankedSuccessor(letter);
            if(lvlSucc.isTight()) {
                StateRankNsbc succ = mComplement.getOrAddState(lvlSucc);
                super.addSuccessor(letter, succ.getId());
            }
        }

        return super.getSuccessors(letter);
    }
    
    private LevelRanking getUnRankedSuccessor(ISet succs) {
        ISet rankThree = succs.clone();
        rankThree.and(mComplement.mNondetStates);
        ISet rankOne = succs.clone();
        rankOne.and(mComplement.mDetStates);
        ISet rankTwo = rankOne.clone();
        rankOne.andNot(mComplement.getOperand().getFinalStates());
        rankTwo.andNot(rankOne);
        LevelRanking lvlRank = new LevelRanking(true, Options.mTurnwise);
        // now we check, if rank three is empty then only leaves rank 1 and 0
        if(rankThree.isEmpty()) {
            for(final int s : rankTwo) {
                lvlRank.addLevelRank(s, LevelRanking.ZERO, false);
            }
            for(final int s : rankOne) {
                lvlRank.addLevelRank(s, LevelRanking.ONE, false);
            }
        }else {
            // now if rankOne is empty
            if(rankOne.isEmpty()) {
                for(final int s : rankThree) {
                    lvlRank.addLevelRank(s, LevelRanking.ONE, false);
                }
                for(final int s : rankTwo) {
                    lvlRank.addLevelRank(s, LevelRanking.ZERO, false);
                }
            }else {
                for(final int s : rankThree) {
                    lvlRank.addLevelRank(s, LevelRanking.THREE, false);
                }
                for(final int s : rankTwo) {
                    lvlRank.addLevelRank(s, LevelRanking.TWO, false);
                }
                for(final int s : rankOne) {
                    lvlRank.addLevelRank(s, LevelRanking.ONE, false);
                }
            }
        }
        
        return lvlRank;
    }
    
    private LevelRanking getRankedSuccessor(int letter) {
        IBuchi operand = mComplement.getOperand();
        LevelRankingConstraint constraint = UtilRank.getRankedConstraint(operand, mLevelRanking, letter);
        LevelRanking lvlRank = new LevelRanking(true, Options.mTurnwise);
        for(final int s : constraint.getS()) {
            int rank = constraint.getLevelRank(s);
            lvlRank.addLevelRank(s, rank, LevelRanking.isEven(rank) && constraint.isInO(s));
        }
        return lvlRank;
    }
    
    

}
