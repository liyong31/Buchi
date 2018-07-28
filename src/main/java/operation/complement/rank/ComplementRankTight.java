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

import automata.Buchi;
import automata.IBuchi;
import main.Options;
import operation.complement.ncsb.ComplementNcsb;
import operation.removal.Remove;

/**
 * 
 *  "Büchi Complementation Made Tighter"
 *  by  Ehud Friedgut, Orna Kupferman and Moshe Y. Vardi
 *  in ATVA 2004
 *  <br>
 *  "Büchi Complementation Made Tight"
 *  by  Sven Schewe
 *  in STACS 2009
 *  <br>
 *  by default we only allow tight rankings
 */

public class ComplementRankTight extends ComplementRank<StateRankTight> {

    public ComplementRankTight(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementRankTight" + 
                (Options.mTurnwise ? "+turnwise" : "") +
                (Options.mReduceOutdegree? "+rmdegree" : "");
    }

    @Override
    protected StateRankTight makeRankState(int id, LevelRanking lvlRank) {
        return new StateRankTight(this, id, lvlRank);
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
        Options.mTightRank = true;
        Options.mMinusOne = true;
//        Options.mTurnwise = true;
        Options.mReduceOutdegree = true;
        Options.mLazyS = true;
        ComplementRankTight complement = new ComplementRankTight(buchi);
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
