package operation.complement.rank;

import automata.IBuchi;
import automata.State;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import main.Options;
import operation.complement.Complement;

public abstract class ComplementRank<S extends State>  extends Complement {
    
    protected TObjectIntMap<S> mStateIndices;
    
    public ComplementRank(IBuchi operand) {
        super(operand);
    }
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        LevelRanking lvlRnk = null;
        if(!Options.mTightRank) {
            int n = mOperand.getStateSize();
            int r = mOperand.getFinalStates().cardinality();
            lvlRnk = new LevelRanking(true);
            for(final int init : mOperand.getInitialStates()) {
                lvlRnk.addLevelRank(init, 2*(n - r), false);
            }
        }else {
            lvlRnk = new LevelRanking(false);
            lvlRnk.setS(mOperand.getInitialStates());
        }
        S stateLvlRnk = getOrAddState(lvlRnk);
        this.setInitial(stateLvlRnk.getId());
    }
    
    @SuppressWarnings("unchecked")
    protected S getStateLevelRanking(int id) {
        return (S)getState(id);
    }
    
    protected abstract S makeRankState(int id, LevelRanking lvlRank);

    protected S getOrAddState(LevelRanking lvlRank) {
        S state = makeRankState(0, lvlRank);
        if(mStateIndices.containsKey(state)) {
            return getStateLevelRanking(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            S newState = makeRankState(index, lvlRank);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(lvlRank.isFinal()) setFinal(index);
            return newState;
        }
    }

    @Override
    public String getName() {
        return "ComplementRank";
    }

}
