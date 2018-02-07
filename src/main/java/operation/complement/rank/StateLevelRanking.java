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

public class StateLevelRanking extends State {
 
    private final ComplementNBA mComplement;
    private final IBuchi mOperand;
    private final LevelRankingState mLevelRanking; // (S, O, f)
    
    public StateLevelRanking(ComplementNBA complement, int id, LevelRankingState lvlRank) {
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
        Collection<LevelRankingState> lvlRanks = generator.generateLevelRankings(constraint);
        if(letter == 1) {
            System.out.println("letter: " + letter);
        }
        for(LevelRankingState lvlRank : lvlRanks) {
            StateLevelRanking succ = mComplement.getOrAddState(lvlRank);
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
        if(obj instanceof StateLevelRanking) {
            StateLevelRanking other = (StateLevelRanking)obj;
            return mLevelRanking.equals(other.mLevelRanking);
        }
        return false;
    }

}
