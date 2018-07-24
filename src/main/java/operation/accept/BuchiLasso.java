/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi.
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

package operation.accept;

import java.util.ArrayList;
import java.util.List;

import automata.Buchi;
import automata.IState;

class BuchiLasso extends Buchi {
    
    private final ArrayList<Integer> mStem;
    private final ArrayList<Integer> mLoop;
    
    public BuchiLasso(int apSize, List<Integer> stem, List<Integer> loop) {
        super(apSize);
        mStem = new ArrayList<>();
        mLoop = new ArrayList<>();
        for(final Integer letter : stem) {
            assert letter >= 0 && letter < apSize;
            mStem.add(letter);
        }
        for(final Integer letter : loop) {
            assert letter >= 0 && letter < apSize;
            mLoop.add(letter);
        }
        initialize();
    }

    private void initialize() {
        //add all states
        for(int i = 0; i <= getSum(); i ++) {
            IState state = addState();
            assert state.getId() == i;
            state.addSuccessor(getNextLetter(state.getId())
                    , getNextState(state.getId()));
            if(isFinalState(state.getId())) {
                this.setFinal(state.getId());
            }
        }
        this.setInitial(0);
    }
    
    private int getSum() {
        return mStem.size() + mLoop.size();
    }
    
    private int getNextLetter(int state) {
        assert state >= 0 && state <= getSum();
        if(state < mStem.size()) {
            return mStem.get(state);
        }
        if(state < getSum()) {
            return mLoop.get(state - mStem.size());
        }
        return mLoop.get(0);
    }
    
    private int getNextState(int state) {
        assert state >= 0 && state <= getSum();
        if(state < getSum()) {
            return state + 1;
        }
        return mStem.size() + 1;
    }
    
    private boolean isFinalState(int state) {
        assert state >= 0 && state <= getSum();
        return state > mStem.size();
    }

}
