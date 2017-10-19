package operation.explore;

import java.util.LinkedList;

import automata.IBuchi;
import automata.IState;
import operation.IUnaryOp;
import util.ISet;
import util.UtilISet;

public class Explore implements IUnaryOp<IBuchi, IBuchi> {
    
    protected final IBuchi mOperand;
    protected boolean mExplored = false;
    
    public Explore(IBuchi operand) {
        mOperand = operand;
        explore();
    }

    @Override
    public IBuchi getOperand() {
        return mOperand;
    }

    @Override
    public IBuchi getResult() {
        return mOperand;
    }
    
    protected void explore() {
        
        if(mExplored) return ;
        
        mExplored = true;
        
        LinkedList<IState> walkList = new LinkedList<>();
        for(int init : mOperand.getInitialStates()) {
            walkList.addFirst(mOperand.getState(init));
        }
        
        ISet visited = UtilISet.newISet();
        
        while(! walkList.isEmpty()) {
            IState s = walkList.remove();
            if(visited.get(s.getId())) continue;
            visited.set(s.getId());
            
            for(int letter = 0; letter < mOperand.getAlphabetSize(); letter ++) {
                for(int succ : s.getSuccessors(letter)) {
                    if(! visited.get(succ)) {
                        walkList.addFirst(mOperand.getState(succ));
                    }
                }
            }
        }
    }

    @Override
    public String getOperantionName() {
        return "DFS explore";
    }

}
