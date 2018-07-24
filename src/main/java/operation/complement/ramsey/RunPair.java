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

package operation.complement.ramsey;

import java.util.Comparator;

// run pair <state, final>
public class RunPair implements Comparator<RunPair>, Comparable<RunPair> {
    
    private final int mState;
    private final boolean mIsFinal; // indicate whether the run visits final states
    
    public RunPair(int state, boolean f) {
        this.mIsFinal = f;
        this.mState = state;
    }
    
    public int getState() {
        return this.mState;
    }
    
    public boolean isFinal() {
        return this.mIsFinal;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(obj instanceof RunPair) {
            RunPair other = (RunPair)obj;
            return this.mState == other.mState
               && this.mIsFinal == other.mIsFinal;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.mState;
        result = prime * result + getBooleanInt();
        return result;
    }
    
    private int getBooleanInt() {
        return (this.mIsFinal ? 1 : 0);
    }
    
    private String getBooleanString() {
        return (this.mIsFinal ? "tt" : "ff");
    }

    @Override
    public int compare(RunPair arg0, RunPair arg1) {
        if(arg0.mState == arg1.mState) {
            return arg0.getBooleanInt() - arg1.getBooleanInt();
        }else {
            return arg0.mState - arg1.mState;
        }
    }
    
    @Override
    public String toString() {
        return "(" + mState + "," + getBooleanString() + ")";
    }

    @Override
    public int compareTo(RunPair arg1) {
        return this.compare(this, arg1);
    }

}
