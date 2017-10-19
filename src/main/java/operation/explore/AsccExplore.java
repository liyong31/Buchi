package operation.explore;

import automata.IBuchi;
import automata.IState;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import util.ISet;
import util.IntStack;
import util.UtilISet;

public class AsccExplore extends Explore {

    private final boolean mStopAfterNotEmpty;
    private ISet mAcceptedSCC;
    
    public AsccExplore(IBuchi operand, boolean stopAfterNotEmpty) {
        super(operand);
        mStopAfterNotEmpty = stopAfterNotEmpty;
    }
    
    protected String getName() {
        return "Ascc";
    }
    
    @Override
    protected void explore() {
        new Ascc();
    }
    
    private class Ascc {
        
        private int mDepth;
        private final IntStack mRootsStack;             // C99 's root stack
        private final IntStack mActiveStack;            // tarjan's stack
        private final TIntIntMap mDfsNum;
        private final ISet mCurrent;
                       
        public Ascc() {
            this.mRootsStack = new IntStack();
            this.mActiveStack = new IntStack();
            this.mDfsNum = new TIntIntHashMap();
            this.mCurrent = UtilISet.newISet();
            
            for(int init : mOperand.getInitialStates()) {
                if(! mDfsNum.containsKey(init)){
                    strongConnect(init);
                    if(terminate()) return ;
                }
            }
        }
        
        boolean terminate() {
            return mStopAfterNotEmpty && mAcceptedSCC != null;
        }
        
        void strongConnect(int n) {
            
            ++ mDepth;
            mDfsNum.put(n, mDepth);
            mRootsStack.push(n);
            mActiveStack.push(n);
            mCurrent.set(n);
            
            IState state = mOperand.getState(n);
            for(int letter = 0; letter < mOperand.getAlphabetSize(); letter ++) {
                for(int succ : state.getSuccessors(letter)) {
                    if(! mDfsNum.containsKey(succ)) {
                        strongConnect(succ);
                        if(terminate()) return ;
                    }else if(mCurrent.get(succ)) {
                        // we have already seen it before, there is a loop
                        ISet scc = UtilISet.newISet();
                        while(true) {
                            //pop element u
                            int u = mRootsStack.pop();
                            scc.set(u);
                            // found one accepting scc
                            if(mOperand.isFinal(u)) {
                                mAcceptedSCC = scc;
                            }
                            if(mDfsNum.get(u) <= mDfsNum.get(succ)) {
                                mRootsStack.push(u); // push back
                                break;
                            }
                        }
                        if(terminate()) return ;
                    }
                }
            }
            
            // if current number is done, 
            // then we should remove all 
            // active states in the same scc
            if(mRootsStack.peek() == n) {
                mRootsStack.pop();
                while(true) {
                    int u = mActiveStack.pop(); // Tarjan' Stack
                    mCurrent.clear(u);
                    if(u == n) break;
                }
            }
        }
        
    }
    
    public ISet getAcceptedScc() {
        return mAcceptedSCC;
    }
    

}
