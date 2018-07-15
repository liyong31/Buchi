package operation.complement.ramsey;

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.complement.Complement;
import operation.complement.tuple.ComplementTuple;
import operation.explore.Explore;
import operation.explore.UtilExplore;

/**
 * "The complementation problem for Buchi automata with applications to temporal logic"
 * by A. Prasad Sistla, Moshe Y. Vardi, and Pierre Wolper in Theoretical Computer Science.
 *  
 * **/
public class ComplementRamsey extends Complement {

    private TObjectIntMap<StateRamsey> mStateIndices;
    private DAProfile mDA;
    
    public ComplementRamsey(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementRamsey";
    }
    
    protected DAProfile getDA() {
        return mDA;
    }
    
    @Override
    protected void computeInitialStates() {
        // first compute deterministic automaton
        this.mStateIndices = new TObjectIntHashMap<>();
        this.mDA = new DAProfile(this.mOperand);
        UtilExplore.explore(this.mDA);
        // compute initial state
        StateDAProfile state = (StateDAProfile)this.mDA.getState(0);
        StateRamsey init = getOrAddState(state, 0);
        this.setInitial(init.getId());
    }
    
    protected StateRamsey getStateRamsey(int id) {
        return (StateRamsey) getState(id);
    }
    
    protected StateRamsey getOrAddState(StateDAProfile state, int label) {
        StateRamsey newState = new StateRamsey(this, 0, state, label);
        if(mStateIndices.containsKey(newState)) {
            return getStateRamsey(mStateIndices.get(newState));
        }else {
            int index = getStateSize();
            newState = new StateRamsey(this, index, state, label);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(mDA.isInitial(state.getId()) && label > 0) {
                this.setFinal(id);
            }
            return newState;
        }
    }
    
    public static void main(String[] args) {
        IBuchi buchi = new Buchi(2);
        buchi.addState();
        buchi.addState();
        
        buchi.getState(0).addSuccessor(0, 0);
        buchi.getState(0).addSuccessor(1, 0);
        buchi.getState(0).addSuccessor(1, 1);
        
        buchi.getState(1).addSuccessor(0, 0);
        buchi.getState(1).addSuccessor(0, 1);
        
        buchi.setFinal(1);
        buchi.setInitial(0);
        
        ComplementRamsey cr = new ComplementRamsey(buchi);
        new Explore(cr);
        System.out.println(cr.toDot());
        System.out.println(cr.toBA());
        
        ComplementTuple ct = new ComplementTuple(buchi);
        new Explore(ct);
        System.out.println(ct.toDot());
        System.out.println(ct.toBA());
        
    }

}
