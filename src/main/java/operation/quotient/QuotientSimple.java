package operation.quotient;

import automata.Buchi;
import automata.IBuchi;
import automata.IState;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.explore.Explore;
import operation.explore.ExploreBuchi;

// only merge states (p, q) which have the same incoming and outgoing transitions
// not correct
public class QuotientSimple extends Buchi {
    
    protected final IBuchi mOperand;
    private final TObjectIntMap<IState> mStateIndices;
    protected ExploreBuchi mExplore;
    
    public QuotientSimple(IBuchi operand) {
        super(operand.getAlphabetSize());
        this.mOperand = operand;
        this.mStateIndices = new TObjectIntHashMap<>();
        initializeQuotient();
    }
    
    protected void initializeQuotient() {
        // compute initial states
        new Explore(mOperand);
//        mOperand.makeComplete();
        mExplore = new ExploreBuchi(mOperand);
        mExplore.explore();
        for(int init : mOperand.getInitialStates()) {
            StateSimple state = getOrAddState(mOperand.getState(init));
            this.setInitial(state.getId());
        }
        // now we compute the quotient state space
        for(int i = 0; i < mOperand.getStateSize(); i ++) {
            getOrAddState(mOperand.getState(i));
        }
    }

    protected StateSimple getStateSimple(int id) {
        return (StateSimple)getState(id);
    }

    protected StateSimple getOrAddState(IState is) {
        StateSimple state = new StateSimple(this, 0, is);
        if(mStateIndices.containsKey(state)) {
            int id = mStateIndices.get(state);
            StateSimple representor = getStateSimple(id);
            if(!representor.contains(is)) {
                representor.addEqualStates(is);
                if(mOperand.isFinal(is.getId())) {
                    this.setFinal(id);
                }
                System.out.println("N: " + representor + " : " + is);
            }
            return getStateSimple(id);
        }else {
            int index = getStateSize();
            StateSimple newState = new StateSimple(this, index, is);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(mOperand.isFinal(state.getId())) setFinal(index);
            return newState;
        }
    }
    
    public String getName() {
        return "SimulationNsbc";
    }

}
