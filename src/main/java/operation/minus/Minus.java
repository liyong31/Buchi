package operation.minus;

import automata.IBuchi;
import operation.complement.Complement;
import operation.intersect.Intersect;

public class Minus implements IMminus{

    private final IBuchi mFstOperand;
    private final IBuchi mSndOperand;
    private final IBuchi mSndComplement;
    private final IBuchi mResult;
    
    public Minus(IBuchi fstOperand, IBuchi sndOperand) {
        if(fstOperand.getAlphabetSize() != sndOperand.getAlphabetSize()) {
            throw new UnsupportedOperationException("Intersect: different alphabets");
        }
        mFstOperand = fstOperand;
        mSndOperand = sndOperand;
        mSndComplement = new Complement(sndOperand);
        mResult = new Intersect(mFstOperand, mSndComplement);
    }

    @Override
    public String getOperantionName() {
        return "Minus";
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
    public IBuchi getResult() {
        return mResult;
    }

    @Override
    public IBuchi getSecondComplement() {
        return mSndComplement;
    }

}
