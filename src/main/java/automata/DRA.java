package automata;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

public class DRA extends DOA {
    private final AccRA mAcc;
    
    public DRA(int alphabetSize) {
        super(alphabetSize);
        this.mAcc = new AccRA();
    }

    @Override
    public AccRA getAcceptance() {
        return mAcc;
    } 
    
    @Override
    public void toDot(PrintStream out, List<String> alphabet) {
        // output automata in dot
        out.print("digraph {\n");
        Collection<IS> states = getStates();
        for (IS state : states) {
            StateDA st = (StateDA)state;
            out.print("  " + state.getId() + " [label=\"s" + state.getId() + "\" , shape = ");
            out.print("circle");

            out.print("];\n");
            st.toDot(out, alphabet);
        }
        
        out.print("  " + states.size() + " [label=\"\", shape = plaintext];\n");
        out.print("  " + states.size() + " -> " + this.getInitialState() + " [label=\"\"];\n");
        out.print("}\n\n");
    }

}
