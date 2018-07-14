package operation.complement.ramsey;

import java.util.TreeSet;

//
public class RunSet extends TreeSet<RunPair>{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public boolean contains(int state, boolean f) {
        return super.contains(new RunPair(state, f));
    }

}
