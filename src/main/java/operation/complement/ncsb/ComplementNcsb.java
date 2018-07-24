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

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import main.Options;
import operation.complement.Complement;
import operation.complement.nsbc.ComplementNsbc;
import operation.explore.Explore;
import operation.removal.Remove;
import util.ISet;
import util.PairXX;
import util.PowerSet;

/**
 * Only valid for Semi-deterministic Buchi automata
 * NCSB original version
 * <br>
 * "Complementing Semi-deterministic Büchi Automata" 
 * by František Blahoudek, Matthias Heizmann, Sven Schewe, Jan Strejček and Ming-Hsien Tsai
 * in TACAS 2016 (NCSB)
 * <br>
 */
public class ComplementNcsb extends Complement {
    
    protected ISet mDetStates;    // Q2
    protected ISet mNondetStates; // Q1
    protected TObjectIntMap<StateNcsb> mStateIndices;
    
    public ComplementNcsb(IBuchi buchi) {
        super(buchi);
    }
    
    /**
     * I'= {(Q1 ∩ I, C, S, C) | S ∪ C = I ∩ Q2 , S ∩ C = ∅}
     * */
    @Override
    protected void computeInitialStates() {
        // first compute partitions of states
        PairXX<ISet> partitions = UtilNcsb.partitionStates(mOperand);
        this.mDetStates = partitions.getFirst();
        this.mNondetStates = partitions.getSecond();
        
        // second compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        ISet mayInC = mOperand.getInitialStates().clone();
        mayInC.and(mDetStates); // S ∪ C = I ∩ Q2
        ISet N = mOperand.getInitialStates().clone();
        N.and(mNondetStates);   // Q1 ∩ I
        // S must not contain final states
        ISet mayInS = mayInC.clone();
        mayInS.andNot(mOperand.getFinalStates());
        PowerSet ps = new PowerSet(mayInS);
        while(ps.hasNext()) {
            // now we create initial states
            ISet S = ps.next();
            ISet C = mayInC.clone();
            C.andNot(S);
            NCSB ncsb = new NCSB(N, C, S, C);
            StateNcsb state = getOrAddState(ncsb);
            this.setInitial(state.getId());
        }
    }
    
    protected StateNcsb getOrAddState(NCSB ncsb) {
        StateNcsb state = new StateNcsb(this, 0, ncsb);
        if(mStateIndices.containsKey(state)) {
            return getStateNcsb(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            StateNcsb newState = new StateNcsb(this, index, ncsb);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            if(ncsb.getBSet().isEmpty()) setFinal(id);
            return newState;
        }
    }

    @Override
    public String getName() {
        return "ComplementNcsb";
    }
    
    public StateNcsb getStateNcsb(int id) {
        return (StateNcsb) getState(id);
    }
    

    public void setDetStates(int state) {
        mDetStates.set(state);
        mNondetStates.andNot(mDetStates);
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
        System.out.println(buchi.getDetStatesAfterFinals());
        Options.mEnhancedSliceGuess = true;
        ComplementNcsb complement = new ComplementNcsb(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        System.out.println(complement.toBA());
        
        IBuchi result = (new Remove(complement)).getResult();
        
        System.out.println(result.toDot());
        
        System.out.println(result.toBA());
        
        
        buchi = new Buchi(2);
        
        buchi.addState();
        buchi.addState();
        buchi.addState();
        
        buchi.getState(0).addSuccessor(0, 1);
        buchi.getState(0).addSuccessor(1, 2);
        buchi.getState(1).addSuccessor(0, 1);
        buchi.getState(1).addSuccessor(1, 1);
        
        buchi.getState(2).addSuccessor(0, 2);
        buchi.getState(2).addSuccessor(1, 2);
        
        buchi.setFinal(1);
        buchi.setInitial(0);
        
        complement = new ComplementNcsb(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        
        buchi = new Buchi(2);
        
        buchi.addState();
        buchi.addState();
        buchi.addState();
        buchi.addState();
        buchi.addState();
        buchi.addState();
        
        int q0 = 0;
        int q0a = 1;
        int q0b = 2;
        
        int q1 = 3;
        int q1a = 4;
        int q1b = 5;
        
        buchi.getState(q0).addSuccessor(0, q0a);
        buchi.getState(q0).addSuccessor(1, q0b);
        buchi.getState(q0).addSuccessor(1, q1);
        
        buchi.getState(q1).addSuccessor(0, q1a);
        buchi.getState(q1).addSuccessor(1, q1b);
        buchi.getState(q1).addSuccessor(0, q0);
        
        buchi.getState(q0a).addSuccessor(0, q0a);
        buchi.getState(q0a).addSuccessor(1, q0a);
        
        buchi.getState(q0b).addSuccessor(0, q0b);
        buchi.getState(q0b).addSuccessor(1, q0b);
        
        buchi.getState(q1a).addSuccessor(0, q1a);
        buchi.getState(q1a).addSuccessor(1, q1a);
        
        buchi.getState(q1b).addSuccessor(0, q1b);
        buchi.getState(q1b).addSuccessor(1, q1b);
        
        
        
        buchi.setInitial(q0);
        buchi.setFinal(q0a);
        buchi.setFinal(q1b);
        
        System.out.println(buchi.toDot());
        
        complement = new ComplementNcsb(buchi);
        complement.setDetStates(q0b);
        complement.setDetStates(q1a);
        
        new Explore(complement);
        System.out.println(complement.toDot());
        
        ComplementNsbc complementNsbc = new ComplementNsbc(buchi);
        new Explore(complementNsbc);
        System.out.println(complementNsbc.toDot());
        
        System.out.println(complement.toBA());
        
        System.out.println(complementNsbc.toBA());

    }

    
    

}
