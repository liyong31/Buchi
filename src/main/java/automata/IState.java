package automata;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;

import util.ISet;

public interface IState {
    
    int getId();
    
    boolean equals(Object otherState);
    
    int hashCode();
    
    String toString();
    
    void addSuccessor(int letter, int state);
    
    ISet getSuccessors(int letter);
    
    Set<Integer> getEnabledLetters();

    default void toBA(PrintStream printer, List<String> alphabet) {
        Set<Integer> enabledLetters = this.getEnabledLetters();
        for(Integer letter : enabledLetters) {
            for(Integer succ : this.getSuccessors(letter)) {
                printer.print(alphabet.get(letter) + ",[" + this.getId() + "]->[" + succ + "]\n");
            }
        }
    }

    
    default void toDot(PrintStream printer, List<String> alphabet) {
        Set<Integer> enabledLetters = this.getEnabledLetters();
        for(Integer letter : enabledLetters) {
            for(Integer succ : this.getSuccessors(letter)) {
                printer.print("  " + this.getId() + " -> " + succ + " [label=\"" + alphabet.get(letter) + "\"];\n");
            }
        }
    }

}
