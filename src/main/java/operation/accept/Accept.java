package operation.accept;

import java.util.List;

import automata.IBuchi;
import operation.UnaryOp;
import operation.intersect.Intersect;
import operation.isempty.IsEmpty;

public class Accept extends UnaryOp<IBuchi, Boolean>{
    private final IsEmpty mIsEmpty;
    public Accept(IBuchi operand, List<Integer> stem, List<Integer> loop) {
        super(operand);
        IBuchi lasso = new BuchiLasso(operand.getAlphabetSize(), stem, loop);
        Intersect intersect = new Intersect(operand, lasso);
        mIsEmpty = new IsEmpty(intersect);
    }

    @Override
    public String getName() {
        return "Accept";
    }
    
    public Boolean getResult() {
        return !mIsEmpty.getResult();
    }

}
