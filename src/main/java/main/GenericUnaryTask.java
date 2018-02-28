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
		+ COLUMN_NAMES[7] + " = "	+ mOperationName + "\n"
		+ COLUMN_NAMES[8] + " = "	+ mRunTime + "\n"
		+ COLUMN_NAMES[9] + " = "	+ mResultValue  + "\n";
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
