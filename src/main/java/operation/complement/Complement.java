package operation.complement;

import automata.Buchi;
import automata.IBuchi;
import operation.IUnaryOp;
import operation.explore.Explore;

public class Complement extends Buchi implements IUnaryOp<IBuchi, IBuchi> {

    protected final IBuchi mOperand;
    
    public Complement(IBuchi operand) {
        super(operand.getAlphabetSize());
        this.mOperand = operand;
        computeInitialStates();
    }
    
    protected void computeInitialStates() {
        
    }

    @Override
    public IBuchi getOperand() {
        return mOperand;
    }


    @Override
    public IBuchi getResult() {
        return this;
    }

    @Override
    public String getName() {
        return "Complement";
    }
    
    public void explore() {
        new Explore(this);
    }

}
