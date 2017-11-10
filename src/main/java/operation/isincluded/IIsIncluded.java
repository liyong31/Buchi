package operation.isincluded;

import automata.IBuchi;
import operation.IBinaryOp;
import operation.complement.Complement;
import operation.complement.StateNCSB;

public interface IIsIncluded extends IBinaryOp<IBuchi, Boolean>{
    Complement getSecondComplement();
    StateNCSB getComplementState(int state);
    default String getName() {
        return "IsIncluded";
    }
}