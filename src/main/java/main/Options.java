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

package main;

public class Options {
	
    // verbose mode
	public static boolean mVerbose = false;
	
	// 0 for BitSet, 1 for SparseBitSet, 2 for TInSet, 3 for TreeSet, and 4 for HashSet 
	public static int mSet = 3;
	
	public static boolean mDebug = false;
	
	public static boolean mAntichain = false;
	
	public static boolean mGBA = false;
	
	// Ondra exploration
	public static boolean mOE = false;
	
	
	// complementation operation
	
	public static Algorithm mAlgo = Algorithm.RANK;
	
	public static enum Algorithm {
	    NCSBOTF, // actually NCSB is a special case of RANK-based algorithm 
	    NCSB,
	    RANK,
	    TIGHT,
	    RAMSEY,
	    TUPLE,
	    SLICE,
	    NSBC
	}

	   // whether to enable optimized version of NCSB
    // delay the word from C (newly incomers from N) to S 
    // so set B to be distribution source 
    public static boolean mLazyS = false;
    
    // delay the word from C (newly incomers from N) to B 
    public static boolean mLazyB = false;
    
    // decrease one at a time
    public static boolean mMinusOne = false;
    
    // only tight rank is allowed in FKV's paper
    public static boolean mTightRank = false;
    
    // only turn wise in Sven's paper
    public static boolean mTurnwise = false;
    
    public static boolean mReduceOutdegree = false;
    
    // deterministic guess for jumping edges
    public static boolean mEnhancedSliceGuess= false;
    
    
    // merge adjacent sets with same colors
    public static boolean mMergeAdjacentSets = false;
    
    // merge adjacent sets with 1 and 2 colors
    public static boolean mMergeAdjacentColoredSets = false;
    
    public static boolean mRemoveDead = false;
    
    public static boolean mComplete = false;
    
    // merge states in complement
    public static boolean mDirectSimulation = false;
}
