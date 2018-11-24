package operation.complement.ordercsb;

import automata.Buchi;
import automata.IBuchi;
import automata.RandomBuchiGenerator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import main.Options;
import operation.complement.Complement;
import operation.complement.ncsb.NCSB;
import operation.complement.ncsb.StateNcsb;
import operation.complement.tuple.ComplementTuple;
import operation.explore.Explore;
import util.ISet;
import util.PowerSet;
import util.UtilISet;

/**
 * This construction is flawed
 * 
 *   apply NCSB algorithm to ordered runs
 * **/

public class ComplementOrderCSB extends Complement {

    private TObjectIntMap<StateOrderCSB> mStateIndices;
    
    public ComplementOrderCSB(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementOrderCSB";
    }
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        ISet inits = mOperand.getInitialStates().clone();
        ISet temp = inits.clone();
        temp.and(mOperand.getFinalStates());
        OrderedCSB runs = new OrderedCSB();
        for(int s : temp) {
            runs.addOrdState(s);
        }
        for(int s : inits) {
            if(temp.get(s)) continue;
            runs.addOrdState(s);
        }
        // powerset
        PowerSet ps = new PowerSet(temp);
        while(ps.hasNext()) {
            ISet B = ps.next();
            ISet C = inits.clone();
            C.andNot(B);
            OrderedCSB newRuns = runs.clone();
            newRuns.setC(C);
            newRuns.setB(C);
            StateOrderCSB state = getOrAddState(newRuns);
            this.setInitial(state.getId());
        }
    }
    
    protected StateOrderCSB getStateOrder(int id) {
        return (StateOrderCSB)getState(id);
    }

    protected StateOrderCSB getOrAddState(OrderedCSB osets) {
        StateOrderCSB state = new StateOrderCSB(this, 0, osets);
        if(mStateIndices.containsKey(state)) {
            return getStateOrder(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateOrderCSB newState = new StateOrderCSB(this, index, osets);
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
        
        ComplementOrderCSB complement = new ComplementOrderCSB(buchi);
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
        complement = new ComplementOrderCSB(buchi);
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
        complement = new ComplementOrderCSB(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        

    }
    

}
