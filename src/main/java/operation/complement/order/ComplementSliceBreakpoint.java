package operation.complement.order;

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import main.Options;
import operation.complement.Complement;
import operation.complement.tuple.ComplementTuple;
import operation.explore.Explore;
import util.ISet;

/**
 * Slice-based complementation algorithm optimized by lazy breakpoint construction
 * */

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

    protected StateSliceBreakpoint getOrAddState(SliceBreakpoint sliceBreakpoint) {
        StateSliceBreakpoint state = new StateSliceBreakpoint(this, 0, sliceBreakpoint);
        if(mStateIndices.containsKey(state)) {
            return getStateTupleOrder(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateSliceBreakpoint newState = new StateSliceBreakpoint(this, index, sliceBreakpoint);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(sliceBreakpoint.isFinal()) setFinal(index);
            return newState;
        }
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
        
        ComplementSliceBreakpoint complement = new ComplementSliceBreakpoint(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        System.out.println(complement.toBA());
        
        Options.mMergeAdjacentSets = true;
        Options.mMergeAdjacentColoredSets = true;
        complement = new ComplementSliceBreakpoint(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        System.out.println(complement.toBA());
        
        buchi = new Buchi(2);
        
        buchi.addState();
        buchi.addState();
        
        buchi.getState(0).addSuccessor(1, 0);
        buchi.getState(0).addSuccessor(1, 1);
        
        buchi.getState(1).addSuccessor(1, 0);
        
        buchi.setFinal(1);
        buchi.setInitial(0);
        
        System.out.println(buchi.toDot());
        complement = new ComplementSliceBreakpoint(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());

    }

}
