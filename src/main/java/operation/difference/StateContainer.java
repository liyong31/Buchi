package operation.difference;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import util.ISet;
import util.UtilISet;

public class StateContainer {
    
    private final int mState;
    private final TIntObjectMap<ISet> mSuccs; // successors
    private final TIntObjectMap<ISet> mPreds; // predecessors
    
    public StateContainer(int state) {
        mState = state;
        mSuccs = new TIntObjectHashMap<>();
        mPreds = new TIntObjectHashMap<>();
    }
    
    public int getState() {
        return mState;
    }
    
    public void addSuccessors(int letter, int succ) {
        ISet succs = mSuccs.get(letter);
        if(succs == null) {
            succs = UtilISet.newISet();
        }
        succs.set(succ);
        mSuccs.put(letter, succs);
    }
    
    public void addPredecessors(int letter, int pred) {
        ISet preds = mSuccs.get(letter);
        if(preds == null) {
            preds = UtilISet.newISet();
        }
        preds.set(pred);
        mSuccs.put(letter, preds);
    }
    
    

}
