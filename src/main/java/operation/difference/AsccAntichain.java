package operation.difference;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import automata.LassoRun;
import automata.Run;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import operation.isempty.RunConstructor;
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
    LinkedList<LassoRun> mLassos;
    final int SHARP = -1;
    
    
    AsccAntichain(Difference difference) {
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
                addPredecessor(init, SHARP, SHARP);
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
        
        // now we construct all the accepting run
//        mLassos = new LinkedList<>();
//        for(Run loop : mLoops) {
//            ISet source = UtilISet.newISet();
//            source.or(mDifference.getInitialStates());
//            ISet target = UtilISet.newISet();
//            target.set(loop.getFirstState());
//            RunConstructor rc = new RunConstructor(mDifference, source, target, false);
//            Run stem = rc.getRun();
//            assert stem.getLastState() == loop.getFirstState();
//            mLassos.addLast(new LassoRun(stem, loop)); 
//        }
        
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
            if(letter == 0 && s == 12) {
                System.out.println("Hello");
            }
            for(final int t : prodS.getSuccessors(letter)) {
                ProductState prodT =  mDifference.getProductState(t);
                if(mQPrime.get(t)) {
                    is_nemp = true;
                }else if(mEmp.covers(prodT)) {
                    continue;
                }else if(!mAct.contains(t)) {
                    addPredecessor(t, letter, s);
                    boolean r = construct(t);
                    is_nemp = r || is_nemp;
                }else {
                    addPredecessor(t, letter, s);
                    ISet B = UtilISet.newISet();
                    int u;
                    do {
                        ElemPair pair = mSCCs.pop();
                        u = pair.mState;
                        B.or(pair.mLabel);
                        if(B.cardinality() == mDifference.getAccSize()) {
                            is_nemp = true;
                            System.out.println("State s" + u);
                            extractRun(u);
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
    
    private LinkedList<Run> mLoops = new LinkedList<>();

    private void extractRun(final int u) {
        Run loop = new Run();
        int h = u;
        do {
//            System.out.print("," + h);
            LinkedList<PredPair> list = mPredMap.get(h);
//            assert list != null: "empty predecessors";
            PredPair pair = list.getLast();
            if(list.size() > 1) {
                list.removeLast();
            }
            if(pair.mPred != SHARP)
                loop.preappend(pair.mPred, pair.mLetter, h);
            h = pair.mPred;
        }while(h != SHARP);
        System.out.println(loop);
        mLoops.addLast(loop);
    }

    class ElemPair {
        int mState;
        ISet mLabel;
        ElemPair(int state, ISet label) {
            mState = state;
            mLabel = label;
        }
        
        @Override
        public String toString() {
            return "(" + mState + ", " + mLabel + ")";
        }
    }
    
    class PredPair {
        int mPred;
        int mLetter;
        PredPair(int pred, int letter) {
            mPred = pred;
            mLetter = letter;
        }
        
        @Override
        public String toString() {
            return "(" + mPred + ", " + mLetter + ")";
        }
    }
    
    private final TIntObjectMap<LinkedList<PredPair>> mPredMap = new TIntObjectHashMap<>();
    
    private void addPredecessor(int succ, int letter, int pred) {
        LinkedList<PredPair> predPair = mPredMap.get(succ);
        if(predPair == null) {
            predPair = new LinkedList<>();
        }
        predPair.addLast(new PredPair(pred, letter));
        mPredMap.put(succ, predPair);
    }

}
