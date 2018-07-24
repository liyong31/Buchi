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

package operation.complement.ramsey;

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.complement.Complement;
import operation.complement.tuple.ComplementTuple;
import operation.explore.Explore;
import operation.explore.UtilExplore;
import operation.removal.Remove;

/**
 * "The complementation problem for Buchi automata with applications to temporal logic"
 * by A. Prasad Sistla, Moshe Y. Vardi, and Pierre Wolper in Theoretical Computer Science.
 * <br>
 * "BÜCHI COMPLEMENTATION AND SIZE-CHANGE TERMINATION"
 * by SETH FOGARTY a AND MOSHE Y. VARDI in Logical Methods in Computer Science.
 * **/
public class ComplementRamsey extends Complement {

    private TObjectIntMap<StateRamsey> mStateIndices;
    private DABg mDA;
    
    public ComplementRamsey(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementRamsey";
    }
    
    protected DABg getDA() {
        return mDA;
    }
    
    @Override
    protected void computeInitialStates() {
        // first compute deterministic automaton
        this.mStateIndices = new TObjectIntHashMap<>();
        this.mDA = new DABg(this.mOperand);
        UtilExplore.explore(this.mDA);
        // compute initial state
        StateDABg state = (StateDABg)this.mDA.getState(0);
        StateRamsey init = getOrAddState(state, 0);
        this.setInitial(init.getId());
    }
    
    protected StateRamsey getStateRamsey(int id) {
        return (StateRamsey) getState(id);
    }
    
    protected StateRamsey getOrAddState(StateDABg state, int label) {
        StateRamsey newState = new StateRamsey(this, 0, state, label);
        if(mStateIndices.containsKey(newState)) {
            return getStateRamsey(mStateIndices.get(newState));
        }else {
            int index = getStateSize();
            newState = new StateRamsey(this, index, state, label);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(mDA.isInitial(state.getId()) && label > 0) {
                this.setFinal(id);
            }
            return newState;
        }
    }
    
    public static void main(String[] args) {
        IBuchi buchi = new Buchi(2);
        buchi.addState();
        buchi.addState();
        
        buchi.getState(0).addSuccessor(0, 0);
        buchi.getState(0).addSuccessor(1, 0);
        buchi.getState(0).addSuccessor(1, 1);
        
        buchi.getState(1).addSuccessor(0, 0);
        buchi.getState(1).addSuccessor(0, 1);
        
        buchi.setFinal(1);
        buchi.setInitial(0);
        
        ComplementRamsey cr = new ComplementRamsey(buchi);
        new Explore(cr);
        IBuchi crr = (new Remove(cr)).getResult();
        System.out.println(crr.toDot());
        System.out.println(crr.toBA());
        
        ComplementTuple ct = new ComplementTuple(buchi);
        new Explore(ct);
        System.out.println(ct.toDot());
        System.out.println(ct.toBA());
        
    }

}
