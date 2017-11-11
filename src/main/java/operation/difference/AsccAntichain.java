package operation.difference;

import java.util.Stack;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import util.ISet;
import util.UtilISet;

class AsccAntichain {
    
    Stack<ElemPair> mSCCs;
    Stack<Integer> mAct;
    TIntIntMap mDfsNum;
    Antichain mEmp;
    int mCnt;
    Difference mDifference;
    ISet mQPrime;
    Boolean mIsEmpty;
    
    public AsccAntichain(Difference difference) {
        mDifference = difference;
        mSCCs = new Stack<>();
        mAct = new Stack<>();
        mDfsNum = new TIntIntHashMap();
        mCnt = 0;
        mEmp = new Antichain();
        mQPrime = UtilISet.newISet();
        
        boolean is_nemp = false;
        for(final int init : difference.getInitialStates()) {
            if(! mDfsNum.containsKey(init)) {
                final boolean result = construct(init);
                is_nemp = result || is_nemp;
            }
        }
        mIsEmpty = ! is_nemp;
        
        for(int s = 0; s < mDifference.getStateSize(); s ++) {
            ProductState prodS = mDifference.getProductState(s);
            if(mQPrime.get(s)) {
                assert !mEmp.covers(prodS) : "Wrong coverage in mQPrime " + prodS + "\n";
            }else {
                assert mEmp.covers(prodS) : "Wrong coverage in Antichain \n";
            }
        }
    }
    
    private ISet getLabel(int state) {
        return mDifference.getAccSet(state);
    }
    
    private boolean construct(int s) {
        mCnt ++;
        mDfsNum.put(s, mCnt);
        mSCCs.push(new ElemPair(s, getLabel(s)));
        mAct.push(s);
        boolean is_nemp = false;
        ProductState prodS = mDifference.getProductState(s);
        for(int letter = 0; letter < mDifference.getAlphabetSize(); letter ++ ) {
            for(final int t : prodS.getSuccessors(letter)) {
                ProductState prodT =  mDifference.getProductState(t);
                if(mQPrime.get(t)) {
                    is_nemp = true;
                }else if(mEmp.covers(prodT)) {
                    continue;
                }else if(!mAct.contains(t)) {
                    boolean r = construct(t);
                    is_nemp = r || is_nemp;
                }else {
                    ISet B = UtilISet.newISet();
                    int u;
                    do {
                        ElemPair pair = mSCCs.pop();
                        u = pair.mState;
                        B.or(pair.mLabel);
                        if(B.cardinality() == mDifference.getAccSize()) {
                            is_nemp = true;
                        }
                    }while(mDfsNum.get(u) > mDfsNum.get(t));
                    mSCCs.push(new ElemPair(u, B));
                }
            }
        }
        
        if(mSCCs.peek().mState == s) {
            mSCCs.pop();
            int u = 0;
            do {
                assert ! mAct.isEmpty() : "mAct is empty\n";
                u = mAct.pop();
                if(is_nemp) {
                    mQPrime.set(u);
                }else {
                    mEmp.addProductState(mDifference.getProductState(u));
                }
            }while(u != s);
        }
        
        return is_nemp;
    }

    class ElemPair {
        int mState;
        ISet mLabel;
        ElemPair(int state, ISet label) {
            mState = state;
            mLabel = label;
        }
        
        public String toString() {
            return "(" + mState + ", " + mLabel + ")";
        }
    }

}
