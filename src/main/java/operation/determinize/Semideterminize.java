package operation.determinize;

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.IUnaryOp;
import operation.explore.Explore;
import util.ISet;

/**
 * Semideterminize a given Buchi automaton
 *  Costas Courcoubetis and Mihalis Yannakakis
 *   "Verifying Temporal Properties of Finite-State Probabilistic Programs"
 *  in FOCS 
 * */

public class Semideterminize extends Buchi implements IUnaryOp<IBuchi, IBuchi> {

    private final IBuchi mOperand;
    private final TObjectIntMap<StateSemiDet> mStateIndices = new TObjectIntHashMap<>();
    private final ISet mOpAcc;
    
    public Semideterminize(IBuchi operand) {
        super(operand.getAlphabetSize());
        this.mOperand = operand;
        this.mOpAcc = operand.getFinalStates();
        computeInitialStates();
    }

    private void computeInitialStates() {
        ISet initials = mOperand.getInitialStates();
        for(final int init : initials) {
            StateSemiDet sd = getOrAddState(null, null, init);
            this.setInitial(sd.getId());
        }
    }
    
    
    protected StateSemiDet getStateSemiDet(int id) {
        return (StateSemiDet)getState(id);
    }
    
    protected StateSemiDet getOrAddState(ISet P, ISet Q, int preId) {
        StateSemiDet state = new StateSemiDet(this, 0, preId, P, Q);
        if(mStateIndices.containsKey(state)) {
            return getStateSemiDet(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateSemiDet newState = new StateSemiDet(this, index, preId, P, Q);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(P != null && Q != null) {
                if(P.overlap(mOpAcc) &&  P.equals(Q)) setFinal(index);
            }
            System.out.println("id: " + index + " state: " + newState + " isF: " + this.isFinal(index));
            return newState;
        }        
    }

    @Override
    public String getName() {
        return "Semideterminize";
    }

    @Override
    public IBuchi getOperand() {
        return mOperand;
    }

    @Override
    public IBuchi getResult() {
        return this;
    }
    
    
    public static void main(String[] args) {
        IBuchi nba = new Buchi(2);
        nba.addState();
        nba.addState();
        nba.getState(0).addSuccessor(0, 0);
        nba.getState(0).addSuccessor(0, 1);
        nba.getState(1).addSuccessor(1, 0);
        nba.getState(1).addSuccessor(1, 1);
        nba.setInitial(0);
        nba.setFinal(1);
        
        // 
        Semideterminize det = new Semideterminize(nba);
        Explore explore = new Explore(det);
        System.out.println("A: \n" + nba.toDot());
        System.out.println("A: \n" + nba.toBA());
        System.out.println("B: \n" + det.toBA());
        System.out.println("B: \n" + det.toDot());
    }

}
