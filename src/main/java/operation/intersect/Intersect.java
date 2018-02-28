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

package operation.intersect;

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.IBinaryOp;

public class Intersect extends Buchi implements IBinaryOp<IBuchi, IBuchi>{
    
    private final IBuchi mFstOperand;
    private final IBuchi mSndOperand;
    private final TObjectIntMap<ProductState> mStateMap;
    
    public Intersect(IBuchi fstOperand, IBuchi sndOperand) {
        super(fstOperand.getAlphabetSize());
        if(fstOperand.getAlphabetSize() != sndOperand.getAlphabetSize()) {
            throw new UnsupportedOperationException("Intersect: different alphabets");
        }
        mFstOperand = fstOperand;
        mSndOperand = sndOperand;
        mStateMap = new TObjectIntHashMap<>();
        computeInitialStates();
    }

    private void computeInitialStates() {
        for(int fstInit : mFstOperand.getInitialStates()) {
            for(int sndInit : mSndOperand.getInitialStates()) {
                ProductState state = getOrAddState(fstInit
                        , sndInit, TrackNumber.TRACK_ONE);
                this.setInitial(state.getId());
            }
        }
    }
    
    protected ProductState getOrAddState(int fst, int snd, TrackNumber track) {
        ProductState state = new ProductState(this, 0, fst, snd, track);
        if(mStateMap.containsKey(state)) {
            return (ProductState) getState(mStateMap.get(state));
        }
        // add new state
        ProductState newState = new ProductState(this, getStateSize(), fst, snd, track);
        int id = this.addState(newState);
        mStateMap.put(newState, id);
        // whether it is accepting state
        final boolean isFinal = mFstOperand.isFinal(fst) && track.isOne();
        if(isFinal) setFinal(id);
        return newState;
    }

    @Override
    public IBuchi getFirstOperand() {
        return mFstOperand;
    }

    @Override
    public IBuchi getSecondOperand() {
        return mSndOperand;
    }

    @Override
    public IBuchi getResult() {
        return this;
    }

    @Override
    public String getName() {
        return "Intersect";
    }
    
    

}
