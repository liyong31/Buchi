package operation.complement.tuple;

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import main.Options;
import operation.complement.Complement;
import operation.explore.Explore;
import util.ISet;

// tuple-based complementation
/**
 * Complementing Buchi Automata with a Subset-tuple Construction
 *    Joel Allred and Ulrich Ultes-Nitsche
 *    
 *    COMMENTS: This paper seems to be almost the same algorithm proposed by Ming-Hsien Tsai et al
 *    in their LMCS paper "STATE OF BÜCHI COMPLEMENTATION". The difference I am aware of is that Allred and Ultes-Nitsche
 *    provide a different complexity analysis. 
 *    
 * */

public class ComplementTuple extends Complement {

    public ComplementTuple(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementTuple";
    }
    
    private TObjectIntMap<StateTuple> mStateIndices;
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        ISet inits = mOperand.getInitialStates().clone();
        ISet pset1 = mOperand.getFinalStates().clone();
        pset1.and(inits);
        ISet pset2 = inits;
        pset2.andNot(pset1);
        OrderedSets osets = new OrderedSets(false);
        if(!pset1.isEmpty()) {
            osets.addSet(pset1, Color.NONE);
        }
        if(!pset2.isEmpty()) {
            osets.addSet(pset2, Color.NONE);
        }
        StateTuple stateSlice = getOrAddState(osets);
        this.setInitial(stateSlice.getId());
    }
    
    protected StateTuple getStateTuple(int id) {
        return (StateTuple)getState(id);
    }

    protected StateTuple getOrAddState(OrderedSets osets) {
        StateTuple state = new StateTuple(this, 0, osets);
        if(mStateIndices.containsKey(state)) {
            return getStateTuple(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateTuple newState = new StateTuple(this, index, osets);
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
        
        ComplementTuple complement = new ComplementTuple(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        System.out.println(complement.toBA());
        
        Options.mMergeAdjacentSets = true;
        Options.mMergeAdjacentColoredSets = true;
        complement = new ComplementTuple(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        System.out.println(complement.toBA());

    }

    
    

}
