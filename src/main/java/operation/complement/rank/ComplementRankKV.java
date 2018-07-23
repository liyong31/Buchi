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

package operation.complement.rank;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import automata.Buchi;
import automata.IBuchi;
import automata.IState;

import main.Options;
import operation.explore.Explore;

// valid for all nondeterministic Buchi automata

/**
 * 
 *  Weak alternating automata are not that weak
 *  by  Orna Kupferman and  Moshe Y. Vardi
 *  in ACM Transactions on Computational Logic
 *  
 *   A simple implementation for the rank-based complementation
 *   
 */

public class ComplementRankKV extends ComplementRank<StateRankKV> {
    
    public ComplementRankKV(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementRankKV";
    }

    @Override
    protected StateRankKV makeRankState(int id, LevelRanking lvlRank) {
        return new StateRankKV(this, id, lvlRank);
    }
    
    public static void main(String[] args) {
        Buchi buchi = new Buchi(2);
        IState aState = buchi.addState();
        IState bState = buchi.addState();
        
        aState.addSuccessor(0, aState.getId()); 
        aState.addSuccessor(1, aState.getId());     
        aState.addSuccessor(1, bState.getId());     
        bState.addSuccessor(1, bState.getId());
        
        buchi.setFinal(bState);
        buchi.setInitial(aState);
        
        System.out.println(buchi.toDot());
        
        ComplementRankKV complement = new ComplementRankKV(buchi);
//        new Explore(complement);
//        System.out.println(complement.toDot());
//        Remove rm = new Remove(complement);
//        System.out.println(rm.getResult().toDot());
//        System.out.println(rm.getResult().toBA());
        
        complement = new ComplementRankKV(buchi);
        Options.mLazyS = true;
        Options.mMinusOne = true;
        new Explore(complement);
        System.out.println(complement.toDot());
        System.out.println(complement.toBA());
        
        // now we use the complement as input
        buchi = new Buchi(2);
        aState = buchi.addState();
        bState = buchi.addState();
        
        aState.addSuccessor(1, aState.getId()); 
        aState.addSuccessor(0, bState.getId());     
        bState.addSuccessor(0, bState.getId());     
        bState.addSuccessor(1, aState.getId());
        
        buchi.setFinal(bState);
        buchi.setInitial(aState);
        
        complement = new ComplementRankKV(buchi);
        Options.mLazyS = true;
        new Explore(complement);
        System.out.println(complement.toDot());
        System.out.println(complement.toBA());
        
//        complement = new ComplementNBA(buchi);
//        Options.mLazyS = false;
//        new Explore(complement);
//        System.out.println(complement.toDot());
//        System.out.println(complement.toBA());
        buchi = getBuchi(5);
        PrintStream print;
        try {
            print = new PrintStream(new FileOutputStream("/home/liyong/Downloads/op.ba"));
            print.print(buchi.toBA());
            print.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        complement = new ComplementRankKV(buchi);
        Options.mLazyS = true;
        Options.mMinusOne = true;
        new Explore(complement);
        
        try {
            print = new PrintStream(new FileOutputStream("/home/liyong/Downloads/op2.ba"));
            print.print(complement.toBA());
            print.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        complement = new ComplementRankKV(buchi);
        Options.mLazyS = false;
        Options.mMinusOne = true;
        new Explore(complement);
        try {
            print = new PrintStream(new FileOutputStream("/home/liyong/Downloads/op1.ba"));
            print.print(complement.toBA());
            print.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
    }
    
    private static Buchi getBuchi(int n) {
        Buchi buchi = new Buchi(2);
        IState[] states = new IState[n + 1];
        for(int i = 1; i <= n; i ++) {
            states[i] = buchi.addState();
        }
        final int[] ap = new int[2];
        ap[0] = 0;
        ap[1] = 1;
        
        buchi.setInitial(states[1]);
        // accepting
        for(int i = 2; i <= n; i += 2) {
            buchi.setFinal(states[i]);
        }
        
        // transition
        for(int j = 1; j <= n - 2; j ++) {
            states[j].addSuccessor(ap[0], states[j+1].getId());
            states[j].addSuccessor(ap[1], states[j+1].getId());
        }
        states[1].addSuccessor(ap[0], states[1].getId());
        states[1].addSuccessor(ap[1], states[1].getId());
        states[n].addSuccessor(ap[0], states[n].getId());
        states[n].addSuccessor(ap[1], states[n].getId());
        states[n-1].addSuccessor(ap[1], states[n-1].getId());
        states[n-1].addSuccessor(ap[0], states[n].getId());
        
        return buchi;
        

    }

}
