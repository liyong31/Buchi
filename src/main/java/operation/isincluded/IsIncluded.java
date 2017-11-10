package operation.isincluded;

import automata.IBuchi;
import operation.complement.Complement;
import operation.complement.StateNCSB;

public class IsIncluded implements IIsIncluded {
    
    protected final IBuchi mFstOperand;
    protected final IBuchi mSndOperand;
    protected final Complement mSndComplement;
    protected Boolean mResult;
    
    public IsIncluded(IBuchi fstOperand, IBuchi sndOperand) {
        if(fstOperand.getAlphabetSize() != sndOperand.getAlphabetSize()) {
            throw new UnsupportedOperationException("Minus: different alphabets");
        }
        mFstOperand = fstOperand;
        mSndOperand = sndOperand;
        mSndComplement = new Complement(sndOperand);
    }

    @Override
    public IBuchi getFirstOperand() {
        return mFstOperand;
    }

    @Override
    public IBuchi getSecondOperand() {
        return mSndOperand;
    }

    @Override
    public Boolean getResult() {
        return mResult;
    }

    @Override
    public Complement getSecondComplement() {
        return mSndComplement;
    }

    @Override
    public StateNCSB getComplementState(int state) {
        assert state >= 0 && state < mSndComplement.getStateSize();
        return (StateNCSB) mSndComplement.getState(state);
    }

}
