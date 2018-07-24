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

import automata.StateDA;
import util.ISet;
import util.UtilISet;


public class StateDABg extends StateDA {

    private final Profile mProfile;
    private final DABg mDA;
    
    public StateDABg(DABg da, int id, Profile profile) {
        super(id);
        this.mDA = da;
        this.mProfile = profile;
    }
    
    private final ISet mVisitedLetters = UtilISet.newISet();
    @Override
    public int getSuccessor(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessor(letter);
        }
        mVisitedLetters.set(letter);
        Profile nextProfile = mProfile.getSuccessorProfile(letter);
        StateDABg nextState = mDA.getOrAddState(nextProfile);
        super.addSuccessor(letter, nextState.getId());
        return nextState.getId();
    }
    
    @Override
    public int hashCode() {
        return mProfile.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(obj instanceof StateDABg) {
            StateDABg other = (StateDABg)obj;
            return this.mProfile.equals(other.mProfile);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.mProfile.toString();
    }
    
    protected boolean isDisjointWith(StateDABg other) {
        return this.mProfile.isDisjointWith(other.mProfile);
    }
    
    protected boolean isProper(StateDABg other) {
        return this.mProfile.isProper(other.mProfile);
    }

}
