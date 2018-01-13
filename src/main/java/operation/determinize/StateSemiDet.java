package operation.determinize;

import automata.IBuchi;
import automata.State;
import operation.complement.NCSB;
import util.ISet;
import util.UtilISet;

/**
 * */
public class StateSemiDet extends State {
    
    protected int mInnerState;
    protected ISet mP;
    protected ISet mQ;
    protected final Semideterminize mSDBA;
    protected final IBuchi mOperand;

    public StateSemiDet(Semideterminize sdba, int id,
            int predId, ISet P, ISet Q) {
        super(id);
        this.mInnerState = predId;
        this.mP = P;
        this.mQ = Q;
        this.mOperand = sdba.getOperand();
        this.mSDBA = sdba;
    }
    
    public int getInnerState() {
        return mInnerState;
    }
    
    public ISet getP() {
        return mP;
    }
    
    public ISet getQ() {
        return mQ;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        ISet succs = UtilISet.newISet();
        if(mInnerState >= 0) {
            for(final int succ : mOperand.getState(mInnerState).getSuccessors(letter)) {
                StateSemiDet stateSD = mSDBA.getOrAddState(null, null, succ);
                succs.set(stateSD.getId());
                super.addSuccessor(letter, stateSD.getId());
            }
            // if it is a final state, we should also add the successor of ({f}, {f})
            if(mOperand.isFinal(mInnerState)) {
                ISet currP = UtilISet.newISet();
                currP.set(mInnerState);
                int s = computeSuccessor(letter, currP, currP);
                super.addSuccessor(letter, s);
                succs.set(s);
            }
        }else {
            assert mP != null && mQ != null;
            int s = computeSuccessor(letter, mP, mQ);
            super.addSuccessor(letter, s);
            succs.set(s);
        }
        
        return succs;
    }
    
    // compute the successors of a pair <P, Q>
    private int computeSuccessor(int letter, ISet currP, ISet currQ) {
        // first get the successors of the second component Q
        ISet succQ = UtilISet.newISet();
        ISet succP = UtilISet.newISet();
        for(final int q : currQ) {
            ISet succs = mOperand.getState(q).getSuccessors(letter);
            succQ.or(succs);
            // first set it to d(Q/\F, a)
            if(mOperand.isFinal(q)) {
                succP.or(succs);
            }
        }
        boolean isEq = currP.equals(currQ);
        
        if(!isEq) {
            // P'
            for(final int p : currP) {
                succP.or(mOperand.getState(p).getSuccessors(letter));
            }
        }
        StateSemiDet stateSD = mSDBA.getOrAddState(succP, succQ, -1);
        return stateSD.getId();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(!(obj instanceof StateSemiDet)) {
            return false;
        }
        StateSemiDet other = (StateSemiDet)obj;
        if(mInnerState >= 0) {
            return mInnerState == other.mInnerState; 
        }
        // in the deterministic part
        assert mP != null && mQ != null;
        return  mP.equals(other.mP)
             && mQ.equals(other.mQ);
    }
    
    @Override
    public String toString() {
        if(mInnerState >= 0) {
            return mInnerState + "";
        }else {
            return "<" + mP.toString() + "," + mQ.toString() + ">";
        }
    }
    

    @Override
    public int hashCode() {
        if(mInnerState >= 0) {
            return mInnerState;
        }else {
            int code = NCSB.hashValue(mP);
            code = code * 31 + NCSB.hashValue(mQ);
            return code;
        }
    }

}
