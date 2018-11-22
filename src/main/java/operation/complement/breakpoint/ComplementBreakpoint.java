package operation.complement.breakpoint;

import automata.Buchi;
import automata.IBuchi;
import automata.RandomBuchiGenerator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import main.Options;
import operation.complement.Complement;
import operation.complement.tuple.ComplementTuple;
import operation.explore.Explore;
import util.ISet;

/**
 * This construction is flawed, and only gets an overapproximation of the complement language
 * **/

public class ComplementBreakpoint extends Complement {

    private TObjectIntMap<StateBreakpoint> mStateIndices;
    
    public ComplementBreakpoint(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementBreakpoint";
    }
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        ISet inits = mOperand.getInitialStates().clone();
        ISet pset1 = mOperand.getFinalStates().clone();
        pset1.and(inits);
        ISet pset2 = inits;
        pset2.andNot(pset1);
        OrderedSetsBreakpoint osets = new OrderedSetsBreakpoint(false);
        if(!pset1.isEmpty()) {
            osets.addSet(pset1);
        }
        if(!pset2.isEmpty()) {
            osets.addSet(pset2);
        }
        StateBreakpoint stateSlice = getOrAddState(osets);
        this.setInitial(stateSlice.getId());
    }
    
    protected StateBreakpoint getStateBreakpoint(int id) {
        return (StateBreakpoint)getState(id);
    }

    protected StateBreakpoint getOrAddState(OrderedSetsBreakpoint osets) {
        StateBreakpoint state = new StateBreakpoint(this, 0, osets);
        if(mStateIndices.containsKey(state)) {
            return getStateBreakpoint(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateBreakpoint newState = new StateBreakpoint(this, index, osets);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(osets.isFinal()) setFinal(index);
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
        
        ComplementBreakpoint complement = new ComplementBreakpoint(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        System.out.println(complement.toBA());
        
//        Options.mMergeAdjacentSets = true;
//        Options.mMergeAdjacentColoredSets = true;
//        complement = new ComplementTuple(buchi);
//        new Explore(complement);
//        System.out.println(complement.toDot());
//        
//        System.out.println(complement.toBA());
        buchi = new Buchi(2);
        
        buchi.addState();
        buchi.addState();
        buchi.addState();
        
        buchi.getState(0).addSuccessor(0, 1);
        buchi.getState(0).addSuccessor(1, 2);
        
        buchi.getState(1).addSuccessor(0, 2);
        
        buchi.getState(2).addSuccessor(1, 0);
        buchi.getState(2).addSuccessor(1, 1);
        
        buchi.setFinal(1);
        buchi.setInitial(0);
        
        System.out.println(buchi.toDot());
        complement = new ComplementBreakpoint(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        

    }
    

}
