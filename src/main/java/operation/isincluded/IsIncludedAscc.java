package operation.isincluded;

import java.util.Stack;

import automata.IBuchi;
import automata.IState;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import main.Options;
import operation.complement.Complement;

class IsIncludedAscc {
    
    protected final IBuchi mFstOperand;
    protected final Complement mSndComplement;
    protected int mFstFinalState;
    protected int mSndFinalState;
    protected IBuchi mProduct;
    protected Boolean mResult;
    
    IsIncludedAscc(IBuchi fstOperand, Complement sndComplement) {
        mFstOperand = fstOperand;
        mSndComplement = sndComplement;
        mFstFinalState = -1;
        mSndFinalState = -1;
    }
    
    private class Ascc {
        
        private int mDepth;
        private final Stack<AsccPair> mRootsStack;             // C99 's root stack
        private final Stack<AsccPair> mActiveStack;            // tarjan's stack
        private final TIntObjectMap<TIntObjectMap<AsccPair>> mDfsNum;
        private final Antichain mEmptyStates;
        
        
        AsccPair getOrAddAsccPair(int fst, int snd) {
            TIntObjectMap<AsccPair> sndMap = mDfsNum.get(fst);
            if(sndMap == null) {
                sndMap = new TIntObjectHashMap<>();
            }
            AsccPair pair = sndMap.get(snd);
            if(pair == null) {
                pair = new AsccPair(fst, snd, mSndComplement);
                sndMap.put(snd, pair);
            }
            mDfsNum.put(fst, sndMap);
            return pair;
        }
        
        public Ascc() {
            
            this.mRootsStack = new Stack<>();
            this.mActiveStack = new Stack<>();
            this.mDfsNum = new TIntObjectHashMap<>();
            this.mEmptyStates = new Antichain();
            
            for(int fstInit : mFstOperand.getInitialStates()) {
                for(int sndInit : mSndComplement.getInitialStates()) {
                    AsccPair pair = getOrAddAsccPair(fstInit, sndInit);
                    if(pair.mDfsnum == 0){
                        strongConnect(pair);
                    }
                }
            }
        }
        
        boolean strongConnect(AsccPair pair) {
            
            ++ mDepth;
            mRootsStack.push(pair);
            mActiveStack.push(pair);
            pair.mDfsnum = mDepth;
            pair.mCurrent = true;
            
            boolean notEmpty = false;
            
            IState fstState = mFstOperand.getState(pair.getFstState());
            IState sndState = mSndComplement.getState(pair.getSndState());
            
            for(int letter = 0; letter < mFstOperand.getAlphabetSize(); letter ++) {
                for(int fstSucc : fstState.getSuccessors(letter)) {
                    for(int sndSucc : sndState.getSuccessors(letter)) {
                        AsccPair succPair = getOrAddAsccPair(fstSucc, sndSucc);
                        // Antichain to check whether it is empty state
                        if(Options.mAntichain && mEmptyStates.covers(succPair)) {
                            continue;
                        }
                        if(succPair.mDfsnum == 0) {
                            notEmpty = strongConnect(succPair) || notEmpty;
                        }else if(succPair.mCurrent) {
                            // we have already seen it before, there is a loop
                            while(true) {
                                //pop element u
                                AsccPair currPair = mRootsStack.pop();
                                if(mFstOperand.isFinal(currPair.getFstState())) {
                                    mFstFinalState = currPair.getFstState();
                                }
                                if(mSndComplement.isFinal(currPair.getSndState())) {
                                    mSndFinalState = currPair.getFstState();
                                }
                                // found one accepting scc
                                if(mFstFinalState > 0 && mSndFinalState > 0) {
                                    notEmpty = true;
                                }
                                
                                if(currPair.mDfsnum <= succPair.mDfsnum) {
                                    mRootsStack.push(currPair); // push back
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            
            if(mRootsStack.peek().equals(pair)) {
                mRootsStack.pop();
                while(! mActiveStack.isEmpty()) {
                    AsccPair topPair = mActiveStack.pop();
                    if(! notEmpty) {
                        mEmptyStates.addAsccPair(topPair);
                    }
                }
            }
            
            return notEmpty;
        }
    }
    
}
