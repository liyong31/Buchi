package operation.convert.dpa;

import automata.Buchi;
import automata.DPA;
import automata.IBuchi;
import automata.State;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.IUnaryOp;
import operation.explore.Explore;

// DPA to NBA
public class DPA2NBA extends Buchi implements IUnaryOp<DPA, IBuchi> {

    private final DPA mOperand;
    private final TObjectIntMap<State> mStateIndices;

    
    public DPA2NBA(DPA dpa) {
        super(dpa.getAlphabetSize());
        this.mOperand = dpa;
        this.mStateIndices = new TObjectIntHashMap<>();
        computeInitialStates();
    }
    
    private void computeInitialStates() {
        int init = mOperand.getInitialState();
        StateNBA state = getOrAddState(init, -1);
        this.setInitial(state.getId());
    }
    
    public StateNBA getStateNBA(int id) {
        return (StateNBA) getState(id);
    }
    
    protected StateNBA getOrAddState(int st, int label) {
        StateNBA state = new StateNBA(this, 0, st, label);
        if(mStateIndices.containsKey(state)) {
            return getStateNBA(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateNBA newState = new StateNBA(this, index, st, label);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(label == mOperand.getAcceptance().getColor(st)){
                setFinal(index);
            }
            return newState;
        }
    }

    @Override
    public String getName() {
        return "DPA2NBA";
    }

    @Override
    public DPA getOperand() {
        return mOperand;
    }

    @Override
    public IBuchi getResult() {
        return this;
    }
    
    public static void main(String[] args) {
        DPA dpa = new DPA(2);
        dpa.addState();
        dpa.addState();
        
        dpa.getState(0).addSuccessor(0, 0);
        dpa.getState(0).addSuccessor(1, 1);
        
        dpa.getState(1).addSuccessor(0, 0);
        dpa.getState(1).addSuccessor(1, 1);
        
        dpa.getAcceptance().setColor(0, 1);
        dpa.getAcceptance().setColor(1, 2);
        
        dpa.setInitial(0);
        
        DPA2NBA cc = new DPA2NBA(dpa);
        System.out.println(dpa.toDot());
        
        new Explore(cc);
        System.out.println(cc.toDot());
        
        DPA dpa2 = new DPA(2);
        dpa2.addState();
        dpa2.addState();
        dpa2.addState();
        dpa2.addState();
        
        dpa2.getState(0).addSuccessor(0, 1);
        dpa2.getState(0).addSuccessor(1, 2);
        
        dpa2.getState(1).addSuccessor(0, 1);
        dpa2.getState(1).addSuccessor(1, 3);
        
        dpa2.getState(2).addSuccessor(0, 3);
        dpa2.getState(2).addSuccessor(1, 2);
        
        dpa2.getState(3).addSuccessor(0, 3);
        dpa2.getState(3).addSuccessor(1, 3);
        
        dpa2.getAcceptance().setColor(0, 1);
        dpa2.getAcceptance().setColor(1, 2);
        dpa2.getAcceptance().setColor(2, 4);
        dpa2.getAcceptance().setColor(3, 3);
        
        dpa2.setInitial(0);
        
        cc = new DPA2NBA(dpa2);
        System.out.println(dpa2.toDot());
        
        new Explore(cc);
        System.out.println(cc.toDot());
    }
    
    

}
