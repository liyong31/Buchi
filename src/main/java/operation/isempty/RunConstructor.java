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

package operation.isempty;

import java.util.PriorityQueue;

import automata.IBuchi;
import automata.IState;
import automata.Run;


import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import util.ISet;
import util.UtilISet;


public class RunConstructor {
    
    private final IBuchi mBuchi;
    private final ISet mSources;
    private final ISet mTargets;
    private int mGoal;
    private Run mRun;
    private final TIntObjectMap<SuccessorInfo> mSuccInfo;
    private final boolean mGoalIsFinal;
    
    public RunConstructor(IBuchi buchi, ISet source
                  , ISet target, boolean goalIsFinal) {
        mBuchi = buchi;
        mSources = source;
        mTargets = target;
        mSuccInfo = new TIntObjectHashMap<>();
        mGoalIsFinal = goalIsFinal;
    }
    
    public Run getRun() {
        if(mRun == null) {
            search();
            construct();
        }
        return mRun;
    }
    
    int getGoalState() {
        return mGoal;
    }

    private void construct() {
        // construct the run and word
        SuccessorInfo currInfo = getSuccessorInfo(mGoal);
        mRun = new Run();
        while(! mSources.get(currInfo.mState)) {
            mRun.preappend(currInfo.mPreState, currInfo.mLetter, currInfo.mState);
            currInfo = getSuccessorInfo(currInfo.mPreState);
        }
    }
    
    private SuccessorInfo getSuccessorInfo(int state) {
        if(mSuccInfo.containsKey(state)) {
            return mSuccInfo.get(state);
        }
        SuccessorInfo succInfo = new SuccessorInfo(state);
        mSuccInfo.put(state, succInfo);
        return succInfo;
    }

    private void search() {
        PriorityQueue<SuccessorInfo> workList = new PriorityQueue<>();
        // input source states
        for(final int state : mSources) {
            SuccessorInfo succInfo = getSuccessorInfo(state);
            workList.add(succInfo);
            succInfo.mDistance = 0;
        }
        
        ISet visited = UtilISet.newISet();
        while(! workList.isEmpty()) {
            SuccessorInfo currInfo = workList.remove(); 
            if(visited.get(currInfo.mState)) {
                continue;
            }
            if(isGoalState(currInfo.mState)) {
                mGoal = currInfo.mState;
                break;
            }
            if(currInfo.unreachable()) {
                assert false : "Unreachable state";
            }
            // update distance of successors
            IState state = mBuchi.getState(currInfo.mState);
            for(final int letter : state.getEnabledLetters()) {
                for(final int succ : state.getSuccessors(letter)) {
                    SuccessorInfo succInfo = getSuccessorInfo(succ);
                    int distance = currInfo.mDistance + 1;
                    if(!visited.get(succ) && succInfo.mDistance > distance) {
                        // update distance
                        succInfo.mLetter = letter;
                        succInfo.mDistance = distance;
                        succInfo.mPreState = state.getId();
                        workList.remove(succInfo);
                        workList.add(succInfo);
                    }
                }
            }
        }
        
    }
    
    private boolean isGoalState(int state) {
        if(! mTargets.get(state))
            return false;
        if(mGoalIsFinal) {
            return mBuchi.isFinal(state);
        }
        return true;
    }
}
