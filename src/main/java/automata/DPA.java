package automata;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

public class DPA extends DOA {
    private final AccPA mAcc;
    
    public DPA(int alphabetSize) {
        super(alphabetSize);
        this.mAcc = new AccPA();
    }

    @Override
    public AccPA getAcceptance() {
        return mAcc;
    }
    
    @Override
    public void toDot(PrintStream out, List<String> alphabet) {
        // output automata in dot
        out.print("digraph {\n");
        Collection<IS> states = getStates();
        for (IS state : states) {
            StateDA st = (StateDA)state;
            out.print("  " + state.getId() + " [label=\"s" + state.getId() 
            + " : " + this.getAcceptance().getColor(state.getId())+ "\" , shape = ");
            out.print("circle");

            out.print("];\n");
            st.toDot(out, alphabet);
        }
        
        out.print("  " + states.size() + " [label=\"\", shape = plaintext];\n");
        out.print("  " + states.size() + " -> " + this.getInitialState() + " [label=\"\"];\n");
        out.print("}\n\n");
    }

}
