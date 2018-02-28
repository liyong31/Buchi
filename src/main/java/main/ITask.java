package main;

/**
 * General Task Interface
 * */
public interface ITask {
	
	
	String getOperationName();
		
	long getRuntime();
	
	void runTask();
	
	void setRunningTime(long time);
	
	ResultValue getResultValue();
	
	void setResultValue(ResultValue resultValue);
	
	String toString();
	String toStringVerbose();

}
