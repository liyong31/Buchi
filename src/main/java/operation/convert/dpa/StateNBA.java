package operation.convert.dpa;

import automata.DPA;
import automata.State;
import util.ISet;
import util.UtilISet;

public class StateNBA extends State {
    
    private final int mState;
    private final int mLabel;
    private final DPA2NBA mNBA;
    
    public StateNBA(DPA2NBA nba, int id, int state, int label) {
        super(id);
        this.mNBA = nba;
        this.mState = state;
        this.mLabel = label;
    }
    
    public int getState() {
        return mState;
    }
    
    public int getLabel() {
        return mLabel;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        // check whether current state has even parity
        DPA dpa = mNBA.getOperand();
        // original parity
        StateNBA bsucc = null;
        ISet bsuccs = UtilISet.newISet();
        
        int psucc = dpa.getState(mState).getSuccessor(letter);
        int succParity = dpa.getAcceptance().getColor(psucc);
        
        
        if(mLabel < 0) {
            //1. add successor state from the original DPA in the copy DPA
            bsucc = mNBA.getOrAddState(psucc, -1);
            System.out.println(toString() + " -> " + bsucc + " : " + letter);
            super.addSuccessor(letter, bsucc.getId());
            bsuccs.set(bsucc.getId());
            
            //2. check if we have to add a copy for successors with even parities
            if(succParity % 2 == 0) {
                bsucc = mNBA.getOrAddState(psucc, succParity);
                super.addSuccessor(letter, bsucc.getId());
                bsuccs.set(bsucc.getId());
                System.out.println(toString() + " -> " + bsucc + " : " + letter);
            }
        }else {
            //3. if currently the parity of successor is larger than the label
            if(succParity >= mLabel) {
                bsucc = mNBA.getOrAddState(psucc, mLabel);
                System.out.println(toString() + " -> " + bsucc + " : " + letter);
                super.addSuccessor(letter, bsucc.getId());
                bsuccs.set(bsucc.getId());
            }
        }
        
        return bsuccs;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(this == obj) return true;
        if(obj instanceof StateNBA) {
            StateNBA other = (StateNBA)obj;
            return this.mState == other.mState
               &&  this.mLabel == other.mLabel;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + mState;
        result = prime * result + mLabel;
        return result;
    }
    
    @Override
    public String toString() {
        return "s" + mState + (mLabel >= 0 ? (":" + mLabel) : "");
    }
    

}
