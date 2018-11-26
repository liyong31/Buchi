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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import java.util.List;

/**
 * currently only supports state-based acceptance
 * 
 * */
public interface IAutomata {
    
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
