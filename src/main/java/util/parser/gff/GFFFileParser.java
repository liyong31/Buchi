package util.parser.gff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import automata.Buchi;
import automata.IBuchi;
import automata.IState;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import util.parser.SingleParser;

public class GFFFileParser implements SingleParser {
    
    public final static String STRUCTURE = "structure";

    public final static String ALPHABET = "alphabet";
    public final static String TYPE = "type";
    public final static String ALPHABET_TYPE_VALUE = "classical";
    public final static String ALPHABET_SYMBOL = "symbol";
    
    public final static String STATESET = "stateSet";
    public final static String STATE = "state";
    public final static String STATE_SID = "sid";
    
    public final static String TRANSITIONSET = "transitionSet";
    public final static String TRANSITION = "transition";
    public final static String TRANSITION_ID = "tid";
    public final static String TRANSITION_FROM = "from";
    public final static String TRANSITION_TO = "to";
    public final static String TRANSITION_READ = "read";

    public final static String INITIALSTATESET = "initialStateSet";
    public final static String STATE_ID = "stateID";
    
    public final static String ACCSTATESET = "acc";
    
    public final static String BUCHI = "buchi";
    
    private List<String> mAlphabet;
    private Map<String, Integer> mAlphabetMap;
    private TIntIntMap mStateMap;
    private IBuchi mBuchi;
    
    public GFFFileParser() {
        this.mAlphabetMap = new HashMap<>();
        this.mStateMap = new TIntIntHashMap();
        this.mAlphabet = new ArrayList<>();
    }
    
    @Override
    public void parse(String file) {
        File xmlFile = new File(file);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        Document doc = null;
        try {
            docBuilder = dbFactory.newDocumentBuilder();
            doc = docBuilder.parse(xmlFile);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        // first check alphabet: classical
        parseAlphabet(doc);
        // states
        parseStateSet(doc);
        // transitions
        parseTransitionSet(doc);
        // initial states
        parseInitialStates(doc);
        // final states
        parseAccStates(doc);
    }
    
    private void parseAlphabet(Document doc) {
        assert doc != null;
        // first check alphabet: classical
        Element alphaElem = getElementByTagName(doc, GFFFileParser.ALPHABET);
//        System.out.println(alphaElem.toString());
        if(!alphaElem.getAttribute(GFFFileParser.TYPE).equals(GFFFileParser.ALPHABET_TYPE_VALUE)) {
            throw new UnsupportedOperationException("Not classical alphabet: " + alphaElem.getAttribute(GFFFileParser.TYPE));
        }
        NodeList symbols = alphaElem.getElementsByTagName(GFFFileParser.ALPHABET_SYMBOL);
        for(int item = 0; item < symbols.getLength(); item ++) {
            String symbol = symbols.item(item).getTextContent().trim();
            int index = this.mAlphabet.size();
            this.mAlphabet.add(symbol);
            this.mAlphabetMap.put(symbol, index);
        }
        // for buchi
        mBuchi = new Buchi(mAlphabet.size());
    }
    
    private void parseStateSet(Document doc) {
        assert doc != null;
        Element stateSet = getElementByTagName(doc, GFFFileParser.STATESET);
        NodeList states = stateSet.getElementsByTagName(GFFFileParser.STATE);
        for(int item = 0; item < states.getLength(); item ++) {
            Node node = states.item(item);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                String state = elem.getAttribute(GFFFileParser.STATE_SID).trim();
                // for buchi
                IState is = mBuchi.addState();
                this.mStateMap.put(Integer.parseInt(state), is.getId());
            }
        }
    }
    
    private void parseTransitionSet(Document doc) {
        assert doc != null;
        Element transitionSet = getElementByTagName(doc, GFFFileParser.TRANSITIONSET);
        NodeList transitions = transitionSet.getElementsByTagName(GFFFileParser.TRANSITION);
        for(int item = 0; item < transitions.getLength(); item ++) {
            Node node = transitions.item(item);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element elemTrans = (Element) node;
                Element elemFrom = getElementByTagName(elemTrans, GFFFileParser.TRANSITION_FROM);
                Element elemTo = getElementByTagName(elemTrans, GFFFileParser.TRANSITION_TO);
                Element elemRead = getElementByTagName(elemTrans, GFFFileParser.TRANSITION_READ);
                if(elemFrom == null || elemTo == null || elemRead == null) {
                    throw new UnsupportedOperationException("Invalid transition element");
                }
                int from = Integer.parseInt(elemFrom.getTextContent().trim());
                int to = Integer.parseInt(elemTo.getTextContent().trim());
                String symbol = elemRead.getTextContent().trim();
                mBuchi.getState(mStateMap.get(from)).addSuccessor(mAlphabetMap.get(symbol), mStateMap.get(to));
            }
        }
    }
    
    private void parseInitialStates(Document doc) {
        assert doc != null;
        Element stateSet = getElementByTagName(doc, GFFFileParser.INITIALSTATESET);
        NodeList states = stateSet.getElementsByTagName(GFFFileParser.STATE_ID);
        for(int item = 0; item < states.getLength(); item ++) {
            Node node = states.item(item);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                mBuchi.setInitial(Integer.parseInt(elem.getTextContent().trim()));
            }
        }
    }
    
    private void parseAccStates(Document doc) {
        assert doc != null;
        // have to check acceptance buchi
        Element stateSet = getElementByTagName(doc, GFFFileParser.ACCSTATESET);
        if(!stateSet.getAttribute(GFFFileParser.TYPE).equals(GFFFileParser.BUCHI)) {
            throw new UnsupportedOperationException("Not buchi acceptance: " + stateSet.getAttribute(GFFFileParser.TYPE));
        }
        NodeList states = stateSet.getElementsByTagName(GFFFileParser.STATE_ID);
        for(int item = 0; item < states.getLength(); item ++) {
            Node node = states.item(item);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                mBuchi.setFinal(Integer.parseInt(elem.getTextContent().trim()));
            }
        }
    }
    
    private Element getElementByTagName(Document doc, String tagName) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        for(int item = 0; item < nodeList.getLength(); item ++ ) {
            Node node = nodeList.item(item);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) node;
            }
        }
        throw new UnsupportedOperationException("No element " + tagName);
    }
    
    private Element getElementByTagName(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        for(int item = 0; item < nodeList.getLength(); item ++) {
            Node node = nodeList.item(item);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element)node;
            }
        }
        throw new UnsupportedOperationException("No element " + tagName);
    }
    

    @Override
    public List<String> getAlphabet() {
        return Collections.unmodifiableList(mAlphabet);
    }

    @Override
    public IBuchi getBuchi() {
        return mBuchi;
    }
    
    public static void main(String[] args) {
        GFFFileParser parser = new GFFFileParser();
        parser.parse("/home/liyong/tools/GOAL-20151018/new-s-20-r-1.00-f-0.10--1-of-100.gff");
        IBuchi buchi = parser.getBuchi();
        System.out.println(buchi.toDot());
        System.out.println(buchi.toGFF());
    }

}
