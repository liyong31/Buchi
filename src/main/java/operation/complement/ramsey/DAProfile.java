package operation.complement.ramsey;

import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import automata.Buchi;
import automata.DOA;
import automata.IAcc;
import automata.IBuchi;
import automata.IS;
import automata.StateDA;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.explore.UtilExplore;
import util.ISet;
import util.UtilISet;


/**
 * deterministic automaton for Buchi automata to partition words in \Sigma^+ 
 * 
 * */

public class DAProfile extends DOA {

    private final TObjectIntMap<StateDAProfile> mStateIndices;
    private final IBuchi mOperand;
    
    
    public DAProfile(IBuchi operand) {
        super(operand.getAlphabetSize());
        this.mOperand = operand;
        this.mStateIndices = new TObjectIntHashMap<>();
        computeInitialState();
    }
    
    protected IBuchi getOperand() {
        return this.mOperand;
    }
    
    protected void computeInitialState() {
        Profile profile = new Profile(mOperand);
        StateDAProfile init = getOrAddState(profile);
        super.setInitial(init.getId());
    }

    @Override
    public IAcc getAcceptance() {
        return null;
    }
    
    public StateDAProfile getStateDAProfile(int id) {
        return (StateDAProfile) super.getState(id);
    }
    
    public StateDAProfile getOrAddState(Profile profile) {
        StateDAProfile state = new StateDAProfile(this, 0, profile);
        if(mStateIndices.containsKey(state)) {
            return getStateDAProfile(mStateIndices.get(state));
        }else {
            int index = getStateSize();
            state = new StateDAProfile(this, index, profile);
            int id = addState(state);
            mStateIndices.put(state, id);
            return state;
        }
    }
    
    /**
     * check whether the language Y<sub>ij</sub> intersects with the language of input
     * NBA where Y<sub>ij</sub> = X<sub>j</sub>(X<sub>j</sub>)<sup>w</sup> and X<sub>i</sub> = L(D)
     ***/
    public boolean isIntersectionEmpty(int i, int j) {
        return getStateDAProfile(i).isIntersectionEmpty(getStateDAProfile(j));
    }
    
    @Override
    public void toDot(PrintStream out, List<String> alphabet) {
        // output automata in dot
        out.print("digraph {\n");
        Collection<IS> states = getStates();
        for (IS state : states) {
            StateDA st = (StateDA)state;
            out.print("  " + state.getId() + " [label=\"s" + state.getId() 
            + " : " + state + "\" , shape = ");
            out.print("circle");

            out.print("];\n");
            st.toDot(out, alphabet);
        }
        
        out.print("  " + states.size() + " [label=\"\", shape = plaintext];\n");
        out.print("  " + states.size() + " -> " + this.getInitialState() + " [label=\"\"];\n");
        out.print("}\n\n");
    }
    
    public static void main(String[] args) {
        IBuchi buchi = new Buchi(2);
        buchi.addState();
        buchi.addState();
        
        buchi.getState(0).addSuccessor(0, 0);
        buchi.getState(0).addSuccessor(1, 0);
        buchi.getState(0).addSuccessor(1, 1);
        
        buchi.getState(1).addSuccessor(0, 0);
        buchi.getState(1).addSuccessor(0, 1);
        
        buchi.setFinal(1);
        buchi.setInitial(0);
        
        DAProfile po = new DAProfile(buchi);
        
        LinkedList<StateDA> walkList = new LinkedList<>();
        walkList.add(po.getState(po.getInitialState()));

        ISet visited = UtilISet.newISet();

        while (!walkList.isEmpty()) {
            StateDA s = walkList.remove();
            if (visited.get(s.getId()))
                continue;
            visited.set(s.getId());

            for (int letter = 0; letter < po.getAlphabetSize(); letter++) {
                int succ = s.getSuccessor(letter);
                if (!visited.get(succ)) {
                    walkList.addFirst(po.getState(succ));
                }
            }
        }
        System.out.println(po.toDot());
        
        buchi = new Buchi(2);
        buchi.addState();
        buchi.addState();
        buchi.addState();
        buchi.addState();
        
        buchi.getState(0).addSuccessor(0, 0);
        buchi.getState(0).addSuccessor(1, 0);
        buchi.getState(0).addSuccessor(1, 1);
        
        buchi.getState(1).addSuccessor(0, 2);
        buchi.getState(1).addSuccessor(1, 1);
        
        buchi.getState(2).addSuccessor(0, 3);
        buchi.getState(2).addSuccessor(1, 2);
        
        buchi.getState(3).addSuccessor(0, 3);
        buchi.getState(3).addSuccessor(1, 1);
        
        buchi.setFinal(3);
        buchi.setInitial(0);
        
        po = new DAProfile(buchi);
        UtilExplore.explore(po);
        System.out.println(po.toDot());
        
    }

}
