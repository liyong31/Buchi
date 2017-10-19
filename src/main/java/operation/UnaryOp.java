package operation;

public abstract class UnaryOp<I, O> implements IUnaryOp<I, O>{
    
    protected final I mOperand;
    protected O mResult;
    
    public UnaryOp(I operand) {
        mOperand = operand;
    }

    @Override
    public I getOperand() {
        return mOperand;
    }

    @Override
    public O getResult() {
        return mResult;
    }
    

}
