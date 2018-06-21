package operation.complement.tuple;

import automata.IBuchi;
import operation.complement.Complement;

// tuple-based complementation

public class ComplementTuple extends Complement {

    public ComplementTuple(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementTuple";
    }
    
    

}
