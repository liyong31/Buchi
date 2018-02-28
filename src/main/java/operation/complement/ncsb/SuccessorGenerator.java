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

package operation.complement.ncsb;

import main.Options;
import util.ISet;
import util.PowerSet;

public class SuccessorGenerator {
	
	private boolean mIsCurrBEmpty;
	private final NCSB mSuccNCSB;
	
	private ISet mMinusFSuccs;
	private ISet mInterFSuccs;
	
	private ISet mF;       // so far all final states
	
	private ISet mNPrime;  // d(N)\F\B'\S'
	private ISet mVPrime;  // d(C) \/ (d(N) /\ F)
	private ISet mMustIn;  // must in states in C or B
	private ISet mSPrime;  // d(S)
	private ISet mBPrime;  // d(B)
	
	private PowerSet mPs;	
		
	public SuccessorGenerator(boolean isBEmpty, NCSB succ, ISet minusFSuccs, ISet interFSuccs, ISet f) {
		this.mIsCurrBEmpty = isBEmpty;
		this.mSuccNCSB = succ;
				
		this.mMinusFSuccs = minusFSuccs;
		this.mInterFSuccs = interFSuccs;
		this.mF = f;
		
		// initialization
		initialize();
	}
	
	private void initialize() {
		
		// N'
		mNPrime =  this.mSuccNCSB.copyNSet();
		mNPrime.andNot(mF);                    // remove final states
		mNPrime.andNot(mSuccNCSB.getCSet());   // remove successors of C, the final states of NSuccs are in CSuccs 
		mNPrime.andNot(mSuccNCSB.getSSet());   // remove successors of S
		
		// V' = d(C) \/ (d(N)/\F)
		mVPrime =  mSuccNCSB.copyCSet();
		ISet nInterFSuccs =  mSuccNCSB.copyNSet();
		nInterFSuccs.and(mF);           // (d(N) /\ F)
		mVPrime.or(nInterFSuccs);       // d(C) \/ (d(N) /\ F)
		
		// S successors
		mSPrime =  mSuccNCSB.getSSet();
		
		// B successors
		mBPrime =  mSuccNCSB.getBSet();
		
		// compute must in (C/B) states
		// in order not to mess up the code with the description 
		// some lines may repeat in different situation
		if(Options.mLazyS) {
			// lazy NCSB initialization
			if(mIsCurrBEmpty) {
				mInterFSuccs = mSuccNCSB.copyCSet(); // set to d(C)
				// must in states computation
				mMustIn = mSuccNCSB.copyCSet();
				mMustIn.and(mF);                  // d(C) /\ F
				mMustIn.or(nInterFSuccs);         // C_under = d(C\/N) /\F
			}else {
				mMustIn = mInterFSuccs.clone(); // d(B/\F)
				mMustIn.and(mF);                // d(B/\F) /\F
				mMustIn.or(mMinusFSuccs);       // B_under = d(B\F) \/ (d(B/\F) /\F)
			}
		}else {
			// original NCSB
			mMustIn = mInterFSuccs.clone(); // d(C/\F)
			mMustIn.and(mF);                // d(C/\F) /\F
			mMustIn.or(mMinusFSuccs);       // d(C\F) \/ (d(C/\F) /\F)
			mMustIn.or(nInterFSuccs);       // C_under = d(C\F) \/ (d(C/\F) /\F) \/ (d(N)\/ F)
		}
		
		// compute nondeterministic states from mInterFSuccs
		mInterFSuccs.andNot(mMinusFSuccs);     // remove must-in C (B) states
		mInterFSuccs.andNot(mSPrime);          // remove must in S states
		mInterFSuccs.andNot(mF);               // remove final states 

		mPs = new PowerSet(mInterFSuccs);
		
	}
	
	public boolean hasNext() {
		return mPs.hasNext();
	}
	
	public NCSB next() {
		ISet toS = mPs.next(); // extra states to be added into S'
		ISet left = mInterFSuccs.clone();
		left.andNot(toS);
		// this is implementation for NCSB 
		ISet NP = mNPrime;
		ISet CP =  null;
		ISet SP =  mSPrime.clone();
		ISet BP = null;
		
		if(Options.mLazyS) {
			SP.or(toS); // S'=d(S)\/M'
			if(mIsCurrBEmpty) {
				// as usual S and C
				CP = mMustIn.clone();
				CP.or(left); // C' get extra
				if(!Options.mLazyB) {
					BP = CP;
				}else {
					// following is d(C) /\ C'
					BP = mSuccNCSB.copyCSet(); 
					BP.and(CP);   // B'= d(C) /\ C'
				}
			}else {
				// B is not empty
				BP = mMustIn.clone();
				BP.or(left); // B'=d(B)\M'
				CP = mVPrime.clone();
				CP.andNot(SP); // C'= V'\S'
			}
			
			assert !SP.overlap(mF) && !BP.overlap(SP) : "S:" + SP.toString() + " B:" + BP.toString();

		}else {
			// original NCSB
			CP = mMustIn.clone();
			CP.or(left);
			SP.or(toS);
			if(mIsCurrBEmpty) {
				if(!Options.mLazyB) {
					BP = CP;
				}else {
					BP = mSuccNCSB.copyCSet();
					BP.and(CP);
				}
			}else {
				BP =  mBPrime.clone();
				BP.and(CP);
			}
			
			assert !SP.overlap(mF) && !CP.overlap(SP) : "S:" + SP.toString() + " C:" + CP.toString();
		}

		return new NCSB(NP, CP, SP, BP);
	}
	
	
	

}
