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

import java.util.Stack;

import automata.GeneralizedBuchi;
import automata.IGeneralizedBuchi;
import automata.IGeneralizedState;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import util.ISet;
import util.UtilISet;

public class OndraExplore extends Explore{

    Stack<AsccElem> mSCCs;
    Stack<Integer> mAct;
    TIntIntMap mDfsNum;
    int mCnt;
    ISet mEmp;
    ISet mQPrime;
    IGeneralizedBuchi mOperandGBA;
    Boolean mIsEmpty;
    
    public OndraExplore(IGeneralizedBuchi operand) {
        super(operand);
    }
    
    class AsccElem {
        int mState;
        ISet mLabel;
        AsccElem(int state, ISet label) {
            mState = state;
            mLabel = label;
        }
        
        public String toString() {
            return "(" + mState + "," + mLabel + ")";
        }
    }
    
    @Override
    public void explore() {
        
        mOperandGBA = (IGeneralizedBuchi) mOperand;
        
        mSCCs = new Stack<>();
        mAct = new Stack<>();
        mCnt = 0;
        mEmp = UtilISet.newISet();
        mQPrime = UtilISet.newISet();
        mDfsNum = new TIntIntHashMap();
        
        boolean is_nemp = false;
        
        for(final int init : mOperandGBA.getInitialStates()) {
            if(!mDfsNum.containsKey(init)) {
                boolean result = construct(init);
                is_nemp = result || is_nemp;
            }
        }
        
        mIsEmpty= !is_nemp;
        new Explore(mOperandGBA);
        for(int s : mEmp) {
            assert !mQPrime.get(s) : "Wrong state in mQPrime";
            // check whether this state can reach any accepting loop
            IGeneralizedBuchi gba = copyGba(mOperandGBA);
            gba.setInitial(s);
            Tarjan tarjan = new Tarjan(gba);
            assert tarjan.isEmpty() : "not empty language";
        }
    }
    
    private IGeneralizedBuchi copyGba(IGeneralizedBuchi operand) {
        IGeneralizedBuchi gba = new GeneralizedBuchi(operand.getAlphabetSize());
        gba.setAccSize(operand.getAccSize());
        for(int i = 0; i < operand.getStateSize(); i ++) {
            gba.addState();
        }
        // copy states
        for(int i = 0; i < operand.getStateSize(); i ++) {
            IGeneralizedState state = (IGeneralizedState) operand.getState(i);
            IGeneralizedState copy = (IGeneralizedState) gba.getState(i);
            for(int letter = 0; letter < operand.getAlphabetSize(); letter ++) {
                for(final int t : state.getSuccessors(letter)) {
                    copy.addSuccessor(letter, t);
                }
            }
            for(int index : state.getAccSet()) {
                copy.setFinal(index);
            }
        }
        return gba;
    }

    private boolean construct(int s) {
        IGeneralizedState state = (IGeneralizedState) mOperandGBA.getState(s);
        ++ mCnt;
        mDfsNum.put(s, mCnt);
        mSCCs.push(new AsccElem(s, state.getAccSet()));
        mAct.push(s);
        boolean is_nemp = false;
        for(int letter = 0; letter < mOperandGBA.getAlphabetSize(); letter ++) {
            for(final int t : state.getSuccessors(letter)) {
                if(mQPrime.get(t)) {
                    is_nemp = true;
                }else if(mEmp.get(t)) {
                    continue;
                }else if(! mAct.contains(t)) {
                    boolean result = construct(t);
                    is_nemp = result || is_nemp;
                }else {
                    ISet B = UtilISet.newISet();
                    int u;
                    do {
                        AsccElem p = mSCCs.pop();
                        u = p.mState;
                        B.or(p.mLabel);
                        if(B.cardinality() == mOperandGBA.getAccSize()) {
                            is_nemp = true;
                        }
                    }while(mDfsNum.get(u) > mDfsNum.get(t));
                    mSCCs.push(new AsccElem(u, B));
                }
            }
        }
        
        if(mSCCs.peek().mState == s) {
            mSCCs.pop();
            int u = 0;
            do {
                assert !mAct.isEmpty() : "Act empty";
                u = mAct.pop();
                if(is_nemp) {
                    mQPrime.set(u);
                }else {
                    mEmp.set(u);
                }
            }while(u != s);
        }
        
        return is_nemp;
    }

}
