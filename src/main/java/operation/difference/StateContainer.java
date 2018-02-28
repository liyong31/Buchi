/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi which is a simple version of SemiBuchi.
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
        ISet preds = mPreds.get(letter);
        if(preds == null) {
            preds = UtilISet.newISet();
        }
        preds.set(pred);
        mPreds.put(letter, preds);
    }
    
    

}
