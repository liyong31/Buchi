package operation.isincluded;

import java.util.ArrayList;
import java.util.List;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import operation.complement.StateNCSB;

public class Antichain {
    
    private TIntObjectMap<List<StateNCSB>> mPairMap;
    
    public Antichain() {
        mPairMap = new TIntObjectHashMap<>();
    }
    
    /**
     * return true if @param snd has been added successfully
     * */
    public boolean addAsccPair(AsccPair pair) {
        return addAsccPair(pair.getFstState(), pair.getSndComplementState());
    }
    
    public boolean addAsccPair(int fst, StateNCSB snd) {
        
        List<StateNCSB> sndElem = mPairMap.get(fst);
        
        if(sndElem == null) {
            sndElem = new ArrayList<>();
        }
        
        List<StateNCSB> copy = new ArrayList<>();
        //avoid to add pairs are covered by other pairs
        for(int i = 0; i < sndElem.size(); i ++) {
            StateNCSB s = sndElem.get(i);
            //pairs covered by the new pair
            //will not be kept in copy
            if(s.getNCSB().coveredBy(snd.getNCSB())) {
//              mTask.increaseDelPairInAntichain();
                continue;
            }else if(snd.getNCSB().coveredBy(s.getNCSB())) {
                // no need to add it
//              mTask.increaseRejPairByAntichain();
                return false;
            }
            copy.add(s);
        }
        
        copy.add(snd); // should add snd
        mPairMap.put(fst, copy);
        return true;
    }
    
    public boolean covers(AsccPair pair) {
        List<StateNCSB> sndElem = mPairMap.get(pair.getFstState());
        if(sndElem == null) return false;
        
        StateNCSB snd = pair.getSndComplementState();
        for(int i = 0; i < sndElem.size(); i ++) {
            StateNCSB s = sndElem.get(i);
            if(snd.getNCSB().coveredBy(s.getNCSB())) { // no need to add it
                return true;
            }
        }
        
        return false;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
//      for(Entry<Integer, List<StateNCSB>> entry : mPairMap.entrySet()) {
//          sb.append(entry.getKey() + " -> " + entry.getValue() + "\n");
//      }
        TIntObjectIterator<List<StateNCSB>> iter = mPairMap.iterator();
        while(iter.hasNext()) {
            iter.advance();
            sb.append(iter.key() + " -> " + iter.value() + "\n");
        }
        return sb.toString();
    }
    
    public int size() {
        int num = 0;
//      for(Map.Entry<Integer, List<StateNCSB>> entry : mPairMap.entrySet()) {
//          num += entry.getValue().size();
//      }
        TIntObjectIterator<List<StateNCSB>> iter = mPairMap.iterator();
        while(iter.hasNext()) {
            iter.advance();
            num += iter.value().size();
        }
        return num;
    }

}
