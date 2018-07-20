package operation.complement.nsbc;

import automata.Buchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.explore.Explore;

// still not correct
public class QuotientNsbc extends Buchi {
    
    protected final ComplementNsbc mComplement;
    private final TObjectIntMap<StateNsbcSimilar> mStateIndices;
    
    public QuotientNsbc(ComplementNsbc complement) {
        super(complement.getAlphabetSize());
        this.mComplement = complement;
        this.mStateIndices = new TObjectIntHashMap<>();
        initializeQuotient();
    }
    
    protected void initializeQuotient() {
        // compute initial states
        for(final int init : mComplement.getInitialStates()) {
            StateNsbcSimilar state = getOrAddState(mComplement.getStateNsbc(init));
            this.setInitial(state.getId());
        }
        new Explore(mComplement);
        // now we compute the quotient state space
        for(int i = 0; i < mComplement.getStateSize(); i ++) {
            getOrAddState(mComplement.getStateNsbc(i));
        }
    }

    protected StateNsbcSimilar getStateNsbcSimular(int id) {
        return (StateNsbcSimilar)getState(id);
    }

    protected StateNsbcSimilar getOrAddState(StateNsbc nsbc) {
        StateNsbcSimilar state = new StateNsbcSimilar(this, 0, nsbc);
        if(mStateIndices.containsKey(state)) {
            int id = mStateIndices.get(state);
            StateNsbcSimilar representor = getStateNsbcSimular(id);
            // first have to be colored
            if(!representor.contains(nsbc)) {
                representor.addEqualStates(nsbc);
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
