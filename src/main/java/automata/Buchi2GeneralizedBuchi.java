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

package automata;

import java.util.Collection;

import util.ISet;
import util.UtilISet;

/** an GBA wrapper for BA */
public class Buchi2GeneralizedBuchi implements IGeneralizedBuchi {

    public static final int ONE = 1;
    public static final int ZERO = 0;
    private final IBuchi mBuchi;
    
    public Buchi2GeneralizedBuchi(IBuchi buchi) {
        assert buchi != null;
        mBuchi = buchi;
    }

    @Override
    public int getAccSize() {
        return ONE;
    }
    
    @Override
    public void setAccSize(int size) {
    	throw new UnsupportedOperationException("Can't set accepting sets size for BAs");
    }

    @Override
    public ISet getAccSet(int state) {
        ISet acc = UtilISet.newISet();
        if(mBuchi.isFinal(state)) {
            acc.set(ZERO);
        }
        return acc;
    }

    @Override
    public void setFinal(int state, int index) {
    	throw new UnsupportedOperationException("Can't set accepting sets size for BAs");
    }

    @Override
    public boolean isFinal(int state, int index) {
        if(index < 0 || index > 0) return false;
        return mBuchi.isFinal(state);
    }

    @Override
    public int getStateSize() {
        return mBuchi.getStateSize();
    }

    @Override
    public int getAlphabetSize() {
        return mBuchi.getAlphabetSize();
    }

    @Override
    public IState addState() {
        return mBuchi.addState();
    }

    @Override
    public IState makeState(int id) {
        return mBuchi.makeState(id);
    }

    @Override
    public int addState(IState state) {
        return mBuchi.addState(state);
    }

    @Override
    public IState getState(int id) {
        return mBuchi.getState(id);
    }

    @Override
    public ISet getInitialStates() {
        return mBuchi.getInitialStates();
    }

    @Override
    public ISet getFinalStates() {
        return mBuchi.getFinalStates();
    }

    @Override
    public boolean isInitial(int id) {
        return mBuchi.isInitial(id);
    }

    @Override
    public void setInitial(int id) {
        mBuchi.setInitial(id);
    }

    @Override
    public Collection<IState> getStates() {
        return mBuchi.getStates();
    }

}
