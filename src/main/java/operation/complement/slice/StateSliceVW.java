/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi.
 * 
 * Buchi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Buchi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Buchi. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package operation.complement.slice;

import java.util.ArrayList;

import automata.IBuchi;
import automata.State;
import gnu.trove.map.TIntIntMap;
import main.Options;
import operation.complement.tuple.Color;
import operation.complement.tuple.OrderedSetsGenerator;
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
        IBuchi operand = mComplement.getOperand();
        OrderedSetsGenerator generator = new OrderedSetsGenerator(operand, mSlice, letter);
        ArrayList<ISet> nextOrdSets = generator.getResult().mNextOrdSets;
        TIntIntMap predMap = generator.getResult().mPredMap;
        boolean hasColoredSucc = generator.hasColoredSuccessor();
        
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
            if(Options.mDebug) System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + nextSlice + " : " + letter);
            
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
                if(Options.mDebug) System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + nextSlice + " : " + letter);
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
                    if(Options.mDebug) System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + nextSlice + " : " + letter);
                } 
            }
        }else if(hasColoredSucc){
            //2. every state compute colors
            Slice nextSlice = new Slice(true);
            ISet fset = operand.getFinalStates();
            for(int i = 0; i < nextOrdSets.size(); i ++) {
                nextSlice.addSet(nextOrdSets.get(i)
                        , decideColor(mSlice, nextOrdSets.get(i), predMap.get(i), fset));
            }
            nextState = mComplement.getOrAddState(nextSlice);
            super.addSuccessor(letter, nextState.getId());
            succs.set(nextState.getId());
            if(Options.mDebug) System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + nextSlice + " : " + letter);
        }
        
        return succs;
    }
    
    
    protected static Color decideColor(Slice slice, ISet sjp, int jpred, ISet fset) {        
        if(! slice.isFinal()) {
            if(slice.getColor(jpred) == Slice.getInfinite()
            && sjp.overlap(fset)) {
                // f_i is inf and final component set to new (marked)
                return Slice.getMarked(); 
            }else {
                // just f_i
                return slice.getColor(jpred);
            }
        }else {
            // current state is final
            if (slice.getColor(jpred) == Slice.getInfinite()
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
