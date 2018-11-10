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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import automata.IBuchi;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import operation.complement.Complement;
import operation.complement.dba.ComplementDBA;
import operation.complement.ncsb.ComplementNcsbOtf;
<<<<<<< HEAD
import operation.complement.retrorank.ComplementRetrorank;
import util.ISet;
import util.parser.ba.BAFileParser;
import util.parser.gff.GFFFileParser;
=======
import operation.semideterminize.Semideterminize;
>>>>>>> c773792ff3740a320e6ea538bfb699443bf8a9c7

public class IsIncludedExplore {
    
    protected final IBuchi mFstOperand;
    protected final Complement mSndComplement;
//    protected int mFstFinalState;
//    protected int mSndFinalState;
//    protected IBuchi mProduct;
    protected boolean mEmpty;
    
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
                System.out.println("SDBA");
                mSndComplement = new ComplementNcsbOtf(sndOperand);
            }else {
                System.out.println("NBA");
                mSndComplement = new ComplementRetrorank(sndOperand);
            }
        }
        new AsccExplore();
    }
    
    public boolean isIncluded() {
        return mEmpty;
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
    
    private class AsccExplore {
        
        private int mDepth;
        private final Stack<Elem> mSCCs;             // C99 's root stack
        private final Stack<Integer> mAct;            // tarjan's stack
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
                strongConnect(init);
            }
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
                            strongConnect(succ);
                            if(! mEmpty) return ;
                        } else if (mAct.contains(succ.mResState)) {
                            // we have already seen it before, there is a loop
                            // probably there is one final state without self-loop
                            byte B = 0;
                            ProductState u;
                            do {
                                Elem p = mSCCs.pop();
                                u = p.mState;
                                B |= p.mLabel;
                                if(B == 3) {
                                    mEmpty = false;
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
                }while(u != prod.mResState);
            }
        }

        ProductState getOrAddState(int fst, int snd) {
            ProductState prod = new ProductState(fst, snd);
            if(mMap.containsKey(prod)) {
                return mMap.get(prod);
            }
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
    }
    
}
