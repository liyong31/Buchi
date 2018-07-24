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

package operation.complement.ncsb;

import automata.IBuchi;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.complement.Complement;
import util.ISet;
import util.UtilISet;

/**
 * Only valid for Semi-deterministic Buchi automata
 * NCSB On-the-fly version: input Buchi automaton is constructed during its complementation
 * <br>
 * "Complementing Semi-deterministic Büchi Automata" 
 * by František Blahoudek, Matthias Heizmann, Sven Schewe, Jan Strejček and Ming-Hsien Tsai
 * in TACAS 2016 (NCSB)
 * <br>
 * "Advanced Automata-based Algorithms for Program Termination Checking"
 * by Yu-Fang Chen, Matthias Heizmann, Ondra Lengál, Yong Li, Ming-Tsien Tsai, Andrea Turrini and Lijun Zhang.
 * In PLDI 2018 (NCSB + Lazy-S version)
 * <br>
 * NCSB + Lazy-B has not been published yet
 * 
 * */
public class ComplementNcsbOtf extends Complement {

    protected TObjectIntMap<StateNcsbOtf> mStateIndices;
    
    public ComplementNcsbOtf(IBuchi buchi) {
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
        StateNcsbOtf state = new StateNcsbOtf(this, 0, ncsb);
        if(C.isEmpty()) this.setFinal(0);
        this.setInitial(0);
        int id = this.addState(state);
        mStateIndices.put(state, id);
    }
    

    protected StateNcsbOtf getOrAddState(NCSB ncsb) {
        
        StateNcsbOtf state = new StateNcsbOtf(this, 0, ncsb);
        
        if(mStateIndices.containsKey(state)) {
            return getStateNCSB(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateNcsbOtf newState = new StateNcsbOtf(this, index, ncsb);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(ncsb.getBSet().isEmpty()) setFinal(index);
            return newState;
        }
    }

    @Override
    public String getName() {
        return "ComplementNcsbOtf";
    }
    
    public StateNcsbOtf getStateNCSB(int id) {
        return (StateNcsbOtf) getState(id);
    }
    
    public void testLemma() {
        for(int i = 0; i < getStateSize(); i ++) {
            StateNcsbOtf s = (StateNcsbOtf) getState(i);
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
                StateNcsbOtf nn = getOrAddState(ncsb);
                System.out.println(s.getNCSB() + " : " + ncsb);
                assert nn != null : "Not reachable " + s.getNCSB() + " -> " + ncsb;
            }

        }
    }

}
