package operation.complement.tuple;

import java.util.ArrayList;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import util.ISet;

public class OrderedSetsResult {
    
    public ArrayList<ISet> mNextOrdSets;
    
    public TIntIntMap mPredMap;
    
    public OrderedSetsResult() {
        this.mNextOrdSets = new ArrayList<>();
        this.mPredMap = new TIntIntHashMap();
    }
    
    public OrderedSetsResult(ArrayList<ISet> nextOrdSets, TIntIntMap predMap) {
        this.mNextOrdSets = nextOrdSets;
        this.mPredMap = predMap;
    }
    
    @Override
    public String toString() {
        return "<" + mNextOrdSets + ":" + this.mPredMap + ">";
    }
    

}
