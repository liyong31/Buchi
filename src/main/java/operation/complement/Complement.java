package operation.complement;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

import automata.Buchi;
import automata.IBuchi;
import automata.IState;
import operation.IUnaryOp;
import operation.explore.Explore;

public abstract class Complement extends Buchi implements IUnaryOp<IBuchi, IBuchi> {

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
    
    public void explore() {
        new Explore(getResult());
    }
    
    @Override
    public void toDot(PrintStream out, List<String> alphabet) {

        // output automata in dot
        out.print("digraph {\n");
        Collection<IState> states = getStates();
        for (IState state : states) {
            out.print("  " + state.getId() + " [label=\"" + state + "\" , shape = ");
            if (isFinal(state.getId()))
                out.print("doublecircle");
            else
                out.print("circle");

            out.print("];\n");
            state.toDot(out, alphabet);
        }
        out.print("  " + states.size() + " [label=\"\", shape = plaintext];\n");
        for (final int init : getInitialStates()) {
            out.print("  " + states.size() + " -> " + init + " [label=\"\"];\n");
        }

        out.print("}\n\n");
    }

}
