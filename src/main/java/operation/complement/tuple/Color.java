package operation.complement.tuple;

public enum Color {
    
    NONE,
    ZERO,
    ONE,
    TWO,
    THREE;
    
    @Override
    public String toString() {
        if(this == NONE) {
            return "";
        }else if(this == ZERO) {
            return "" + 0;
        }else if(this == ONE) {
            return "" + 1;
        }else if(this == TWO) {
            return "" + 2;
        }else {
            return "" + 3;
        }
    }
}
