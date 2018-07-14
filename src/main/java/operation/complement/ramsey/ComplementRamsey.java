package operation.complement.ramsey;

import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.complement.Complement;
import operation.explore.UtilExplore;

/**
 * "The complementation problem for Buchi automata with applications to temporal logic"
 * by A. Prasad Sistla, Moshe Y. Vardi, and Pierre Wolper in Theoretical Computer Science.
 *  
 * **/
public class ComplementRamsey extends Complement {

    private final TObjectIntMap<StateRamsey> mStateIndices = new TObjectIntHashMap<>();
    private DAProfile mDA;
    
    public ComplementRamsey(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementRamsey";
    }
    
    @Override
    protected void computeInitialStates() {
        // first compute deterministic automaton
        this.mDA = new DAProfile(this.mOperand);
        UtilExplore.explore(this.mDA);
        // compute initial state
        StateDAProfile state = (StateDAProfile)this.mDA.getState(0);
        StateRamsey init = getOrAddState(state, 0);
        this.setInitial(init.getId());
    }
    
    protected StateRamsey getStateRamsey(int id) {
        return (StateRamsey) getState(id);
    }
    
    protected StateRamsey getOrAddState(StateDAProfile state, int label) {
        StateRamsey newState = new StateRamsey(this, 0, state, label);
        if(mStateIndices.containsKey(newState)) {
            return getStateRamsey(mStateIndices.get(newState));
        }else {
            int index = getStateSize();
            newState = new StateRamsey(this, index, state, label);
            if(mDA.isInitial(state.getId()) && label > 0) {
                this.setFinal(index);
            }
            return newState;
        }
    }

}
