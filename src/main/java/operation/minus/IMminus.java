package operation.minus;

import automata.IBuchi;
import operation.IBinaryOp;

public interface IMminus extends IBinaryOp<IBuchi, IBuchi>{
    
    IBuchi getSecondComplement();

}
