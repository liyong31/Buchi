package operation.isempty;

import automata.IBuchi;
import automata.LassoRun;
import operation.UnaryOp;
import operation.explore.AsccExplore;

public class IsEmpty extends UnaryOp<IBuchi, Boolean>{
    private final AsccExplore mExplore;
    private LassoRun mAcceptedRun;
    
    public IsEmpty(IBuchi operand) {
        super(operand);
        mExplore = new AsccExplore(mOperand, true);
        mResult = mExplore.getAcceptedScc() == null;
    }

    @Override
    public String getOperantionName() {
        return "IsEmpty";
    }
    
    public LassoRun getAcceptedLassoRun() {
        if(!mResult && mAcceptedRun == null) {
            LassoRunExtractor lre = new LassoRunExtractor(mOperand, mExplore.getAcceptedScc());
            mAcceptedRun = lre.getResult();
        }
        return mAcceptedRun;
    }

}
