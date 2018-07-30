package operation.complement.nsbc;

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.hash.TObjectIntHashMap;
import main.Options;
import operation.complement.ncsb.ComplementNcsbOtf;
import operation.complement.rank.ComplementRank;
import operation.complement.rank.LevelRanking;
import operation.explore.Explore;
import operation.removal.Remove;
import util.ISet;
import util.UtilISet;

//TODO buggy complementation algorithm
public class ComplementRankNsbc extends ComplementRank<StateRankNsbc>{

    protected final ISet mDetStates;
    protected final ISet mNondetStates;
    
    public ComplementRankNsbc(IBuchi operand) {
        super(operand);
        this.mDetStates = operand.getDetStatesAfterFinals();
        this.mNondetStates = UtilISet.newISet();
        for(int s = 0; s < operand.getStateSize(); s ++) {
            if(!this.mDetStates.get(s)) {
                this.mNondetStates.set(s);
            }
        }
    }
    
    @Override
    protected void computeInitialStates() {
        // compute initial states
        mStateIndices = new TObjectIntHashMap<>();
        LevelRanking lvlRnk = new LevelRanking(false, false);
        lvlRnk.setS(mOperand.getInitialStates());
        StateRankNsbc stateLvlRnk = getOrAddState(lvlRnk);
        this.setInitial(stateLvlRnk.getId());
    }

    @Override
    protected StateRankNsbc makeRankState(int id, LevelRanking lvlRank) {
        return new StateRankNsbc(this, id, lvlRank);
    }
    
    @Override
    public String getName() {
        return "ComplementRankNsbc";
    }
    
    public static void main(String[] args) {
        IBuchi buchi = new Buchi(2);
        
        buchi.addState();
        buchi.addState();
//        buchi.addState();
        
        buchi.getState(0).addSuccessor(0, 0);
        buchi.getState(0).addSuccessor(1, 0);
        buchi.getState(0).addSuccessor(0, 1);
        buchi.getState(0).addSuccessor(1, 1);
        
//        buchi.getState(1).addSuccessor(0, 2);
        buchi.getState(1).addSuccessor(1, 1);
        
//        buchi.getState(2).addSuccessor(0, 2);
//        buchi.getState(2).addSuccessor(1, 2);
        
        buchi.setFinal(1);
        buchi.setInitial(0);
        
        System.out.println(buchi.toDot());
        System.out.println(buchi.getDetStatesAfterFinals());
        Options.mEagerGuess = true;
        ComplementNsbc complement = new ComplementNsbc(buchi);
        new Explore(complement);
        System.out.println(complement.toDot());
        System.out.println(complement.toBA());
        
        ComplementRankNsbc complementRank = new ComplementRankNsbc(buchi);
        new Explore(complementRank);
        System.out.println(complementRank.toDot());
        
        System.out.println(complementRank.toBA());
        
        
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
        
        complement = new ComplementNsbc(buchi);
        complement.setDetStates(2);
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
        
        complement = new ComplementNsbc(buchi);
        complement.setDetStates(q0b);
        complement.setDetStates(q1a);
        
        new Explore(complement);
        System.out.println(complement.toBA());
        System.out.println(complement.toDot());
        IBuchi temp = (new Remove(complement)).getResult();
        System.out.println(complement.toBA());
        
        ComplementNcsbOtf complementNcsb = new ComplementNcsbOtf(buchi);
        new Explore(complementNcsb);
        System.out.println(complementNcsb.toBA());

    }


}
