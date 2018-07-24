/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StateDA implements IS, Comparable<StateDA>{

    private final int mId;
    private final Map<Integer, Integer> mSuccessors;
    public StateDA(int id) {
        this.mId = id;
        this.mSuccessors = new HashMap<>();
    }
    
    @Override
    public int compareTo(StateDA other) {
        return mId - other.mId;
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public void addSuccessor(int letter, int state) {
        mSuccessors.put(letter, state);
    }
    
    public int getSuccessor(int letter) {
        Integer state = mSuccessors.get(letter);
        if(state == null) {
            return -1;
        }
        return state;
    }
    
    @Override
    public Set<Integer> getEnabledLetters() {
        return Collections.unmodifiableSet(mSuccessors.keySet());
    }
    
    @Override
    public int hashCode() {
        return mId;
    }
    
    @Override
    public String toString() {
        return "s" + mId;
    }
    
    public void toDot(PrintStream printer, List<String> alphabet) {
        Set<Integer> enabledLetters = this.getEnabledLetters();
        for(Integer letter : enabledLetters) {
             printer.print("  " + this.getId() + " -> " + getSuccessor(letter) + " [label=\"" + alphabet.get(letter) + "\"];\n");
        }
    }

}
