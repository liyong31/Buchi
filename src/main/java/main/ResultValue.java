package main;

public enum ResultValue {
	
	
	OK,        // OK for non-return or object return
	NULL,      // null for boolean or object return
	FALSE,     // false for boolean return
	TRUE,      // true for boolean return
	
	// runtime exceptions or errors
	EXE_UNKNOWN,
	EXE_TIMEOUT,
	EXE_MEMOOUT;

	
	public String toString() {
		
		switch(this) {
		case OK:
			return "ok";
		case NULL:
			return "null";
		case FALSE:
			return "false";
		case TRUE:
			return "true";
		case EXE_UNKNOWN:
			return "unknown";
		case EXE_TIMEOUT:
			return "time-out";
		case EXE_MEMOOUT:
			return "memory-out";
		default:
			assert false : "Unknown value for ResultValue";
		}
		return null;
	}
	
	public boolean isNormal() {
        switch (this) {
        case OK:
            return true;
        case FALSE:
            return true;
        case TRUE:
            return true;
        default:
            return false;
        }
	}

}
