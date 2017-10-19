package operation;

public interface IUnaryOp<I, O> extends IOp {
    
    I getOperand();
    
    O getResult();
    


}
