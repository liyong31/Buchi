package operation.complement.tuple;

import java.util.List;

import automata.IBuchi;
import main.Options;
import operation.complement.slice.Slice;
import util.ISet;
import util.UtilISet;

public class OrderedSetsGenerator {
    
    private final IBuchi mOperand;
    private final OrderedSets mOrdSets;
    private final int mLetter;
    private final OrderedSetsResult mResult;
    private boolean mHasColoredSuccessor = true;
    
    public OrderedSetsGenerator(IBuchi operand
            , OrderedSets ordSets, int letter) {
        this.mOperand = operand;
        this.mOrdSets = ordSets;
        this.mLetter = letter;
        this.mResult = new OrderedSetsResult();
        computeNextOrderedSets();
    }
    
    public boolean hasColoredSuccessor() {
        return mHasColoredSuccessor;
    }
    
    public OrderedSetsResult getResult() {
        return mResult;
    }
    
    private void computeNextOrderedSets() {
        List<ISet> ordSets = mOrdSets.getOrderedSets();
        ISet leftSuccs = UtilISet.newISet();
        int index = 0;
        for(int i = 0; i < ordSets.size(); i ++) {
            ISet Si = ordSets.get(i);
            ISet finalSuccs = UtilISet.newISet();
            ISet nonFinalSuccs = UtilISet.newISet();
            for(final int p : Si) {
                for(final int q : mOperand.getState(p).getSuccessors(mLetter)) {
                    // ignore successors already have been visited
                    if(leftSuccs.get(q)) continue;
                    if(mOperand.isFinal(q)) {
                        finalSuccs.set(q);
                    }else {
                        nonFinalSuccs.set(q);
                    }
                    leftSuccs.set(q);
                }
            }
            if(!finalSuccs.isEmpty()) {
                mResult.mNextOrdSets.add(finalSuccs);
                mResult.mPredMap.put(index, i);
                index ++;
            }
            if(!nonFinalSuccs.isEmpty()) {
                mResult.mNextOrdSets.add(nonFinalSuccs);
                mResult.mPredMap.put(index, i);
                index ++;
            }
            // if INF component has no Q'_{2i+1} successor component
            if(mOrdSets.getColor(i) == Slice.getInfinite()
               && nonFinalSuccs.isEmpty() 
               && !Options.mEnhancedSliceGuess) {
                mHasColoredSuccessor = false;
            }
        }
    }

}
