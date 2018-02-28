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
import java.util.Collection;
import java.util.List;

import util.ISet;

public interface IGeneralizedBuchi extends IBuchi {

    @Override
    default public void setFinal(int id) {
        throw new UnsupportedOperationException("GBA do not support GBA");
    }
    
    @Override
    default public boolean isFinal(int id) {
        return !getAccSet(id).isEmpty();
    }
    
    int getAccSize();

    void setAccSize(int size);
    
    void setFinal(int state, int index);
    
    ISet getAccSet(int state);

    default boolean isFinal(int state, int index) {
        return getAccSet(state).get(index);
    }
    
    @Override
    default void toDot(PrintStream out, List<String> alphabet) {

        // output automata in dot
        out.print("digraph {\n");
        Collection<IState> states = getStates();
        for (IState state : states) {
            out.print("  " + state.getId() + "[ shape = ");
            if (isFinal(state.getId())) {
                out.print("doublecircle, ");
                out.print("label=\"" + state.getId() + " : " + getAccSet(state.getId()) + "\"");
            }
            else {
                out.print("circle, ");
                out.print("label=\"" + state.getId() + "\"");
            }

            out.print("];\n");
            state.toDot(out, alphabet);
        }
        out.print("  " + states.size() + " [label=\"\", shape = plaintext];\n");
        for (final int init : getInitialStates()) {
            out.print("  " + states.size() + " -> " + init + " [label=\"\"];\n");
        }

        out.print("}\n\n");
    }
}
