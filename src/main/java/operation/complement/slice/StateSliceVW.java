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

/**
 * we let 0 as newly emerging branch
 *        1 as infinite branches
 *    and 2 as die out 
 * **/

public class StateSliceVW extends State {

    private final ComplementSliceVW mComplement;
    private final OrderedSets mOSets;
    
    public StateSliceVW(ComplementSliceVW complement, int id, OrderedSets osets) {
        super(id);
        this.mComplement = complement;
        this.mOSets = osets;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();

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
        StateSliceVW nextState;
        //1. non-colored states compute successor
        if(! mOSets.isColored()) {
            OrderedSets osets = new OrderedSets(false);
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
                osets = new OrderedSets(true);
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
        return mOSets.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null) return false;
        if(obj instanceof StateSliceVW) {
            StateSliceVW other = (StateSliceVW)obj;
            return this.mOSets.equals(other.mOSets);
        }
        return false;
    }
}
