package operation.complement.nsbc;

import automata.Buchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

// still not correct
public class SimulatorNsbc extends Buchi {
    
    protected final ComplementNsbc mComplement;
    private final TObjectIntMap<StateNsbcSimilar> mStateIndices;
    
    public SimulatorNsbc(ComplementNsbc complement) {
        super(complement.getAlphabetSize());
        this.mComplement = complement;
        this.mStateIndices = new TObjectIntHashMap<>();
        computeInitialStates();
    }
    
    protected void computeInitialStates() {
        // compute initial states
        for(final int init : mComplement.getInitialStates()) {
            StateNsbcSimilar state = getOrAddState(mComplement.getStateNsbc(init));
            this.setInitial(state.getId());
        }
    }

    protected StateNsbcSimilar getStateNsbcSimular(int id) {
        return (StateNsbcSimilar)getState(id);
    }

    protected StateNsbcSimilar getOrAddState(StateNsbc nsbc) {
        StateNsbcSimilar state = new StateNsbcSimilar(this, 0, nsbc);
        if(mStateIndices.containsKey(state)) {
            int id = mStateIndices.get(state);
            if(nsbc.mNsbc.isFinal()) {
                setFinal(id);
            }
            return getStateNsbcSimular(id);
        }else {
            int index = getStateSize();
            StateNsbcSimilar newState = new StateNsbcSimilar(this, index, nsbc);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(mComplement.isFinal(nsbc.getId())) setFinal(index);
            return newState;
        }
    }
    
    public String getName() {
        return "SimulationNsbc";
    }
    
    

}
