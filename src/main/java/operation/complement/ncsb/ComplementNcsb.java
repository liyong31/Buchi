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

package operation.complement.ncsb;

import automata.IBuchi;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.complement.Complement;
import util.ISet;
import util.UtilISet;

/**
 * Only valid for Semi-deterministic Buchi automata
 * */
public class ComplementNcsb extends Complement {

    private TObjectIntMap<StateNCSB> mStateIndices;
    
    public ComplementNcsb(IBuchi buchi) {
        super(buchi);
        assert mOperand.isSemiDeterministic();
    }

    @Override
    protected void computeInitialStates() {
        mStateIndices = new TObjectIntHashMap<>();
        ISet C = mOperand.getInitialStates().clone();
        C.and(mOperand.getFinalStates()); // goto C
        ISet N = mOperand.getInitialStates().clone();
        N.andNot(C);
        NCSB ncsb = new NCSB(N, C, UtilISet.newISet(), C);
        StateNCSB state = new StateNCSB(this, 0, ncsb);
        if(C.isEmpty()) this.setFinal(0);
        this.setInitial(0);
        int id = this.addState(state);
        mStateIndices.put(state, id);
    }
    

    protected StateNCSB getOrAddState(NCSB ncsb) {
        
        StateNCSB state = new StateNCSB(this, 0, ncsb);
        
        if(mStateIndices.containsKey(state)) {
            return getStateNCSB(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateNCSB newState = new StateNCSB(this, index, ncsb);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(ncsb.getBSet().isEmpty()) setFinal(index);
            return newState;
        }
    }

    @Override
    public String getName() {
        return "ComplementNcsb";
    }
    
    public StateNCSB getStateNCSB(int id) {
        return (StateNCSB) getState(id);
    }
    
    public void testLemma() {
        for(int i = 0; i < getStateSize(); i ++) {
            StateNCSB s = (StateNCSB) getState(i);
            ISet N = s.getNCSB().copyNSet();
            ISet C = s.getNCSB().copyCSet();
            ISet B = s.getNCSB().copyBSet();
            C.andNot(B);
            C.andNot(getFinalStates());
            for(int n : C) {
                ISet Cp = s.getNCSB().copyCSet();
                Cp.clear(n);
                ISet S = s.getNCSB().copySSet();
                S.set(n);
                NCSB ncsb = new NCSB(N, Cp, S, B);
                StateNCSB nn = getOrAddState(ncsb);
                System.out.println(s.getNCSB() + " : " + ncsb);
                assert nn != null : "Not reachable " + s.getNCSB() + " -> " + ncsb;
            }

        }
    }

}
