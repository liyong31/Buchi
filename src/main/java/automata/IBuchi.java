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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.ISet;

import util.UtilISet;
import util.parser.gff.GFFFileParser;

/**
 * (generalized) Buchi automata
 */
public interface IBuchi {

    int getStateSize();

    int getAlphabetSize();

    IState addState();

    IState makeState(int id);

    int addState(IState state);

    IState getState(int id);

    ISet getInitialStates();

    ISet getFinalStates();
    
    default void makeComplete() {
        IState deadState = null;
        List<IState> states = new ArrayList<>();
        for(final IState state : getStates()) {
            states.add(state);
        }
        for(final IState state : states) {
            for (int letter = 0; letter < getAlphabetSize(); letter ++) {
                ISet succs = state.getSuccessors(letter);
                if(succs.cardinality() == 0) {
                    if(deadState == null) deadState = addState();
                    state.addSuccessor(letter, deadState.getId());
                }
            }
        }
        if(deadState != null) {
            for (int letter = 0; letter < getAlphabetSize(); letter ++) {
                deadState.addSuccessor(letter, deadState.getId());
            }
        }
    }

    default public boolean isInitial(IState s) {
        return isInitial(s.getId());
    }

    boolean isInitial(int id);

    default public boolean isFinal(IState s) {
        return isFinal(s.getId());
    }
    
    boolean isFinal(int id);

    default public void setInitial(IState s) {
        setInitial(s.getId());
    }

    void setInitial(int id);

    default public void setFinal(IState s) {
        setFinal(s.getId());
    }

    void setFinal(int id);

    Collection<IState> getStates();

    // printer

    default public String toDot() {
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

    default void toDot(PrintStream out, List<String> alphabet) {

        // output automata in dot
        out.print("digraph {\n");
        Collection<IState> states = getStates();
        for (IState state : states) {
            out.print("  " + state.getId() + " [label=\"" + state.getId() + "\" , shape = ");
            if (isFinal(state.getId()))
                out.print("doublecircle");
            else
                out.print("circle");

            out.print("];\n");
            state.toDot(out, alphabet);
        }
        out.print("  " + states.size() + " [label=\"\", shape = plaintext];\n");
        for (final int init : getInitialStates()) {
            out.print("  " + states.size() + " -> " + init + " [label=\"\"];\n");
        }

        out.print("}\n\n");
    }

    // use this function if automtaton is too large 
    default public void toGFF(PrintStream out, List<String> alphabet) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = dbFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            //add elements to Document
            Element rootElement = doc.createElement(GFFFileParser.STRUCTURE);
            rootElement.setAttribute("label-on", GFFFileParser.TRANSITION);
            rootElement.setAttribute(GFFFileParser.TYPE, "fa");
            // alphabet 
            Element alphaElem = doc.createElement(GFFFileParser.ALPHABET);
            alphaElem.setAttribute(GFFFileParser.TYPE, GFFFileParser.ALPHABET_TYPE_VALUE);
            for(String symbol : alphabet) {
                Element symbolElem = doc.createElement(GFFFileParser.ALPHABET_SYMBOL);
                symbolElem.setTextContent(symbol);
                alphaElem.appendChild(symbolElem);
            }
            rootElement.appendChild(alphaElem);
            // states and transitions
            Element stateSetElem = doc.createElement(GFFFileParser.STATESET);
            Element transitionSetElem = doc.createElement(GFFFileParser.TRANSITIONSET);
            int transitionId = 0;
            for(int i = 0; i < this.getStateSize(); i ++) {
                Element stateElem = doc.createElement(GFFFileParser.STATE);
                stateElem.setAttribute(GFFFileParser.STATE_SID, i + "");
                stateElem.setTextContent("");
                stateSetElem.appendChild(stateElem);
                for(int a = 0; a < this.getAlphabetSize(); a ++) {
                    for(int j : this.getState(i).getSuccessors(a)) {
                        Element transitionElem = doc.createElement(GFFFileParser.TRANSITION);
                        transitionElem.setAttribute(GFFFileParser.TRANSITION_ID, "" + transitionId);
                        Element fromElem = doc.createElement(GFFFileParser.TRANSITION_FROM);
                        fromElem.setTextContent("" + i);
                        Element toElem = doc.createElement(GFFFileParser.TRANSITION_TO);
                        toElem.setTextContent("" + j);
                        Element readElem = doc.createElement(GFFFileParser.TRANSITION_READ);
                        readElem.setTextContent("" + alphabet.get(a));
                        transitionElem.appendChild(fromElem);
                        transitionElem.appendChild(toElem);
                        transitionElem.appendChild(readElem);
                        transitionSetElem.appendChild(transitionElem);
                        transitionId ++;
                    }
                }
            }
            rootElement.appendChild(stateSetElem);
            rootElement.appendChild(transitionSetElem);
            Element initsElem = doc.createElement(GFFFileParser.INITIALSTATESET);
            for(int i : this.getInitialStates()) {
                Element initElem = doc.createElement(GFFFileParser.STATE_ID);
                initElem.setTextContent("" + i);
                initsElem.appendChild(initElem);
            }
            rootElement.appendChild(initsElem);
            Element accsElem = doc.createElement(GFFFileParser.ACCSTATESET);
            accsElem.setAttribute(GFFFileParser.TYPE, GFFFileParser.BUCHI);
            for(int i : this.getFinalStates()) {
                Element accElem = doc.createElement(GFFFileParser.STATE_ID);
                accElem.setTextContent("" + i);
                accsElem.appendChild(accElem);
            }
            rootElement.appendChild(accsElem);
            doc.appendChild(rootElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final int indent = 2;
//            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            final String url = "{http://xml.apache.org/xslt}indent-amount";
            transformer.setOutputProperty(url, "" + indent);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }
    
    
    default public String toGFF() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            List<String> alphabet = new ArrayList<>();
            for(int i = 0; i < getAlphabetSize(); i ++) {
                alphabet.add(i + "");
            }
            toGFF(new PrintStream(out), alphabet);
            return out.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }
    
    default public void toBA(PrintStream out, List<String> alphabet) {
        ISet initialStates = getInitialStates();
        Iterator<Integer> iter = initialStates.iterator();
        out.print("[" + iter.next() + "]\n");
        // output automata in BA (RABIT format)
        Collection<IState> states = getStates();
        for(IState state : states) {
            state.toBA(out, alphabet);
        }   
        for(final int fin : getFinalStates()) {
            out.print("[" + fin + "]\n");
        }
    }
    
    default public String toBA() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            List<String> alphabet = new ArrayList<>();
            for(int i = 0; i < getAlphabetSize(); i ++) {
                alphabet.add(i + "");
            }
            toBA(new PrintStream(out), alphabet);
            return out.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }
    
    default int getTransitionSize() {
        int num = 0;
        for(IState s : getStates()) {
            for(Integer letter : s.getEnabledLetters()) {
                num += s.getSuccessors(letter).cardinality();
            }
        }
        return num;
    }
    
    default public void toATS(PrintStream out, List<String> alphabet) {
        final String PRE_BLANK = "   "; 
        final String ITEM_BLANK = " ";
        final String LINE_END = "},";
        final String BLOCK_END = "\n" + PRE_BLANK + "}";
        final String TRANS_PRE_BLANK = PRE_BLANK + "   "; 
        out.println("FiniteAutomaton result = (");
        
        
        out.print(PRE_BLANK + "alphabet = {");
        for(int i = 0; i < this.getAlphabetSize(); i ++) {
            out.print(alphabet.get(i) + ITEM_BLANK);
        }
        out.println(LINE_END);
        
        // states
        Collection<IState> states = getStates();
        out.print(PRE_BLANK + "states = {");
        for(IState state : states) {
            out.print("s" + state.getId() + ITEM_BLANK);
        }   
        out.println(LINE_END);
        // initial states
        out.print(PRE_BLANK + "initialStates = {");
        for(final Integer id : getInitialStates()) {
            out.print("s" + id + ITEM_BLANK);
        }
        out.println(LINE_END);
        
        // final states
        out.print(PRE_BLANK + "finalStates = {");
        for(final Integer id : getFinalStates()) {
            out.print("s" + id + ITEM_BLANK);
        }
        out.println(LINE_END);
        
        // call transitions
        out.print(PRE_BLANK + "transitions = {");
        for(final IState state : states) {
            for(Integer letter : state.getEnabledLetters()) {
                for(final Integer succ : state.getSuccessors(letter)) {
                    out.print("\n" + TRANS_PRE_BLANK + "(s" + state.getId() + " " + alphabet.get(letter) + " s" + succ + ")" );
                }
            }
        }
        out.println(BLOCK_END);
            
        out.println(");");
    }
    
    // a Buchi automaton is semideterministic if all transitions after the accepting states are deterministic
    default boolean isSemiDeterministic() {
        return !getDetStatesAfterFinals().isEmpty();
    }
    
    default ISet getDetStatesAfterFinals() {
        ISet finIds = getFinalStates();
        LinkedList<IState> walkList = new LinkedList<>();
        
        // add final states to list
        for(final int fin : finIds) {
            walkList.addFirst(getState(fin));
        }
        
        ISet visited = UtilISet.newISet();
        while(! walkList.isEmpty()) {
            IState s = walkList.remove();
            if(visited.get(s.getId())) continue;
            visited.set(s.getId());
            for(int i = 0; i < getAlphabetSize(); i ++) {
                ISet succs = s.getSuccessors(i);
                if(succs.isEmpty()) continue;
                if(succs.cardinality() > 1) {
                    return UtilISet.newISet();
                }

                for(final int succ : succs) {
                    if(! visited.get(succ)) {
                        walkList.addFirst(getState(succ));
                    }
                }                
            }
        }
        
        return visited;
    }
    
    default boolean isDeterministic(int state) {
        LinkedList<IState> walkList = new LinkedList<>();
        
        walkList.addFirst(getState(state));
        
        ISet visited = UtilISet.newISet();
        while(! walkList.isEmpty()) {
            IState s = walkList.remove();
            if(visited.get(s.getId())) continue;
            visited.set(s.getId());
            for(int i = 0; i < getAlphabetSize(); i ++) {
                ISet succs = s.getSuccessors(i);
                if(succs.cardinality() > 1) return false;
                if(succs.isEmpty()) continue;
                Iterator<Integer> iter = succs.iterator();
                int succ = iter.next();
                if(! visited.get(succ)) {
                    walkList.addFirst(getState(succ));
                }
            }
        }
        
        return true;
    }

}
