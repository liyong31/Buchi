package operation.complement.rank;

import automata.Buchi;
import automata.IBuchi;
import operation.IUnaryOp;

public class ComplementNBA extends Buchi implements IUnaryOp<IBuchi, IBuchi> {

    private final IBuchi mOperand;
    
    public ComplementNBA(IBuchi operand) {
        super(operand.getAlphabetSize());
        this.mOperand = operand;
        
    }

    @Override
    public String getName() {
        return "ComplementNBA";
    }

    @Override
    public IBuchi getOperand() {
        return mOperand;
    }

    @Override
    public IBuchi getResult() {
        return this;
    } 

}
