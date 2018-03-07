package main;

public class Options {
	
    // verbose mode
	public static boolean mVerbose = false;
	
	// 0 for BitSet, 1 for SparseBitSet, 2 for TInSet, 3 for TreeSet, and 4 for HashSet 
	public static int mSet = 3;
	
	// whether to enable optimized version of NCSB
	// delay the word from C (newly incomers from N) to S 
	// so set B to be distribution source 
	public static boolean mLazyS = false;
	
	// delay the word from C (newly incomers from N) to B 
	public static boolean mLazyB = false;
	
	// decrease one at a time
	public static boolean mMinusOne = false;
	
	public static boolean mDebug = false;
	
	public static boolean mAntichain = false;
	
	public static boolean mGBA = false;
	
	// Ondra exploration
	public static boolean mOE = false;
	
	public static Algorithm mAlgo = Algorithm.RANK;
	
	public static enum Algorithm {
	    NCSB, 
	    RANK
	}

}
