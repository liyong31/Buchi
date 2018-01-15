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
    // there is an invariant that : P <= Q, so we can store
    // only P and Q\P
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
        int s = -1;
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
                ISet currQMinusP = UtilISet.newISet();
                s = computeSuccessor(letter, currP, currQMinusP, true);
            }
        }else {
            assert mP != null && mQ != null;
            s = computeSuccessor(letter, mP, mQ, mSDBA.isFinal(this.getId()));
        }
        
        // if there is successor
        if(s >= 0) {
            super.addSuccessor(letter, s);
            succs.set(s);
        }
        
        return succs;
    }
    
    // compute the successors of a pair <P, Q>
    private int computeSuccessor(int letter, ISet currP, ISet currQMinusP, boolean accepting) {
        // first get the successors of the second component Q
        ISet succQ = null;
        ISet succP = null;
        ISet succFromF = UtilISet.newISet();
        ISet succNotFromF = UtilISet.newISet();
        // get all states in 
        for(final int q : currP) {
            ISet succs = mOperand.getState(q).getSuccessors(letter);
            // first set it to d(Q/\F, a)
            if(mOperand.isFinal(q)) {
                succFromF.or(succs);
            }else {
                succNotFromF.or(succs);
            }
        }
        
        if(!accepting) {
            succP = succFromF;
            succP.or(succNotFromF);   
            succQ = UtilISet.newISet();
            for(final int p : currQMinusP) {
                ISet succs = mOperand.getState(p).getSuccessors(letter);
                if(mOperand.isFinal(p)) {
                    succP.or(succs);       // d(P, a) \/ d(Q/\F, a)   
                }else {
                    succQ.or(succs);       // d(Q\P, a)   
                }
            }
            succQ.andNot(succP);
        }else {
            assert currQMinusP.isEmpty() : "Not empty for Q\\P";
            succP = succFromF;          // P' = d(Q/\F, a)
            succNotFromF.andNot(succP); // d(Q, a) \ P' = d(Q\F, a) \P'
            succQ = succNotFromF;       
        }
        
        if(succP.isEmpty() && succQ.isEmpty()) {
            return -1;
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
        if(mInnerState >= 0 || other.mInnerState >= 0) {
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
