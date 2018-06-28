package automata;

public enum AccType {
    
    BUCHI,
    RABIN,
    PARITY;
    
    public boolean isBuchi() {
        return this == BUCHI;
    }
    
    public boolean isRabin() {
        return this == RABIN;
    }
    
    public boolean isParity() {
        return this == PARITY;
    }
    
    
    @Override
    public String toString() {
        if(this == BUCHI) {
            return "NBA";
        }else if(this == RABIN) {
            return "DRA";
        }else {
            return "DPA";
        }
    }

}
