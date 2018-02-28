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

import java.util.ArrayList;
import java.util.List;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * A set to store empty states
 * */
public class Antichain {
    
    private final TIntObjectMap<List<ProductState>> mAntichain;
    
    public Antichain() {
        mAntichain = new TIntObjectHashMap<>();
    }
    
    public boolean covers(ProductState state) {
        List<ProductState> sndElem = mAntichain.get(state.getFirstState());
        if(sndElem == null) return false;
        for(int i = 0; i < sndElem.size(); i ++) {
            ProductState elem = sndElem.get(i);
            if(state.coveredBy(elem)) { // no need to add it
                return true;
            }
        }
        return false;
    }
    
    public boolean addProductState(ProductState state) {
        final int fstState = state.getFirstState();
        List<ProductState> sndElem = mAntichain.get(fstState);
        
        if(sndElem == null) {
            sndElem = new ArrayList<>();
        }
        
        List<ProductState> copy = new ArrayList<>();
        //avoid to add pairs are covered by other pairs
        for(int i = 0; i < sndElem.size(); i ++) {
            ProductState elem = sndElem.get(i);
            //pairs covered by the new pair
            //will not be kept in copy
            if(elem.coveredBy(state)) {
                continue;
            }else if(state.coveredBy(elem)) {
                return false;
            }
            copy.add(elem);
        }
        
        copy.add(state); // should add snd
        mAntichain.put(fstState, copy);
        return true;
    }
    
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        TIntObjectIterator<List<ProductState>> iter = mAntichain.iterator();
        while(iter.hasNext()) {
            iter.advance();
            sb.append(iter.key() + " -> " + iter.value() + "\n");
        }
        return sb.toString();
    }
    
    public int size() {
        int num = 0;
        TIntObjectIterator<List<ProductState>> iter = mAntichain.iterator();
        while(iter.hasNext()) {
            iter.advance();
            num += iter.value().size();
        }
        return num;
    }

}
