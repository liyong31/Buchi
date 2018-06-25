package operation.determinize;

public class Decker {
    
    private final int mState;
    private final int mLabel;
        
    public Decker(int state, int label) {
        this.mState = state;
        this.mLabel = label;
    }
    
    public int getState() {
        return mState;
    }
    
    public int getLabel() {
        return mLabel;
    }
    
    @Override
    public boolean equals(Object other) {
        if(this == other) return true;
        if(! (other instanceof Decker)) {
            return false;
        }
        Decker otherDecker = (Decker)other;
        return this.mState == otherDecker.mState
            && this.mLabel == otherDecker.mLabel;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.mState;
        result = prime * result + this.mLabel;
        return result;
    }
    
    @Override
    public String toString() {
        return "<s:" + this.mState + ", i:" +this.mLabel + ">"; 
    }
    
    public static final int EMPTY_DOWN_STATE = -1;

}

