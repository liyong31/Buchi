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

package operation.complement.tuple;

import java.util.ArrayList;

import automata.IBuchi;
import automata.State;
import gnu.trove.map.TIntIntMap;
import main.Options;
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
        IBuchi operand = mComplement.getOperand();
        OrderedSetsGenerator generator = new OrderedSetsGenerator(operand, mOSets, letter);
        ArrayList<ISet> nextOrdSets = generator.getResult().mNextOrdSets;
        TIntIntMap predMap = generator.getResult().mPredMap;
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
            if(Options.mDebug) System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + osets + " : " + letter);
        }
        //2. every state compute colors
        {
            OrderedSets osets = new OrderedSets(true);
            ISet fset = operand.getFinalStates();
            for(int i = 0; i < nextOrdSets.size(); i ++) {
                osets.addSet(nextOrdSets.get(i)
                        , decideColor(nextOrdSets.get(i), predMap.get(i), fset));
            }
            if(Options.mMergeAdjacentSets) {
                osets = osets.mergeAdjacentSets();
            }
            // merge 1-colored following a 2-colored
            if(Options.mMergeAdjacentColoredSets) {
                osets = osets.mergeAdjacentColoredSets();
            }
            nextState = mComplement.getOrAddState(osets);
            super.addSuccessor(letter, nextState.getId());
            succs.set(nextState.getId());
            if(Options.mDebug) System.out.println("" + getId() + " " + toString() + " -> " + nextState.getId() + " " + osets + " : " + letter);
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
                if(Options.mLazyB) {
                    // those which have just visited final states will stay ONE
                    if(mOSets.getColor(jpred) == Color.ZERO && sjp.overlap(fset)) {
                        return Color.ONE;
                    }else {
                        return Color.TWO;
                    }
                }else {
                    return Color.TWO;
                }
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
