package operation.complement.slice;

import java.util.ArrayList;

import automata.IBuchi;
import automata.State;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import main.Options;
import operation.complement.tuple.Color;

import util.ISet;
import util.PowerSet;
import util.UtilISet;



public class StateSliceVW extends State {

    private final ComplementSliceVW mComplement;
    private final Slice mSlice;
    
    public StateSliceVW(ComplementSliceVW complement, int id, Slice slice) {
        super(id);
        this.mComplement = complement;
        this.mSlice = slice;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();

    /**
     * For normal transitions in the complement, see the paper
     *   "Automata: From Logics to Algorithms" by Moshe Y. Vardi and Thomas Wilke
     *    In Logic and Automata: History and Perspective
     * They defined the run trees and divide the states at the same level of the trees as slice
     * 
     * we let * as newly emerging branch (marked components)
     *        1 as infinite continuations
     *    and 0 as die out in the future (no continuation at some point)
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
     *          except that c'_{2i} = new (*) when ci = inf (1). This is to mark the component
     *          filled with final states in the input BA as newly emerging runs.
     *      (ii) current state <(Q1, c1), ..., (Qn, cn)> is final state (no die out)
     *          for every component Qi with color ci, c'_{2i} = c'_{2i+1} = die 
     *          except that c'_{2i} = die (0), c'_{2i+1} = inf when ci = inf (1). 
     *          This is to make the runs of the component filled with final states to die out
     *          and keep only the runs of the other component without final states. Those runs
     *          of marked components (new, *) also have to die out. 
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
        ArrayList<ISet> ordSets = mSlice.getOrderedSets(); 
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
            if(mSlice.getColor(i) == Slice.getInfinite()
            && nonFinalSuccs.isEmpty() && !Options.mEnhancedSliceGuess) {
                hasColoredSucc = false;
            }
        }
        StateSliceVW nextState;
        //1. non-colored states compute successor
        if(! mSlice.isColored()) {
            Slice nextSlice = new Slice(false);
            ISet indices = UtilISet.newISet();
            for(int i = 0; i < nextOrdSets.size(); i ++) {
                nextSlice.addSet(nextOrdSets.get(i), Slice.getNone());
                indices.set(i);
            }
            nextState = mComplement.getOrAddState(nextSlice);
            super.addSuccessor(letter, nextState.getId());
            succs.set(nextState.getId());
            System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + nextSlice + " : " + letter);
            
            if(Options.mEnhancedSliceGuess) {
                /**
                 * "STATE OF BÜCHI COMPLEMENTATION"
                 * by MING-HSIEN TSAI, SETH FOGARTY, MOSHE Y. VARDI, and YIH-KUEN TSAY
                 * in LMCS, Vol. 10(4:13)2014, pp. 1–27
                 * */
                nextSlice = new Slice(true);
                ISet fset = operand.getFinalStates();
                for(int i = 0; i < nextOrdSets.size(); i ++) {
                    ISet si = nextOrdSets.get(i);
                    nextSlice.addSet(si, (si.overlap(fset) ? Slice.getDieout() : Slice.getInfinite()));
                }
                nextState = mComplement.getOrAddState(nextSlice);
                super.addSuccessor(letter, nextState.getId());
                succs.set(nextState.getId());
                System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + nextSlice + " : " + letter);
            }else {
                //nondeterministic choices for {inf, die}
                // we guess to inf for those component index appearing in set
                PowerSet infGuesses = new PowerSet(indices);
                while(infGuesses.hasNext()) {
                    ISet guess = infGuesses.next();
                    nextSlice = new Slice(true);
                    for(int i = 0; i < nextOrdSets.size(); i ++) {
                        if(guess.get(i)) {
                            // inf
                            nextSlice.addSet(nextOrdSets.get(i), Slice.getInfinite());
                        }else {
                            // die
                            nextSlice.addSet(nextOrdSets.get(i), Slice.getDieout());
                        }
                    }
                    nextState = mComplement.getOrAddState(nextSlice);
                    super.addSuccessor(letter, nextState.getId());
                    succs.set(nextState.getId());
                    System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + nextSlice + " : " + letter);
                } 
            }
        }else if(hasColoredSucc){
            //2. every state compute colors
            Slice nextSlice = new Slice(true);
            ISet fset = operand.getFinalStates();
            for(int i = 0; i < nextOrdSets.size(); i ++) {
                nextSlice.addSet(nextOrdSets.get(i)
                        , decideColor(nextOrdSets.get(i), predMap.get(i), fset));
            }
            nextState = mComplement.getOrAddState(nextSlice);
            super.addSuccessor(letter, nextState.getId());
            succs.set(nextState.getId());
            System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + nextSlice + " : " + letter);
        }
        
        return succs;
    }
    
    
    private Color decideColor(ISet sjp, int jpred, ISet fset) {        
        if(! mSlice.isFinal()) {
            if(mSlice.getColor(jpred) == Slice.getInfinite()
            && sjp.overlap(fset)) {
                // f_i is inf and final component set to new (marked)
                return Slice.getMarked(); 
            }else {
                // just f_i
                return mSlice.getColor(jpred);
            }
        }else {
            // current state is final
            if (mSlice.getColor(jpred) == Slice.getInfinite()
            && !sjp.overlap(fset)) {
                // f_i is inf and not final component set to inf
                return Slice.getInfinite();
            } else {
                // otherwise die
                return Slice.getDieout();
            }
        }
    }
    
    @Override
    public int hashCode() {
        return mSlice.hashCode();
    }
    
    @Override
    public String toString() {
        return mSlice.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null) return false;
        if(obj instanceof StateSliceVW) {
            StateSliceVW other = (StateSliceVW)obj;
            return this.mSlice.equals(other.mSlice);
        }
        return false;
    }
}
