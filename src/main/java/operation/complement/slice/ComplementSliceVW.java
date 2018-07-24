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

package operation.complement.slice;

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import main.Options;
import operation.complement.Complement;
import operation.complement.tuple.Color;
import operation.complement.tuple.OrderedSets;
import operation.explore.Explore;
import operation.removal.Remove;
import util.ISet;


/**
 * Automata: From Logics to Algorithms
 * by Moshe Y. Vardi and Thomas Wilke
 * In Logic and Automata: History and Perspective
 * */
public class ComplementSliceVW extends Complement {

    public ComplementSliceVW(IBuchi operand) {
        super(operand);
    }

    private TObjectIntMap<StateSliceVW> mStateIndices;
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        ISet inits = mOperand.getInitialStates().clone();
        ISet pset1 = mOperand.getFinalStates().clone();
        pset1.and(inits);
        ISet pset2 = inits;
        pset2.andNot(pset1);
        Slice osets = new Slice(false);
        if(!pset1.isEmpty()) {
            osets.addSet(pset1, Color.NONE);
        }
        if(!pset2.isEmpty()) {
            osets.addSet(pset2, Color.NONE);
        }
        
        StateSliceVW stateSlice = getOrAddState(osets);
        this.setInitial(stateSlice.getId());
    }
    
    protected StateSliceVW getStateSlice(int id) {
        return (StateSliceVW)getState(id);
    }

    protected StateSliceVW getOrAddState(Slice osets) {
        StateSliceVW state = new StateSliceVW(this, 0, osets);
        if(mStateIndices.containsKey(state)) {
            return getStateSlice(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateSliceVW newState = new StateSliceVW(this, index, osets);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(osets.isFinal()) setFinal(index);
            return newState;
        }
    }

    @Override
    public String getName() {
        return "ComplementSliceVW";
    }
    
    public static void main(String[] args) {
        IBuchi buchi = new Buchi(2);
        
        buchi.addState();
        buchi.addState();
        buchi.addState();
        
        buchi.getState(0).addSuccessor(0, 0);
        buchi.getState(0).addSuccessor(1, 0);
        buchi.getState(0).addSuccessor(0, 1);
        buchi.getState(0).addSuccessor(1, 1);
        
        buchi.getState(1).addSuccessor(0, 2);
        buchi.getState(1).addSuccessor(1, 1);
        
        buchi.getState(2).addSuccessor(0, 2);
        buchi.getState(2).addSuccessor(1, 2);
        
        buchi.setFinal(1);
        buchi.setInitial(0);
        
        System.out.println(buchi.toDot());
        Options.mEnhancedSliceGuess = true;
        ComplementSliceVW complement = new ComplementSliceVW(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        System.out.println(complement.toBA());
        
        IBuchi result = (new Remove(complement)).getResult();
        
        System.out.println(result.toDot());
        
        System.out.println(result.toBA());
        

    }

}
