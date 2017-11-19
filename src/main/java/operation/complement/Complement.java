package operation.complement;

import automata.Buchi;
import automata.IBuchi;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.IUnaryOp;
import util.ISet;
import util.UtilISet;

/**
 * Only valid for Semi-deterministic Buchi automata
 * */
public class Complement extends Buchi implements IUnaryOp<IBuchi, IBuchi> {

    private final IBuchi mOperand;
    private final TObjectIntMap<StateNCSB> mStateIndices = new TObjectIntHashMap<>();
    
    public Complement(IBuchi buchi) {
        super(buchi.getAlphabetSize());
        this.mOperand = buchi;
        computeInitialStates();
    }

    private void computeInitialStates() {
        ISet C = mOperand.getInitialStates().clone();
        C.and(mOperand.getFinalStates()); // goto C
        ISet N = mOperand.getInitialStates().clone();
        N.andNot(C);
        NCSB ncsb = new NCSB(N, C, UtilISet.newISet(), C);
        StateNCSB state = new StateNCSB(this, 0, ncsb);
        if(C.isEmpty()) this.setFinal(0);
        this.setInitial(0);
        int id = this.addState(state);
        mStateIndices.put(state, id);
    }
    

    protected StateNCSB getOrAddState(NCSB ncsb) {
        
        StateNCSB state = new StateNCSB(this, 0, ncsb);
        
        if(mStateIndices.containsKey(state)) {
            return getStateNCSB(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateNCSB newState = new StateNCSB(this, index, ncsb);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(ncsb.getBSet().isEmpty()) setFinal(index);
            return newState;
        }
    }

    @Override
    public IBuchi getOperand() {
        return mOperand;
    }


    @Override
    public IBuchi getResult() {
        return this;
    }

    @Override
    public String getName() {
        return "Complement";
    }
    
    public StateNCSB getStateNCSB(int id) {
        return (StateNCSB) getState(id);
    }
    
    public void testLemma() {
        for(int i = 0; i < getStateSize(); i ++) {
            StateNCSB s = (StateNCSB) getState(i);
            ISet N = s.getNCSB().copyNSet();
            ISet C = s.getNCSB().copyCSet();
            ISet B = s.getNCSB().copyBSet();
            C.andNot(B);
            C.andNot(getFinalStates());
            for(int n : C) {
                ISet Cp = s.getNCSB().copyCSet();
                Cp.clear(n);
                ISet S = s.getNCSB().copySSet();
                S.set(n);
                NCSB ncsb = new NCSB(N, Cp, S, B);
                StateNCSB nn = getOrAddState(ncsb);
                System.out.println(s.getNCSB() + " : " + ncsb);
                assert nn != null : "Not reachable " + s.getNCSB() + " -> " + ncsb;
            }

        }
    }

}
