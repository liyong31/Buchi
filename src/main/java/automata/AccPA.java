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

package automata;


import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;
import util.ISet;
import util.UtilISet;

// Rabin automata
public class AccPA implements IAcc {

    private final TIntIntMap mPs;
    
    public AccPA() {
        this.mPs = new TIntIntHashMap();
    }
    
    @Override
    public AccType getType() {
        return AccType.PARITY;
    }
    
    public void setColor(int state, int color) {
        mPs.put(state, color);
    }
    
    public int getColor(int state) {
        return mPs.get(state);
    }
    
    public ISet getStatesByColor(int color) {
        ISet states = UtilISet.newISet();
        TIntIntProcedure procedure = new TIntIntProcedure() {
            @Override
            public boolean execute(int state, int c) {
                if(c == color) {
                    states.set(state);
                }
                return true;
            }
        };
        mPs.forEachEntry(procedure);
        return states;
    }

    @Override
    public void simplify() {
        
    }

}
