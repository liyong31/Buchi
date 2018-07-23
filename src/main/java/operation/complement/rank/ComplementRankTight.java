package operation.complement.rank;

import automata.Buchi;
import automata.IBuchi;
import main.Options;
import operation.complement.ncsb.ComplementNcsb;

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
        return "ComplementRankTight";
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
        ComplementRankTight complement = new ComplementRankTight(buchi);
        complement.explore();
        System.out.println(complement.toDot());
        System.out.println(complement.toBA());
        
        ComplementNcsb complementNcsb = new ComplementNcsb(buchi);
        complementNcsb.explore();
        System.out.println(complementNcsb.toDot());
        System.out.println(complementNcsb.toBA());
        
    }
}
