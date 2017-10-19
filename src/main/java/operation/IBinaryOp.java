package operation;

public interface IBinaryOp<I, O> extends IOp {
    
    I getFirstOperand();
    
    I getSecondOperand();
    
    O getResult();

}
