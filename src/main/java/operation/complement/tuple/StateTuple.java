package operation.complement.tuple;

import java.util.ArrayList;

import automata.IBuchi;
import automata.State;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import util.ISet;
import util.UtilISet;

public class StateTuple extends State  {

    private final ComplementTuple mComplement;
    private final OrderedSets mOSets;
    
    public StateTuple(ComplementTuple complement, int id, OrderedSets osets) {
        super(id);
        this.mComplement = complement;
        this.mOSets = osets;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();
    
    /**
     * For normal transitions in the complement, see the paper
     *   "Complementing Buchi Automata with a Subset-tuple Construction" by Joel Allred and Ulrich Ultes-Nitsche
     *    In Logic and Automata: History and Perspective
     * They defined the run trees and divide the states at the same level of the trees as slice
     * 
     * we label every component with a color in the accepting component
     *        0 as runs has not yet visited final states
     *        1 as runs has visited final states and wait to become 2 unless no 2 component in the predecessor 
     *    and 2 as runs has visited final states
     *    
     *    There are two parts in the complement (i) the initial part and (ii) the accepting part.
     *    
     *    1. [Jump to accepting part]
     *    Once the runs nondeterministically leave the initial part and go to the accepting part,
     *    we will initially think all components have not visited final states.
     *    
     *    2. [Final states]
     *    The final states will be those states which do not have 2-colored components.
     *    
     *    3. [Transition in the accepting part]
     *      Assume that current state is <(Q1, c1), ..., (Qn, cn)> 
     *       (i) current state is final state (no 2-colored component)
     *          then c'_{2i+1} = 0 if  ci = 0; [runs still have not visited final states]
     *          otherwise c'_{2i+1} = 2 and c'_{2i} = 2. [runs have visited final states]
     *       (ii) current state is not final state (contains 2-colored component)
     *          then (1) c'_{2i+1} = 0 if  ci = 0; [runs still have not visited final states]
     *               (2) c'_{2i+1} = c'_{2i} =2 if ci = 2; [runs have visited final states]
     *               (3) c'_{2i+1} = c'_{2i} =1 if ci = 2; [runs have visited final states but have to wait to be 2-colored]
     * **/ 
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        ISet succs = UtilISet.newISet();
        ArrayList<ISet> ordSets = mOSets.getOrderedSets(); 
        IBuchi operand = mComplement.getOperand();
        ISet leftSuccs = UtilISet.newISet();
        ArrayList<ISet> nextOrdSets = new ArrayList<>();
        TIntIntMap predMap = new TIntIntHashMap();
        int index = 0;
        for(int i = 0; i < ordSets.size(); i ++) {
            ISet Si = ordSets.get(i);
            ISet finalSuccs = UtilISet.newISet();
            ISet nonFinalSuccs = UtilISet.newISet();
            for(final int p : Si) {
                for(final int q : operand.getState(p).getSuccessors(letter)) {
                    // ignore successors already have been visited
                    if(leftSuccs.get(q)) continue;
                    if(operand.isFinal(q)) {
                        finalSuccs.set(q);
                    }else {
                        nonFinalSuccs.set(q);
                    }
                    leftSuccs.set(q);
                }
            }
            if(!finalSuccs.isEmpty()) {
                nextOrdSets.add(finalSuccs);
                predMap.put(index, i);
                index ++;
            }
            if(!nonFinalSuccs.isEmpty()) {
                nextOrdSets.add(nonFinalSuccs);
                predMap.put(index, i);
                index ++;
            }
        }
        StateTuple nextState;
        //1. non-colored states compute successor
        if(!mOSets.isColored()) {
            OrderedSets osets = new OrderedSets(false);
            for(int i = 0; i < nextOrdSets.size(); i ++) {
                osets.addSet(nextOrdSets.get(i), Color.NONE);
            }
            nextState = mComplement.getOrAddState(osets);
            super.addSuccessor(letter, nextState.getId());
            succs.set(nextState.getId());
            System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + osets + " : " + letter);
        }
        //2. every state compute colors
        {
            OrderedSets osets = new OrderedSets(true);
            ISet fset = operand.getFinalStates();
            for(int i = 0; i < nextOrdSets.size(); i ++) {
                osets.addSet(nextOrdSets.get(i)
                        , decideColor(nextOrdSets.get(i), predMap.get(i), fset));
            }
            nextState = mComplement.getOrAddState(osets);
            super.addSuccessor(letter, nextState.getId());
            succs.set(nextState.getId());
            System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + osets + " : " + letter);
        }
        
        return succs;
    }
    
    private Color decideColor(ISet sjp, int jpred, ISet fset) {
        boolean hasTwoColor = mOSets.hasTwoColor();
        
        if(! hasTwoColor) {
            if(mOSets.getColor(jpred) == Color.ZERO
            && !sjp.overlap(fset)) {
                return Color.ZERO; 
            }else {
                return Color.TWO;
            }
        }else {
            if (mOSets.getColor(jpred) == Color.ZERO
            && !sjp.overlap(fset)) {
                return Color.ZERO;
            } else if(mOSets.getColor(jpred) == Color.TWO){
                return Color.TWO;
            } else {
                return Color.ONE;
            }
        }
    }
    
    @Override
    public int hashCode() {
        return mOSets.hashCode();
    }
    
    @Override
    public String toString() {
        return mOSets.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null) return false;
        if(obj instanceof StateTuple) {
            StateTuple other = (StateTuple)obj;
            return this.mOSets.equals(other.mOSets);
        }
        return false;
    }

}
