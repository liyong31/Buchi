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

import util.ISet;
import util.UtilISet;

// Buechi automata

public class AccBA implements IAcc {
    
    private final ISet mFinalStates;
    
    public AccBA() {
        this.mFinalStates = UtilISet.newISet();
    }

    @Override
    public AccType getType() {
        return AccType.BUCHI;
    }
    
    public void setFinal(int state) {
        mFinalStates.set(state);
    }
    
    public boolean isFinal(int state) {
        return mFinalStates.get(state);
    }
    
    public ISet getFinalStates() {
        return mFinalStates;
    }

    @Override
    public void simplify() {
        
    }

}
