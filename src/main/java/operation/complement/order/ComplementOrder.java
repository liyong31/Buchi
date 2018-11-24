package operation.complement.order;

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
 * should add another set to record the sets need to be cut later  
 * **/

public class ComplementOrder extends Complement {

    private TObjectIntMap<StateOrder> mStateIndices;
    
    public ComplementOrder(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementOrder";
    }
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        ISet inits = mOperand.getInitialStates().clone();
        OrderedRuns runs = new OrderedRuns(inits);        
        StateOrder stateSlice = getOrAddState(runs);
        this.setInitial(stateSlice.getId());
    }
    
    protected StateOrder getStateOrder(int id) {
        return (StateOrder)getState(id);
    }

    protected StateOrder getOrAddState(OrderedRuns osets) {
        StateOrder state = new StateOrder(this, 0, osets);
        if(mStateIndices.containsKey(state)) {
            return getStateOrder(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateOrder newState = new StateOrder(this, index, osets);
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
        
        ComplementOrder complement = new ComplementOrder(buchi);
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
        
        buchi.getState(0).addSuccessor(1, 0);
        buchi.getState(0).addSuccessor(1, 1);
        
        buchi.getState(1).addSuccessor(1, 0);
        
        buchi.setFinal(1);
        buchi.setInitial(0);
        
        System.out.println(buchi.toDot());
        complement = new ComplementOrder(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        buchi = new Buchi(2);
        
        buchi.addState();
        buchi.addState();
        buchi.addState();
        
        buchi.getState(0).addSuccessor(1, 1);
        buchi.getState(1).addSuccessor(1, 1);
        buchi.getState(1).addSuccessor(1, 0);
        
        buchi.setFinal(0);
        buchi.setInitial(0);
        
        System.out.println(buchi.toDot());
        complement = new ComplementOrder(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        

    }
    

}
