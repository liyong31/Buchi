package operation.complement.slice;

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.complement.Complement;
import operation.complement.tuple.Color;
import operation.complement.tuple.OrderedSets;
import operation.explore.Explore;
import operation.removal.Remove;
import util.ISet;


/**
 * Complementation, Disambiguation, and Determinization of Buchi Automata Unified
 * by Detlef Kaehler and Thomas Wilke
 * In ICALP 2008
 * */
//TODO
public class ComplementSliceKW extends Complement {

    public ComplementSliceKW(IBuchi operand) {
        super(operand);
    }

    private TObjectIntMap<StateSliceKW> mStateIndices;
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        ISet inits = mOperand.getInitialStates().clone();
        ISet pset1 = mOperand.getFinalStates().clone();
        pset1.and(inits);
        ISet pset2 = inits;
        pset2.andNot(pset1);
        Slice osets = new Slice(true);
        if(!pset1.isEmpty()) {
            osets.addSet(pset1, Color.ONE);
        }
        if(!pset2.isEmpty()) {
            osets.addSet(pset2, Color.ONE);
        }
        
        StateSliceKW stateSlice = getOrAddState(osets);
        this.setInitial(stateSlice.getId());
    }
    
    protected StateSliceKW getStateSlice(int id) {
        return (StateSliceKW)getState(id);
    }

    protected StateSliceKW getOrAddState(Slice osets) {
        StateSliceKW state = new StateSliceKW(this, 0, osets);
        if(mStateIndices.containsKey(state)) {
            return getStateSlice(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateSliceKW newState = new StateSliceKW(this, index, osets);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(osets.isFinal()) setFinal(index);
            return newState;
        }
    }

    @Override
    public String getName() {
        return "ComplementSliceKW";
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
        
        ComplementSliceKW complement = new ComplementSliceKW(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        System.out.println(complement.toBA());
        
        IBuchi result = (new Remove(complement)).getResult();
        
        System.out.println(result.toDot());
        
        System.out.println(result.toBA());
        

    }

}
