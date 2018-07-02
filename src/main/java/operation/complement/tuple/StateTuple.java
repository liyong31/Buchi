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
