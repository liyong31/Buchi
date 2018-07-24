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

package operation.complement.nsbc;

import operation.complement.ncsb.NCSB;
import util.ISet;

public class NSBC extends NCSB {
    
    protected final boolean mColored;
    
    public NSBC(boolean colored) {
        this.mColored = colored;
    }
    
    public NSBC(ISet N) {
        this.mNSet = N;
        this.mColored = false;
    }
    
    public NSBC(ISet N, ISet S, ISet B, ISet C) {
        this.mNSet = N;
        this.mSSet = S;
        this.mBSet = B;
        this.mCSet = C;
        this.mColored = true;
    }
    
    public boolean isColored() {
        return this.mColored;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof NSBC)) {
            return false;
        }
        NSBC nsbc = (NSBC)obj;
        return contentEqual(nsbc);
    }
    
    protected boolean contentEqual(NSBC nsbc) {
        if(this.mColored != nsbc.mColored) {
            return false;
        }
        if(!this.mColored) {
            return this.mNSet.equals(nsbc.getNSet());
        }else {
            return super.contentEqual(nsbc);
        }
    }
    
    @Override
    public String toString() {
        if(! this.mColored) {
            return "" + mNSet.toString() + "";
        }else {
            return "(" + mNSet.toString() + "," 
                    + mSSet.toString() + ","
                    + mBSet.toString() + ","
                    + mCSet.toString() + ")";
        }
    }
    
    public boolean isFinal() {
        return this.mColored && mBSet.isEmpty();
    }
    
    @Override
    public int hashCode() {
        if(!this.mColored) {
            return NCSB.hashValue(mNSet);
        }else {
            return super.hashCode(); 
        }
    }

}
