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
    private ISet mAcceptedScc;
    
    public AsccExplore(IBuchi operand, boolean stopAfterNotEmpty) {
        super(operand);
        mStopAfterNotEmpty = stopAfterNotEmpty;
    }
    
    @Override
    public String getName() {
        return "Ascc";
    }
    
    @Override
    protected void explore() {
        new Ascc();
    }
    
    private void addFinalStates(int state) {
        assert mOperand.isFinal(state);
        if(mAcceptedScc == null) {
            mAcceptedScc = UtilISet.newISet();
        }
        mAcceptedScc.set(state);
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
            return mStopAfterNotEmpty && mAcceptedScc != null;
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
                        // probably there is one final state without self-loop
                        while(true) {
                            //pop element u
                            int u = mRootsStack.pop();
                            // found one accepting scc
                            if(mOperand.isFinal(u)) {
                                addFinalStates(u);
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
        return mAcceptedScc;
    }
    

}
