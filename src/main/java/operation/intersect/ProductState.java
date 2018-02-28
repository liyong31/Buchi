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

import automata.State;
import util.ISet;
import util.UtilISet;

class ProductState extends State {

    private final Intersect mIntersect;
    private final int mFstState;
    private final int mSndState;
    private final TrackNumber mTrack;
    private final TrackNumber mSuccTrack;
    
    public ProductState(Intersect intersect, int id
            , int fstState, int sndState, TrackNumber track) {
        super(id);
        this.mIntersect = intersect;
        this.mFstState = fstState;
        this.mSndState = sndState;
        this.mTrack = track;
        this.mSuccTrack = getSuccStateTrack();
    }
    
    boolean contentEq(ProductState other) {
        return getFirstState() == other.getFirstState()
                && getSecondState() == other.getSecondState()
                && getTrackNumber() == other.getTrackNumber();
    }
    
    int getFirstState() {
        return mFstState;
    }
    
    int getSecondState() {
        return mSndState;
    }
    
    TrackNumber getTrackNumber() {
        return mTrack;
    }
    
    /**
     * current state is in TRACK_ONE
         * If fst is final and the snd is not, then we jump to TRACK_TWO to wait snd to be final
         * If fst and snd are both final, we already see final states in both operands, stay in track one
     * current state is in TRACK_TWO: means that we already saw fst final before
         * If snd is final, then we jump to TRACK_ONE to see fst final states
         * if snd is not final, then we stay in TRACK_TWO
     *    */
    TrackNumber getSuccStateTrack() {
        boolean fstAcc = mIntersect.getFirstOperand().isFinal(mFstState);
        boolean sndAcc = mIntersect.getSecondOperand().isFinal(mSndState);
        TrackNumber succTrack;
        if (getTrackNumber().isOne()) {
            if (fstAcc && !sndAcc) {
                succTrack = TrackNumber.TRACK_TWO;
            } else {
                succTrack = TrackNumber.TRACK_ONE;
            }
        } else {
            assert getTrackNumber().isTwo();
            if (sndAcc) {
                succTrack = TrackNumber.TRACK_ONE;
            } else {
                succTrack = TrackNumber.TRACK_TWO;
            }
        }
        return succTrack;
    }
    
    
    private final ISet mVisitedLetters = UtilISet.newISet();
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        // compute successors
        ISet fstSuccs = mIntersect.getFirstOperand().getState(mFstState).getSuccessors(letter);
        ISet sndSuccs = mIntersect.getSecondOperand().getState(mSndState).getSuccessors(letter);
        final ISet succs = UtilISet.newISet();
        for(final Integer fstSucc : fstSuccs) {
            for(final Integer sndSucc : sndSuccs) {
                // pair (X, Y)
                ProductState succ = mIntersect.getOrAddState(fstSucc, sndSucc, mSuccTrack);                
                this.addSuccessor(letter, succ.getId());
                succs.set(succ.getId());
            }
        }

        return succs;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof ProductState)) {
            return false;
        }
        ProductState other = (ProductState)obj;
        return this.contentEq(other);
    }
    
    @Override
    public String toString() {
        return "(" + mFstState + "," + mSndState + "):" + mTrack;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;
        hashCode = prime * hashCode + mFstState;
        hashCode = prime * hashCode + mSndState;
        hashCode += mTrack == TrackNumber.TRACK_ONE ? 1 : 2;
        return hashCode;
    }

}
