package operation.complement.slice;

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.complement.Complement;
import operation.complement.tuple.Color;
import operation.complement.tuple.OrderedSets;
import operation.explore.Explore;
import operation.removal.Remove;
import util.ISet;


/**
 * Automata: From Logics to Algorithms
 * by Moshe Y. Vardi and Thomas Wilke
 * In Logic and Automata: History and Perspective
 * */
public class ComplementSliceVW extends Complement {

    public ComplementSliceVW(IBuchi operand) {
        super(operand);
    }

    private TObjectIntMap<StateSliceVW> mStateIndices;
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        ISet inits = mOperand.getInitialStates().clone();
        ISet pset1 = mOperand.getFinalStates().clone();
        pset1.and(inits);
        ISet pset2 = inits;
        pset2.andNot(pset1);
        OrderedSets osets = new OrderedSets(false);
        if(!pset1.isEmpty()) {
            osets.addSet(pset1, Color.NONE);
        }
        if(!pset2.isEmpty()) {
            osets.addSet(pset2, Color.NONE);
        }
        
        StateSliceVW stateSlice = getOrAddState(osets);
        this.setInitial(stateSlice.getId());
    }
    
    protected StateSliceVW getStateSlice(int id) {
        return (StateSliceVW)getState(id);
    }

    protected StateSliceVW getOrAddState(OrderedSets osets) {
        StateSliceVW state = new StateSliceVW(this, 0, osets);
        if(mStateIndices.containsKey(state)) {
            return getStateSlice(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateSliceVW newState = new StateSliceVW(this, index, osets);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(osets.isFinal()) setFinal(index);
            return newState;
        }
    }

    @Override
    public String getName() {
        return "ComplementSliceVW";
    }
    
    public static void main(String[] args) {
        IBuchi buchi = new Buchi(2);
        
        buchi.addState();
        buchi.addState();
        buchi.addState();
        
        buchi.getState(0).addSuccessor(0, 0);
        buchi.getState(0).addSuccessor(1, 0);
        buchi.getState(0).addSuccessor(0, 1);
        buchi.getState(0).addSuccessor(1, 1);
        
        buchi.getState(1).addSuccessor(0, 2);
        buchi.getState(1).addSuccessor(1, 1);
        
        buchi.getState(2).addSuccessor(0, 2);
        buchi.getState(2).addSuccessor(1, 2);
        
        buchi.setFinal(1);
        buchi.setInitial(0);
        
        System.out.println(buchi.toDot());
        
        ComplementSliceVW complement = new ComplementSliceVW(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        System.out.println(complement.toBA());
        
        IBuchi result = (new Remove(complement)).getResult();
        
        System.out.println(result.toDot());
        
        System.out.println(result.toBA());
        

    }

}
