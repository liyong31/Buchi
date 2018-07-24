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

package operation.complement.dba;

import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.complement.Complement;
import util.ISet;

// must make sure DBA is complete
/**
 * Complementing deterministic Büchi automata in polynomial time
 * by R.P.Kurshan
 * in Journal of Computer and System Sciences
 * */
public class ComplementDBA extends Complement {

    private TObjectIntMap<StateDBA> mStateIndices;
    
    public ComplementDBA(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementDBA";
    }
    
    @Override
    protected void computeInitialStates() {
        mStateIndices = new TObjectIntHashMap<>();
        ISet inits = mOperand.getInitialStates();
        if(inits.cardinality() > 1) {
            throw new UnsupportedOperationException("BA has more than one initial states " + inits.toString());
        }
        if(inits.cardinality() == 0) {
            return;
        }
        int init = inits.iterator().next();
        StateDBA state = getOrAddState(init, false);
        this.setInitial(state.getId());
    }
    
    public StateDBA getStateDBA(int id) {
        return (StateDBA) getState(id);
    }
    
    protected StateDBA getOrAddState(int st, boolean label) {
        StateDBA state = new StateDBA(this, 0, st, label);
        
        if(mStateIndices.containsKey(state)) {
            return getStateDBA(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateDBA newState = new StateDBA(this, index, st, label);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(!mOperand.isFinal(st) && label) setFinal(index);
            return newState;
        }
    }

}
