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

package operation.complement.rank;

import automata.IBuchi;
import automata.State;
import util.ISet;
import util.UtilISet;

public abstract class StateRank<C extends ComplementRank<?>> extends State {
    
    protected final C mComplement;
    protected final IBuchi mOperand;
    protected final LevelRanking mLevelRanking; // (S, O, f) 
    
    protected final ISet mVisitedLetters;
    
    public StateRank(C complement, int id, LevelRanking lvlRank) {
        super(id);
        this.mComplement = complement;
        this.mOperand = complement.getOperand();
        this.mLevelRanking = lvlRank;
        this.mVisitedLetters = UtilISet.newISet();
    }
        
    @Override
    public String toString() {
        return  mLevelRanking + "";
    }
    
    @Override
    public int hashCode() {
        return mLevelRanking.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(this.getClass().isInstance(obj)) {
            @SuppressWarnings("unchecked")
            StateRank<C> other = (StateRank<C>)obj;
            return mLevelRanking.equals(other.mLevelRanking);
        }
        return false;
    }
    
    public LevelRanking getLevelRanking() {
        return mLevelRanking;
    }

}
