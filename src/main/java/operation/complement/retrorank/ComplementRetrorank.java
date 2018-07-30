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

package operation.complement.retrorank;

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import main.Options;
import operation.complement.Complement;
import operation.complement.ncsb.ComplementNcsb;
import operation.complement.rank.ComplementRankTight;
import operation.removal.Remove;
import util.ISet;

/**
 * 
 *  Unifying Buchi Complementation Constuctions
 *  by  Seth Fogarty, Orna Kupferman, Moshe Y. Vardi and Thomas Wilke
 *  in LMCS
 */
public class ComplementRetrorank extends Complement {

    protected TObjectIntMap<StateRetrorank> mStateIndices;
    
    public ComplementRetrorank(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementRetrorank";
    }
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        ISet inits = mOperand.getInitialStates().clone();
        ISet pset1 = mOperand.getFinalStates().clone();
        pset1.and(inits);
        ISet pset2 = inits;
        pset2.andNot(pset1);
        RetrospectiveRank retroRank = new RetrospectiveRank(false);
        if(!pset1.isEmpty()) {
            retroRank.add(pset1);
        }
        if(!pset2.isEmpty()) {
            retroRank.add(pset2);
        }
        StateRetrorank stateSlice = getOrAddState(retroRank);
        this.setInitial(stateSlice.getId());
    }
    
    protected StateRetrorank getStateRetrorank(int id) {
        return (StateRetrorank)getState(id);
    }
    
    protected StateRetrorank getOrAddState(RetrospectiveRank retroRank) {
        StateRetrorank state = new StateRetrorank(this, 0, retroRank);
        if(mStateIndices.containsKey(state)) {
            return getStateRetrorank(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateRetrorank newState = new StateRetrorank(this, index, retroRank);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(retroRank.isFinal()) setFinal(index);
            return newState;
        }
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
        ComplementRetrorank complement = new ComplementRetrorank(buchi);
        complement.explore();
        System.out.println(complement.toDot());
        System.out.println(complement.toBA());
        System.out.println((new Remove(complement)).getResult().getStateSize());
        
        ComplementNcsb complementNcsb = new ComplementNcsb(buchi);
        complementNcsb.explore();
        System.out.println(complementNcsb.toDot());
        System.out.println(complementNcsb.toBA());
        
    }

}
