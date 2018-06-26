package automata;

import java.util.Set;

/**
 * State Interface
 * */
public interface IS {
    
    int getId();
    
    void addSuccessor(int letter, int state);
    
    Set<Integer> getEnabledLetters();

}
