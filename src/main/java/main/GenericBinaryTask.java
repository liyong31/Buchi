package main;

public abstract class GenericBinaryTask implements ITask {
	
	protected static final String[] COLUMN_NAMES = {
			"FILE"
			, "LHS_SEMIDETERMINISTIC"
			, "RHS_SEMIDETERMINISTIC"
			, "LHS_STATES"
			, "RHS_STATES"
			, "LHS_TRANS"
			, "RHS_TRANS"
			, "ALPHABET_SIZE" // shoud be the same as RHS_ALPHABET
			, "RESULT_STATES"
			, "ALGORITHM"
			, "RUNTIME(ms)"
			, "RESULT"
			};
	
	protected String mFileName;
		
	protected boolean mIsLHSSemiDet;
	
	protected boolean mIsRHSSemiDet;
	
	protected int mLHSStateNum;
	
	protected int mRHSStateNum;
	
	protected int mLHSTransNum;
	
	protected int mRHSTransNum;
	
	protected int mAlphabetSize;
	
	protected int mResultStateSize;
	
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
		+ COLUMN_NAMES[1] + " = "	+ mIsLHSSemiDet  + "\n"
		+ COLUMN_NAMES[2] + " = "	+ mIsRHSSemiDet + "\n"
		+ COLUMN_NAMES[3] + " = "	+ mLHSStateNum + "\n"
		+ COLUMN_NAMES[4] + " = "	+ mRHSStateNum + "\n"
		+ COLUMN_NAMES[5] + " = "	+ mLHSTransNum + "\n"
		+ COLUMN_NAMES[6] + " = "	+ mRHSTransNum + "\n"
		+ COLUMN_NAMES[7] + " = "	+ mAlphabetSize + "\n"
		+ COLUMN_NAMES[8] + " = "	+ mResultStateSize + "\n"
		+ COLUMN_NAMES[9] + " = "	+ mOperationName + "\n"
		+ COLUMN_NAMES[10] + " = "	+ mRunTime + "\n"
		+ COLUMN_NAMES[11] + " = "	+ mResultValue  + "\n";
	}
	
	@Override
	public String toString() {
		return mFileName 
		+ "," + mIsLHSSemiDet
		+ "," + mIsRHSSemiDet
		+ "," + mLHSStateNum
		+ "," + mRHSStateNum
		+ "," + mLHSTransNum
		+ "," + mRHSTransNum
		+ "," + mAlphabetSize
		+ "," + mResultStateSize
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
