package operation.complement.retrorank;

import automata.IBuchi;
import operation.complement.Complement;

/**
 * 
 *  Unifying Buchi Complementation Constuctions
 *  by  Seth Fogarty, Orna Kupferman, Moshe Y. Vardi and Thomas Wilke
 *  in LMCS
 */
public class ComplementRetrorank extends Complement {

    public ComplementRetrorank(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementRetrorank";
    }

}
