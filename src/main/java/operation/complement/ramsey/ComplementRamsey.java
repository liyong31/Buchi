package operation.complement.ramsey;

import automata.IBuchi;
import operation.complement.Complement;

public class ComplementRamsey extends Complement {

    public ComplementRamsey(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementRamsey";
    }

}
