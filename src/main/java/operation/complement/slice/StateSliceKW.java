package operation.complement.slice;

import java.util.ArrayList;

import automata.IBuchi;
import automata.State;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import operation.complement.tuple.Color;
import operation.complement.tuple.OrderedSets;
import operation.complement.tuple.StateTuple;
import util.ISet;
import util.PowerSet;
import util.UtilISet;



public class StateSliceKW extends State {

    private final ComplementSliceKW mComplement;
    private final Slice mOSets;
    
    public StateSliceKW(ComplementSliceKW complement, int id, Slice osets) {
        super(id);
        this.mComplement = complement;
        this.mOSets = osets;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();

    /**
     * For normal transitions in the complement, see the paper
     *   "Complementation, Disambiguation, and Determinization of Buchi Automata Unified" 
     *     by Detlef Kaehler and Thomas Wilke In ICALP 2008
     * They defined the run trees and divide the states at the same level of the trees as slice
     * 
     * we let 0 as newly emerging branch (marked components)
     *        1 as infinite continuations
     *    and 2 as die out in the future (no continuation at some point)
     *    
     *    There are two parts in the complement (i) the initial part and (ii) the accepting part.
     *    
     *    1. [Jump to accepting part]
     *    Once the runs nondeterministically leave the initial part and go to the accepting part,
     *    we will guess whether the runs of each component will have infinitely many continuations
     *    or will die out in the future.
     *    
     *    2. [Final states]
     *    The final states will be those states which do not have die out components.
     *    
     *    3. [Transition in the accepting part]
     *      (i) current state <(Q1, c1), ..., (Qn, cn)> is not final state (has die out)
     *          for every component Qi with color ci, c'_{2i} = c'_{2i+1} = ci 
     *          except that c'_{2i} = new (0) when ci = inf (1). This is to mark the component
     *          filled with final states in the input BA as newly emerging runs.
     *      (ii) current state <(Q1, c1), ..., (Qn, cn)> is final state (no die out)
     *          for every component Qi with color ci, c'_{2i} = c'_{2i+1} = die 
     *          except that c'_{2i} = die (2), c'_{2i+1} = inf when ci = inf (1). 
     *          This is to make the runs of the component filled with final states to die out
     *          and keep only the runs of the other component without final states. Those runs
     *          of marked components (new, 0) also have to die out. 
     *      (iii) current state <(Q1, c1), ..., (Qn, cn)> has ci = inf (1) and Q'_{2i+1} is empty,
     *            then no successor for this letter since it is a wrong guess (no continuation).
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
        boolean hasColoredSucc = true;
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
            if(mOSets.getColor(i) == Color.ONE
            && nonFinalSuccs.isEmpty()) {
                hasColoredSucc = false;
            }
        }
        StateSliceKW nextState;
        //1. non-colored states compute successor
        if(! mOSets.isColored()) {
            Slice osets = new Slice(false);
            ISet indices = UtilISet.newISet();
            for(int i = 0; i < nextOrdSets.size(); i ++) {
                osets.addSet(nextOrdSets.get(i), Color.NONE);
                indices.set(i);
            }
            nextState = mComplement.getOrAddState(osets);
            super.addSuccessor(letter, nextState.getId());
            succs.set(nextState.getId());
            System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + osets + " : " + letter);
            
            //nondeterministic choices for {inf, die}
            // we guess to inf for those component index appearing in set
            PowerSet infGuesses = new PowerSet(indices);
            while(infGuesses.hasNext()) {
                ISet guess = infGuesses.next();
                osets = new Slice(true);
                for(int i = 0; i < nextOrdSets.size(); i ++) {
                    if(guess.get(i)) {
                        // inf
                        osets.addSet(nextOrdSets.get(i), Color.ONE);
                    }else {
                        // die
                        osets.addSet(nextOrdSets.get(i), Color.TWO);
                    }
                }
                nextState = mComplement.getOrAddState(osets);
                super.addSuccessor(letter, nextState.getId());
                succs.set(nextState.getId());
                System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + osets + " : " + letter);
            }
        }else if(hasColoredSucc){
            //2. every state compute colors
            Slice osets = new Slice(true);
            
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
        if(! mOSets.isFinal()) {
            if(mOSets.getColor(jpred) == Color.ONE
            && sjp.overlap(fset)) {
                // f_i is inf and final component set to new
                return Color.ZERO; 
            }else {
                // just f_i
                return mOSets.getColor(jpred);
            }
        }else {
            // current state is final
            if (mOSets.getColor(jpred) == Color.ONE
            && !sjp.overlap(fset)) {
                // f_i is inf and not final component set to inf
                return Color.ONE;
            } else {
                // otherwise die
                return Color.TWO;
            }
        }
    }
    
    @Override
    public int hashCode() {
        return mOSets.hashCode();
    }
    
    @Override
    public String toString() {
        return mOSets.toString() + ((mOSets.isFinal())? "*": "0");
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null) return false;
        if(obj instanceof StateSliceKW) {
            StateSliceKW other = (StateSliceKW)obj;
            return this.mOSets.equals(other.mOSets);
        }
        return false;
    }
}
