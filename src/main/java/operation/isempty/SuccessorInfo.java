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

import java.util.Comparator;

class SuccessorInfo implements Comparator<SuccessorInfo>{
    final int mState;
    int mPreState;
    int mLetter;
    int mDistance;
    
    SuccessorInfo(int state) {
        mState = state;
        mDistance = Integer.MAX_VALUE;
    }
    
    boolean unreachable() {
        return mDistance == Integer.MAX_VALUE;
    }

    @Override
    public int compare(SuccessorInfo arg0, SuccessorInfo arg1) {
        return arg0.mDistance - arg1.mDistance;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(! (obj instanceof SuccessorInfo)) {
            return false;
        }
        SuccessorInfo other = (SuccessorInfo)obj;
        return other.mState == mState;
    }
    
    @Override
    public String toString() {
        return "<" + mState + "," + mPreState + "," + mLetter + "," + mDistance + ">";
    }
    
}
