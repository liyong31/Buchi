package operation.complement.slice;

import java.util.ArrayList;
import java.util.Map;

import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.complement.Complement;
import util.ISet;
import util.UtilISet;

public class ComplementSlice extends Complement {

    public ComplementSlice(IBuchi operand) {
        super(operand);
    }

    private TObjectIntMap<StateSlice> mStateIndices;
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        ISet inits = mOperand.getInitialStates();
        ISet pset1 = mOperand.getFinalStates();
        pset1.and(inits);
        ISet pset2 = inits;
        pset2.andNot(pset1);
        ArrayList<ISet> osets = new ArrayList<>();
        if(!pset1.isEmpty()) {
            osets.add(pset1);
        }
        if(!pset2.isEmpty()) {
            osets.add(pset2);
        }
        PreorderSets psets = new PreorderSets(osets);
        psets.setO(UtilISet.newISet());
        psets.setB(false);
//        Map<Integer, Boolean> 
        StateSlice stateSlice = getOrAddState(psets);
        this.setInitial(stateSlice.getId());
    }
    
    protected StateSlice getStateSlice(int id) {
        return (StateSlice)getState(id);
    }

    protected StateSlice getOrAddState(PreorderSets psets) {
        StateSlice state = new StateSlice(this, 0, psets);
        if(mStateIndices.containsKey(state)) {
            return getStateSlice(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateSlice newState = new StateSlice(this, index, psets);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(psets.isAccepting()) setFinal(index);
            return newState;
        }
    }

    @Override
    public String getName() {
        return "ComplementSlice";
    }

}
