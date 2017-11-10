package automata;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import util.ISet;
import util.UtilISet;

public class GbaToBa extends Buchi {

    private final IGba mGba;
    private final TObjectIntMap<TrackState> mStateMap;
    
    public GbaToBa(IGba gba) {
        super(gba.getAlphabetSize());
        mGba = gba;
        mStateMap = new TObjectIntHashMap<>();
        computeInitialStates();
    }
    
    private void computeInitialStates() {
        for(int gbaState : mGba.getInitialStates()) {
            TrackState baState = getOrAddState(gbaState, BaToGba.ZERO);
            setInitial(baState.getId());
        }
    }
    
    private TrackState getOrAddState(int gbaState, int track) {
        TrackState trackState = new TrackState(gbaState, track, 0);
        if(mStateMap.containsKey(trackState)) {
            return (TrackState) getState(mStateMap.get(trackState));
        }
        TrackState newState = new TrackState(gbaState, track, getStateSize());
        int id = addState(newState);
        mStateMap.put(newState, id);
        final boolean isFinal = (track == BaToGba.ZERO) && (mGba.isFinal(gbaState, BaToGba.ZERO));
        if(isFinal) setFinal(id);
        return newState;
    }

    //we move to next track until we see state finishing current track 
    private int getSuccTrack(TrackState pair, int succ) {
        final int track = (pair.getTrack() + 1) % mGba.getAccSize();
        if(mGba.isFinal(succ, pair.getTrack())) {
            return track;
        }else {
            return pair.getTrack();
        }
    }
    
    
    class TrackState extends State{
        
        int mState;
        int mTrack;
        
        public TrackState(int state, int track, int id) {
            super(id);
            mState = state;
            mTrack = track;
        }
        
        int getState() {
            return mState;
        }
        
        int getTrack() {
            return mTrack;
        }
        
        @Override
        public boolean equals(Object obj) {
            if(obj == null) return false;
            if(this == obj) return true;
            if(!(obj instanceof TrackState)) {
                return false;
            }
            TrackState other = (TrackState)obj;
            return other.mState == mState
               && other.mTrack == mTrack;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = prime + mState;
            result = prime * result + mTrack;
            return result;
        }
        
        private ISet mVisitedLetters = UtilISet.newISet();
        @Override
        public ISet getSuccessors(int letter) {
            if(mVisitedLetters.get(letter)) {
                return super.getSuccessors(letter);
            }
            mVisitedLetters.set(letter);
            // compute successors
            final IState state = mGba.getState(mState);
            final ISet succs = UtilISet.newISet();
            for(int succ : state.getSuccessors(letter)) {
                final int succTrack = getSuccTrack(this, succ);
                TrackState succTrackState = getOrAddState(succ, succTrack);
                this.addSuccessor(letter, succTrackState.getId());
                succs.set(succTrackState.getId());
            }
            return succs;
        }
        
    }

}
