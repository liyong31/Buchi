package operation.complement.rank;

import automata.Buchi;
import automata.IBuchi;
import automata.IState;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.IUnaryOp;
import operation.explore.Explore;

public class ComplementNBA extends Buchi implements IUnaryOp<IBuchi, IBuchi> {

    private final IBuchi mOperand;
    private final TObjectIntMap<StateLevelRanking> mStateIndices = new TObjectIntHashMap<>();
    
    public ComplementNBA(IBuchi operand) {
        super(operand.getAlphabetSize());
        this.mOperand = operand;
        computeInitialStates();
    }
    
    private void computeInitialStates() {
        // compute initial states
        int n = mOperand.getStateSize();
        int r = mOperand.getFinalStates().cardinality();
        LevelRankingState lvlRnk = new LevelRankingState();
        for(final int init : mOperand.getInitialStates()) {
            lvlRnk.addLevelRank(init, 2*(n - r), false);
        }
        StateLevelRanking stateLvlRnk = getOrAddState(lvlRnk);
        this.setInitial(stateLvlRnk.getId());
    }
    
    protected StateLevelRanking getStateLevelRanking(int id) {
        return (StateLevelRanking)getState(id);
    }

    protected StateLevelRanking getOrAddState(LevelRankingState lvlRank) {
        StateLevelRanking state = new StateLevelRanking(this, 0, lvlRank);
        if(mStateIndices.containsKey(state)) {
            return getStateLevelRanking(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateLevelRanking newState = new StateLevelRanking(this, index, lvlRank);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(lvlRank.isOEmpty()) setFinal(index);
            return newState;
        }
    }

    @Override
    public String getName() {
        return "ComplementNBA";
    }

    @Override
    public IBuchi getOperand() {
        return mOperand;
    }

    @Override
    public IBuchi getResult() {
        return this;
    } 
    
    public static void main(String[] args) {
        Buchi buchi = new Buchi(2);
        IState aState = buchi.addState();
        IState bState = buchi.addState();
        
        aState.addSuccessor(0, aState.getId()); 
        aState.addSuccessor(1, aState.getId());     
        aState.addSuccessor(1, bState.getId());     
        bState.addSuccessor(1, bState.getId());
        
        buchi.setFinal(bState);
        buchi.setInitial(aState);
        
        System.out.println(buchi.toDot());
        
        ComplementNBA complement = new ComplementNBA(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
    }

}
