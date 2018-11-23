package operation.complement.cutpoint;

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

public class ComplementCutpoint extends Complement {

    private TObjectIntMap<StateCutpoint> mStateIndices;
    
    public ComplementCutpoint(IBuchi operand) {
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
        OrderedSetsCutpoint osets = new OrderedSetsCutpoint(false);
        if(!pset1.isEmpty()) {
            osets.addSet(pset1);
        }
        if(!pset2.isEmpty()) {
            osets.addSet(pset2);
        }
        StateCutpoint stateSlice = getOrAddState(osets);
        this.setInitial(stateSlice.getId());
    }
    
    protected StateCutpoint getStateBreakpoint(int id) {
        return (StateCutpoint)getState(id);
    }

    protected StateCutpoint getOrAddState(OrderedSetsCutpoint osets) {
        StateCutpoint state = new StateCutpoint(this, 0, osets);
        if(mStateIndices.containsKey(state)) {
            return getStateBreakpoint(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateCutpoint newState = new StateCutpoint(this, index, osets);
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
        
        ComplementCutpoint complement = new ComplementCutpoint(buchi);
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
        complement = new ComplementCutpoint(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        

    }
    

}
