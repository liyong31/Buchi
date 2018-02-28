package util.parser.ba;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import automata.IBuchi;
import util.PairXX;
import util.parser.ParserType;
import util.parser.SingleParser;
import util.parser.UtilParser;

public class BAFileParser implements SingleParser {
	
	IBuchi mBuchi;	
	private Map<String, Integer> mAlphabetMap;
	private Map<String, Integer> mStateMap;
	
	private Map<Integer, Set<PairXX<Integer>>> mTrans;
	private Set<Integer> mFinals;
	private int mInit;
	private List<String> mAlphabet;
	
	public BAFileParser() {
		this.mAlphabetMap = new HashMap<>();
		this.mStateMap = new HashMap<>();
		this.mTrans = new HashMap<>();
		this.mFinals = new HashSet<>();
		this.mAlphabet = new ArrayList<>();
	}
	
	@Override
	public IBuchi getBuchi() {
		return mBuchi;
	}
	
	protected void setInitial(String initStr) {
		if(!mStateMap.containsKey(initStr)) {
			mStateMap.put(initStr, mAlphabet.size());
		}
		mInit = mStateMap.get(initStr);
	}
	
	protected void setFinal(String finalStr) {
		if(mStateMap.containsKey(finalStr)) {
			mFinals.add(mStateMap.get(finalStr));
		}
	}
	
	protected void parseBegin() {
		
	}
	
	protected void parseEnd() {
		mBuchi = new automata.Buchi(mAlphabetMap.size());
		boolean allFinal = mFinals.isEmpty();
		for(int i = 0; i < mStateMap.size(); i ++) {
			mBuchi.addState();
			if(i == mInit) mBuchi.setInitial(i);
			if(allFinal || mFinals.contains(i)) mBuchi.setFinal(i);
		}
		
		for(Map.Entry<Integer, Set<PairXX<Integer>>> entry: mTrans.entrySet()) {
			int source = entry.getKey();
			Set<PairXX<Integer>> trans = entry.getValue();
			for(PairXX<Integer> tr : trans) {
				int letter = tr.getFirst();
				int target = tr.getSecond();
				mBuchi.getState(source).addSuccessor(letter, target);
			}
		}
		clear();
	}
	
	public void clear() {
		mStateMap.clear();
		mAlphabetMap.clear();
		mTrans.clear();
		mFinals.clear();
//		mAlphabet.clear();
	}
	
	protected Integer addLetter(String letterStr) {
		if(! mAlphabetMap.containsKey(letterStr)) {
			mAlphabetMap.put(letterStr, mAlphabet.size());
			mAlphabet.add(letterStr);
		}
		return mAlphabetMap.get(letterStr);
	}
	
	protected Integer addState(String stateStr) {
		if(! mStateMap.containsKey(stateStr)) {
			mStateMap.put(stateStr, mStateMap.size());
		}
		return mStateMap.get(stateStr);
	}
	
	public void addTransition(String sourceStr, String targetStr, String letterStr) {
		int letter = addLetter(letterStr);
		int source = addState(sourceStr);
		int target = addState(targetStr);
		
		Set<PairXX<Integer>> trans = mTrans.get(source);
		if(trans == null) {
			trans = new HashSet<>();
		}
		trans.add(new PairXX<>(letter, target));
		mTrans.put(source, trans);
	}
	
	public void parse(String file) {
		try {
			FileInputStream inputStream = new FileInputStream(new File(file));
			JBAParser parser = new JBAParser(inputStream);
			parser.parse(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String file = "/home/liyong/workspace-neon/SemiBuchi/target/buchi.ba";
		SingleParser parser = UtilParser.getSinleParser(ParserType.BA);
		parser.parse(file);
		System.out.println(parser.getBuchi().toDot());
		System.out.println(parser.getBuchi().toBA());
	}

	@Override
	public List<String> getAlphabet() {
		return Collections.unmodifiableList(mAlphabet);
	}

}
