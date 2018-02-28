package util.parser.ats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import automata.IBuchi;
import util.PairXX;
import util.parser.DoubleParser;
import util.parser.SingleParser;



// now we only support ATS format of Ultimate tools
// every automaton in the same file share the SAME alphabets

public class ATSFileParser implements DoubleParser, SingleParser {
	
	private List<String> mAlphabets;
	private List<PairXX<IBuchi>> mBuchiPairs;
	
	private Map<String, Integer> mAlphabetMap;
	private Map<String, Integer> mStateMap;
	
	
	public ATSFileParser() {
		this.mAlphabets = new ArrayList<>();
		this.mBuchiPairs = new ArrayList<>();
		this.mAlphabetMap = new HashMap<>();
		this.mStateMap = new HashMap<>();
	}
	
	protected void addPair(PairXX<IBuchi> pair) {
		mBuchiPairs.add(pair);
	}
	
	protected void addPair(IBuchi fstBuchi, IBuchi sndBuchi) {
		mBuchiPairs.add(new PairXX<IBuchi>(fstBuchi, sndBuchi));
	}
	
	protected void setAlphabet(List<String> alphabet) {
		mAlphabets = alphabet;
	}
	
	@Override
	public List<String> getAlphabet() {
		return Collections.unmodifiableList(mAlphabets);
	}
	
	protected int getLetterId(String letterStr) {
		return mAlphabetMap.get(letterStr);
	}
	
	protected int getStateId(String stateStr) {
		return mStateMap.get(stateStr);
	}
	
	protected void clearStateMap() {
		mStateMap.clear();
	}
	
	protected void addLetter(String letterStr) {
		if(! mAlphabetMap.containsKey(letterStr)) {
			mAlphabetMap.put(letterStr, mAlphabets.size());
			mAlphabets.add(letterStr);
		}
	}
	
	public int getAlphabetSize() {
		return mAlphabets.size();
	}
	
	// not necessary
	public void clear() {
		mAlphabetMap.clear();
		mStateMap.clear();
	}
	
//	public void putLetter(String letterStr, int letter) {
//		mAlphabetMap.put(letterStr, letter);
//	}
	
	protected void putState(String stateStr, int state) {
		mStateMap.put(stateStr, state);
	}
	
	public List<PairXX<IBuchi>> getBuchiPairs() {
		return Collections.unmodifiableList(mBuchiPairs);
	}
	
	@Override
	public void parse(String file) {
		try {
			FileInputStream inputStream = new FileInputStream(new File(file));
			ATSParser parser = new ATSParser(inputStream);
			parser.parse(this);
			clear();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBuchi getBuchi() {
		return getSndBuchi();
	}

	@Override
	public IBuchi getFstBuchi() {
		return getBuchiPairs().get(0).getFirst();
	}

	@Override
	public IBuchi getSndBuchi() {
		return getBuchiPairs().get(0).getSecond();
	}
	
	

}
