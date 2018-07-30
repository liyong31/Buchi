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
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import main.Options;
import operation.complement.Complement;

public abstract class ComplementRank<S extends State>  extends Complement {
    
    protected TObjectIntMap<S> mStateIndices;
    
    public ComplementRank(IBuchi operand) {
        super(operand);
    }
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        LevelRanking lvlRnk = null;
        if(!Options.mTightRank) {
            int n = mOperand.getStateSize();
            int r = mOperand.getFinalStates().cardinality();
            lvlRnk = new LevelRanking(true, false);
            for(final int init : mOperand.getInitialStates()) {
                lvlRnk.addLevelRank(init, 2*(n - r), false);
            }
        }else {
            lvlRnk = new LevelRanking(false, false);
            lvlRnk.setS(mOperand.getInitialStates());
        }
        S stateLvlRnk = getOrAddState(lvlRnk);
        this.setInitial(stateLvlRnk.getId());
    }
    
    @SuppressWarnings("unchecked")
    protected S getStateLevelRanking(int id) {
        return (S)getState(id);
    }
    
    protected abstract S makeRankState(int id, LevelRanking lvlRank);

    public S getOrAddState(LevelRanking lvlRank) {
        S state = makeRankState(0, lvlRank);
        if(mStateIndices.containsKey(state)) {
            return getStateLevelRanking(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            S newState = makeRankState(index, lvlRank);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(lvlRank.isFinal()) setFinal(index);
            return newState;
        }
    }

    @Override
    public String getName() {
        return "ComplementRank";
    }

}
