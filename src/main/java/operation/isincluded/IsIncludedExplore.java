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

package operation.isincluded;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import automata.IBuchi;
import automata.Run;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import operation.complement.Complement;
import operation.complement.dba.ComplementDBA;
import operation.complement.ncsb.ComplementNcsbOtf;
import operation.complement.retrorank.ComplementRetrorank;
import util.ISet;
import util.PairXX;
import util.parser.ba.BAFileParser;

public class IsIncludedExplore {
    
    protected final IBuchi mFstOperand;
    protected final Complement mSndComplement;
    protected boolean mEmpty;
    private final int SHARP = -1;
    private Run mRun;
    
    public IsIncludedExplore(IBuchi fstOperand, IBuchi sndOperand) {
        mFstOperand = fstOperand;
        ISet inits = sndOperand.getInitialStates();
        boolean isDet = false;
        
        if(inits.cardinality() == 1) {
            int initState = inits.iterator().next();
            isDet = sndOperand.isDeterministic(initState);
        }
        if(isDet) {
            // has to make complete
            mSndComplement = new ComplementDBA(sndOperand);
        }else {
            boolean isSemiDet = sndOperand.isSemiDeterministic();
            if(isSemiDet) {
                mSndComplement = new ComplementNcsbOtf(sndOperand);
            }else {
                mSndComplement = new ComplementRetrorank(sndOperand);
            }
        }
        new AsccExplore();
    }
    
    public boolean isIncluded() {
        return mEmpty;
    }
    
    public PairXX<List<Integer>> getCounterexample() {
        if(!mEmpty) {
            int lastState = mRun.getLastState();
            List<Integer> prefix = new ArrayList<>();
            List<Integer> period = new ArrayList<>();
            
            int breakIndex = 0;
            while(breakIndex < mRun.stateSize()) {
                if(mRun.getStateAt(breakIndex) == lastState) {
                    break;
                }
                prefix.add(mRun.getLetterAt(breakIndex));
                breakIndex ++;
            }
            while(breakIndex + 1 < mRun.stateSize()) {
                period.add(mRun.getLetterAt(breakIndex));
                breakIndex ++;
            }
            return new PairXX<>(prefix, period);
        }
        return null;
    }
    
    protected class ProductState {
        int mFstState;
        int mSndState;
        int mResState;
        
        ProductState(int fstState, int sndState) {
            this.mFstState = fstState;
            this.mSndState = sndState;
        }
        
        @Override
        public int hashCode() {
            int code = 1;
            code = code * 31 + mFstState;
            code = code * 31 + mSndState;
            return code;
        }
        
        @Override
        public boolean equals(Object obj) {
            if(obj == null) return false;
            if(obj == this) return true;
            if(obj instanceof ProductState) {
                ProductState other = (ProductState)obj;
                return mFstState == other.mFstState
                    && mSndState == other.mSndState;
            }
            return false;
        }
        
        @Override
        public String toString() {
            return mResState + ":(" + mFstState + "," + mSndState + ")";
        }
    }
    
    private class Elem {
        ProductState mState;
        byte mLabel;
        Elem(ProductState s, byte l) {
            mState = s;
            mLabel = l;
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
    
    private class AsccExplore {
        
        private int mDepth;
        private final Stack<Elem> mSCCs;             // C99 's root stack
        private final Stack<Integer> mAct;           // tarjan's stack
        private final TIntIntMap mDfsNum;
        private final Map<ProductState, ProductState> mMap; 
        private int numStates;
        
        public AsccExplore() {
            mSCCs = new Stack<>();
            mAct = new Stack<>();
            mDfsNum = new TIntIntHashMap();
            mDepth = 0;
            mMap = new HashMap<>();
            numStates = 0;
            mEmpty = true;
            Set<ProductState> inits = initialize();
            for(ProductState init : inits) {
                if(! mEmpty) break;
                mPredMap.clear();
                addPredecessor(init.mResState, SHARP, SHARP);
                strongConnect(init);
            }
        }
        
        /**
         * predMap stores a state mapped to a linked list of how the run leads to itself
         * There is an order of those predecessors 
         * */
        private final TIntObjectMap<LinkedList<PredPair>> mPredMap = new TIntObjectHashMap<>();
        
        /**
         * 
         * */
        private void addPredecessor(int succ, int letter, int pred) {
            LinkedList<PredPair> predPair = mPredMap.get(succ);
            if(predPair == null) {
                predPair = new LinkedList<>();
            }
            predPair.addLast(new PredPair(pred, letter));
            mPredMap.put(succ, predPair);
        }
        

        private void extractRun(final int u) {
            mRun = new Run();
            int h = u;
            do {
                LinkedList<PredPair> list = mPredMap.get(h);
                PredPair pair = list.getLast();
                if(list.size() > 1) {
                    list.removeLast();
                }
                if(pair.mPred != SHARP)
                    mRun.preappend(pair.mPred, pair.mLetter, h);
                h = pair.mPred;
            }while(h != SHARP);
//            System.out.println(mRun);
        }

        void strongConnect(ProductState prod) {
            ++ mDepth;
            mDfsNum.put(prod.mResState, mDepth);
            mSCCs.push(new Elem(prod, getLabel(prod)));
            mAct.push(prod.mResState);
            
            for (int letter = 0; letter < mFstOperand.getAlphabetSize(); letter ++) {
                for(int fstSucc : mFstOperand.getState(prod.mFstState).getSuccessors(letter)) {
                    for(int sndSucc : mSndComplement.getState(prod.mSndState).getSuccessors(letter)) {
                        ProductState succ = getOrAddState(fstSucc, sndSucc);
                        if (!mDfsNum.containsKey(succ.mResState)) {
                            addPredecessor(succ.mResState, letter, prod.mResState);
                            strongConnect(succ);
                            if(! mEmpty) return ;
                        } else if (mAct.contains(succ.mResState)) {
                            // we have already seen it before, there is a loop
                            // probably there is one final state without self-loop
                            addPredecessor(succ.mResState, letter, prod.mResState);
                            byte B = 0;
                            ProductState u;
                            do {
                                Elem p = mSCCs.pop();
                                u = p.mState;
                                B |= p.mLabel;
                                if(B == 3) {
                                    mEmpty = false;
                                    extractRun(u.mResState);
                                    return;
                                }
                            }while(mDfsNum.get(u.mResState) > mDfsNum.get(succ.mResState));
                            mSCCs.push(new Elem(u, B));
                        }
                    }
                }
            }
            
            // if current number is done, then we should remove all 
            // active states in the same scc
            if(mSCCs.peek().mState.mResState == prod.mResState) {
                mSCCs.pop();
                int u = 0;
                do {
                    assert ! mAct.isEmpty() : "Act empty";
                    u = mAct.pop();
                    // remove all useless predecessors information of this SCC
                    mPredMap.remove(u);
                }while(u != prod.mResState);
            }
        }

        ProductState getOrAddState(int fst, int snd) {
            ProductState prod = new ProductState(fst, snd);
            if(mMap.containsKey(prod)) {
                return mMap.get(prod);
            }
            // no record for original states
            prod.mResState = numStates;
            mMap.put(prod, prod);
            ++ numStates;
            return prod;
        }
        
        byte getLabel(ProductState prod) {
            byte label = 0;
            if(mFstOperand.isFinal(prod.mFstState)) {
                label |= 1;
            }
            if(mSndComplement.isFinal(prod.mSndState)) {
                label |= 2;
            }
            return label;
        }
        
        private Set<ProductState> initialize() {
            Set<ProductState> inits = new HashSet<>();
            for(int fstInit : mFstOperand.getInitialStates()) {
                for(int sndInit : mSndComplement.getInitialStates()) {
                    ProductState prod = getOrAddState(fstInit, sndInit);
                    inits.add(prod);
                }
            }
            return inits;
        }
    }
    
    public static void main(String[] args) {
        
        BAFileParser gffParser =  new BAFileParser();
        gffParser.parse("/home/liyong/Downloads/RABIT244/Examples/mcsA.ba");
        IBuchi A = gffParser.getBuchi();
        gffParser.parse("/home/liyong/Downloads/RABIT244/Examples/mcsB.ba");
        IBuchi B = gffParser.getBuchi();
        
        IsIncludedExplore nn = new IsIncludedExplore(A, B);
        System.out.println(nn.isIncluded());
        System.out.println(nn.getCounterexample());
    }
    
}
