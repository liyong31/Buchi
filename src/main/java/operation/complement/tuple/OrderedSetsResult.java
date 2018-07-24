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
