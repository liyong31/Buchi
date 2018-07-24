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

package automata;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
// deterministic Omega Automaton

public abstract class DOA implements IDA {

    private int mInitState;
        
    private final List<StateDA> mStates;
    
    private final int mAlphabetSize;
    
    public DOA(int alphabetSize) {
        this.mAlphabetSize = alphabetSize;
        this.mStates = new ArrayList<>();
    }
    
    @Override
    public int getStateSize() {
        return mStates.size();
    }

    @Override
    public int getAlphabetSize() {
        return mAlphabetSize;
    }

    @Override
    public StateDA addState() {
        int id = mStates.size();
        mStates.add(makeState(id));
        return mStates.get(id);
    }

    @Override
    public StateDA makeState(int id) {
        return new StateDA(id);
    }

    @Override
    public int addState(IS state) {
        int id = mStates.size();
        mStates.add((StateDA) state);
        return id;
    }

    @Override
    public StateDA getState(int id) {
        assert id < mStates.size();
        if(id < mStates.size()) {
            return mStates.get(id);
        }
        return null;
    }

    @Override
    public boolean isInitial(int id) {
        return id == mInitState;
    }

    @Override
    public void setInitial(int id) {
        mInitState = id;
    }

    @Override
    public Collection<IS> getStates() {
        return Collections.unmodifiableList(mStates);
    }

    @Override
    public void toDot(PrintStream out, List<String> alphabet) {
        
    }

    @Override
    public void toBA(PrintStream out, List<String> alphabet) {
        
    }

    @Override
    public String toBA() {
        return null;
    }

    @Override
    public int getTransitionSize() {
        return 0;
    }

    @Override
    public void toATS(PrintStream out, List<String> alphabet) {
        
    }

    @Override
    public boolean isSemiDeterministic() {
        return false;
    }

    @Override
    public boolean isDeterministic(int state) {
        return true;
    }

    @Override
    public int getInitialState() {
        return mInitState;
    }

    @Override
    public int getSuccessor(int state, int letter) {
        StateDA s = getState(state);
        return s.getSuccessor(letter);
    }

}
