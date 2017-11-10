package operation.isempty;

import automata.IBuchi;
import automata.IState;
import automata.LassoRun;
import automata.Run;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import operation.UnaryOp;
import util.ISet;
import util.UtilISet;

public class LassoRunExtractor extends UnaryOp<IBuchi, LassoRun> {
    private final ISet mScc;
    private Run mStem;
    private Run mLoop;
    private int mGoalState;
    
    public LassoRunExtractor(IBuchi operand, ISet scc) {
        super(operand);
        mScc = scc;
        constructStem();
        constructLoop();
        mResult = new LassoRun(mStem, mLoop);
    }
    
    private void constructLoop() {
        // we construct a loop from the goal state
        IState state = mOperand.getState(mGoalState);
        TIntIntMap letterMap = new TIntIntHashMap();
        ISet sources = UtilISet.newISet();
        mLoop = new Run();
        for(final int letter : state.getEnabledLetters()) {
            for(final int succ : state.getSuccessors(letter)) {
                if(mGoalState == succ) {
                    // found a self loop
                    mLoop.append(mGoalState, letter, mGoalState);
                    return ;
                }
                letterMap.put(succ, letter);
                sources.set(succ);
            }
        }
        // else we construct a path 
        ISet target = UtilISet.newISet();
        target.set(mGoalState);
        RunConstructor rc = new RunConstructor(mOperand, sources, target, false);
        mLoop = rc.getRun();
        assert sources.get(mLoop.getFirstState());
        mLoop.preappend(mGoalState, letterMap.get(mLoop.getFirstState())
                , mLoop.getFirstState());
    }


    private void constructStem() {
        RunConstructor rc = new RunConstructor(mOperand
                , mOperand.getInitialStates(), mScc, true);
        mStem = rc.getRun();
        mGoalState = rc.getGoalState();
    }

    @Override
    public String getName() {
        return "LassoRunExtractor";
    }
    
    
    
    

}
