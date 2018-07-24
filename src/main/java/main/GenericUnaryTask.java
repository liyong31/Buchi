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

public abstract class GenericUnaryTask implements ITask {
	
	protected static final String[] COLUMN_NAMES = {
			"FILE"
			, "OP_SEMIDETERMINISTIC"
			, "OP_STATES"
			, "OP_TRANS"
			, "ALPHABET_SIZE" // shoud be the same as RHS_ALPHABET
			, "RESULT_STATES"
			, "RESULT_TRANS"
			, "RM_RESULT_STATES"
			, "RM_RESULT_TRANS"
			, "ALGORITHM"
			, "RUNTIME(ms)"
			, "RESULT"
			};
	
	protected String mFileName;
		
	protected boolean mOpSemiDet;
		
	protected int mOpStateNum;
	
	protected int mOpTransNum;
	
	protected int mAlphabetSize;
	
	protected int mResultTransSize;
	
	protected int mResultStateSize;
	
    protected int mRmResultTransSize;
    
    protected int mRmResultStateSize;
    
	protected String mOperationName;
	
	protected long mRunTime;
		
	protected ResultValue mResultValue;

	@Override
	public String getOperationName() {
		return mOperationName;
	}

	@Override
	public long getRuntime() {
		return mRunTime;
	}

	@Override
	public ResultValue getResultValue() {
		return mResultValue;
	}

	@Override
	public void setResultValue(ResultValue resultValue) {
		mResultValue = resultValue;
	}

	@Override
	public String toStringVerbose() {
		return 
		COLUMN_NAMES[0] + " = "	+ mFileName + "\n"
		+ COLUMN_NAMES[1] + " = "	+ mOpSemiDet  + "\n"
		+ COLUMN_NAMES[2] + " = "	+ mOpStateNum + "\n"
		+ COLUMN_NAMES[3] + " = "	+ mOpTransNum + "\n"
		+ COLUMN_NAMES[4] + " = "	+ mAlphabetSize + "\n"
		+ COLUMN_NAMES[5] + " = "	+ mResultStateSize + "\n"
		+ COLUMN_NAMES[6] + " = "   + mResultTransSize + "\n"
		+ COLUMN_NAMES[7] + " = "   + mRmResultStateSize + "\n"
		+ COLUMN_NAMES[8] + " = "   + mRmResultTransSize + "\n"
		+ COLUMN_NAMES[9] + " = "	+ mOperationName + "\n"
		+ COLUMN_NAMES[10] + " = "	+ mRunTime + "\n"
		+ COLUMN_NAMES[11] + " = "	+ mResultValue  + "\n";
	}
	
	@Override
	public String toString() {
		return mFileName 
		+ "," + mOpSemiDet
		+ "," + mOpStateNum
		+ "," + mOpTransNum
		+ "," + mAlphabetSize
		+ "," + mResultStateSize
		+ "," + mResultTransSize
		+ "," + mRmResultStateSize
        + "," + mRmResultTransSize
		+ "," + mOperationName
		+ "," + mRunTime
		+ "," + mResultValue;
	}
	
	public static String getColumns() {
		StringBuilder sb = new StringBuilder();
		sb.append(COLUMN_NAMES[0]);
		
		for(int i = 1; i < COLUMN_NAMES.length; i ++) {
			sb.append("," + COLUMN_NAMES[i]);
		}
		return sb.toString();
	}
	

	@Override
	public void setRunningTime(long time) {
		mRunTime = time;
	}

}
