package automata;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import java.util.List;

public interface IA {
    
    IAcc getAcceptance();
    
    int getStateSize();

    int getAlphabetSize();

    IS addState();

    IS makeState(int id);

    int addState(IS state);

    IS getState(int id);

    default boolean isInitial(IS s) {
        return isInitial(s.getId());
    }

    boolean isInitial(int id);

    default void setInitial(IS s) {
        setInitial(s.getId());
    }

    void setInitial(int id);

    Collection<IS> getStates();

    // printer

    default String toDot() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            List<String> alphabet = new ArrayList<>();
            for (int i = 0; i < getAlphabetSize(); i++) {
                alphabet.add(i + "");
            }
            toDot(new PrintStream(out), alphabet);
            return out.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }

    void toDot(PrintStream out, List<String> alphabet);

    // use this function if automtaton is too large 
    void toBA(PrintStream out, List<String> alphabet);
    
    String toBA();
    
    int getTransitionSize();
    
    public void toATS(PrintStream out, List<String> alphabet);
    
    // a Buchi automaton is semideterministic if all transitions after the accepting states are deterministic
    boolean isSemiDeterministic();
    
    boolean isDeterministic(int state);

}
