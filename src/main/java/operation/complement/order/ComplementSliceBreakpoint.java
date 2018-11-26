package operation.complement.order;

import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.complement.Complement;
import operation.complement.tuple.Color;
import util.ISet;

public class ComplementSliceBreakpoint extends Complement {

    private TObjectIntMap<StateSliceBreakpoint> mStateIndices;
    
    public ComplementSliceBreakpoint(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementSliceBreakpoint";
    }
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        ISet inits = mOperand.getInitialStates().clone();
        ISet temp = inits.clone();
        temp.and(mOperand.getFinalStates());
        SliceBreakpoint runs = new SliceBreakpoint(false);
        // first final states
        if(!temp.isEmpty()) {
            runs.addSet(temp);
        }
        inits.andNot(temp);
        // then nonfinal states
        if(!inits.isEmpty()) {
            runs.addSet(inits);
        }
        StateSliceBreakpoint stateSlice = getOrAddState(runs);
        this.setInitial(stateSlice.getId());
    }
    
    protected StateSliceBreakpoint getStateTupleOrder(int id) {
        return (StateSliceBreakpoint)getState(id);
    }

    protected StateSliceBreakpoint getOrAddState(SliceBreakpoint osets) {
        StateSliceBreakpoint state = new StateSliceBreakpoint(this, 0, osets);
        if(mStateIndices.containsKey(state)) {
            return getStateTupleOrder(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateSliceBreakpoint newState = new StateSliceBreakpoint(this, index, osets);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(osets.isFinal()) setFinal(index);
            return newState;
        }
    }

}
