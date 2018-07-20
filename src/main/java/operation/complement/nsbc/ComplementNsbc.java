package operation.complement.nsbc;

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import main.Options;
import operation.complement.Complement;
import operation.complement.ncsb.ComplementNcsb;
import operation.explore.Explore;
import operation.quotient.QuotientSimple;
import operation.removal.Remove;
import util.ISet;
import util.UtilISet;

public class ComplementNsbc extends Complement {

    protected final ISet mDetStates;
    protected final ISet mNondetStates;
    
    public ComplementNsbc(IBuchi operand) {
        super(operand);
        this.mDetStates = operand.getDetStatesAfterFinals();
        this.mNondetStates = UtilISet.newISet();
        for(int s = 0; s < operand.getStateSize(); s ++) {
            if(!this.mDetStates.get(s)) {
                this.mNondetStates.set(s);
            }
        }
    }
    
    public void setDetStates(int state) {
        mDetStates.set(state);
        mNondetStates.andNot(mDetStates);
    }
    
    private TObjectIntMap<StateNsbc> mStateIndices;
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        ISet inits = mOperand.getInitialStates().clone();
        NSBC nsbc = new NSBC(inits);
        StateNsbc stateSlice = getOrAddState(nsbc);
        this.setInitial(stateSlice.getId());
    }
    
    protected StateNsbc getStateNsbc(int id) {
        return (StateNsbc)getState(id);
    }
    
    @Override
    public IBuchi getResult() {
        return this;
    }

    protected StateNsbc getOrAddState(NSBC nsbc) {
        StateNsbc state = new StateNsbc(this, 0, nsbc);
        if(mStateIndices.containsKey(state)) {
            return getStateNsbc(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateNsbc newState = new StateNsbc(this, index, nsbc);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(nsbc.isFinal()) setFinal(index);
            return newState;
        }
    }

    @Override
    public String getName() {
        return "ComplementNsbc";
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
        System.out.println(buchi.getDetStatesAfterFinals());
        Options.mEnhancedSliceGuess = true;
        ComplementNsbc complement = new ComplementNsbc(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        System.out.println(complement.toBA());
        
        IBuchi result = (new Remove(complement)).getResult();
        
        System.out.println(result.toDot());
        
        System.out.println(result.toBA());
        
        
        buchi = new Buchi(2);
        
        buchi.addState();
        buchi.addState();
        buchi.addState();
        
        buchi.getState(0).addSuccessor(0, 1);
        buchi.getState(0).addSuccessor(1, 2);
        buchi.getState(1).addSuccessor(0, 1);
        buchi.getState(1).addSuccessor(1, 1);
        
        buchi.getState(2).addSuccessor(0, 2);
        buchi.getState(2).addSuccessor(1, 2);
        
        buchi.setFinal(1);
        buchi.setInitial(0);
        
        complement = new ComplementNsbc(buchi);
        complement.setDetStates(2);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        buchi = new Buchi(2);
        
        buchi.addState();
        buchi.addState();
        buchi.addState();
        buchi.addState();
        buchi.addState();
        buchi.addState();
        
        int q0 = 0;
        int q0a = 1;
        int q0b = 2;
        
        int q1 = 3;
        int q1a = 4;
        int q1b = 5;
        
        buchi.getState(q0).addSuccessor(0, q0a);
        buchi.getState(q0).addSuccessor(1, q0b);
        buchi.getState(q0).addSuccessor(1, q1);
        
        buchi.getState(q1).addSuccessor(0, q1a);
        buchi.getState(q1).addSuccessor(1, q1b);
        buchi.getState(q1).addSuccessor(0, q0);
        
        buchi.getState(q0a).addSuccessor(0, q0a);
        buchi.getState(q0a).addSuccessor(1, q0a);
        
        buchi.getState(q0b).addSuccessor(0, q0b);
        buchi.getState(q0b).addSuccessor(1, q0b);
        
        buchi.getState(q1a).addSuccessor(0, q1a);
        buchi.getState(q1a).addSuccessor(1, q1a);
        
        buchi.getState(q1b).addSuccessor(0, q1b);
        buchi.getState(q1b).addSuccessor(1, q1b);
        
        
        
        buchi.setInitial(q0);
        buchi.setFinal(q0a);
        buchi.setFinal(q1b);
        
        System.out.println(buchi.toDot());
        
        complement = new ComplementNsbc(buchi);
        complement.setDetStates(q0b);
        complement.setDetStates(q1a);
        
        new Explore(complement);
        System.out.println(complement.toDot());
        
        ComplementNcsb complementNcsb = new ComplementNcsb(buchi);
        new Explore(complementNcsb);
        System.out.println(complementNcsb.toDot());

    }

}
