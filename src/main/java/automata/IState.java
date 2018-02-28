/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi which is a simple version of SemiBuchi.
 * 
 * Buchi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Buchi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Buchi. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
