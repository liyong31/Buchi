package util;

import java.util.Comparator;

public class PairXY<X, Y> implements Comparator<PairXY<X, Y>>{
    
    private X x;
    private Y y;
    
    public PairXY(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X getFirst() {
        return x;
    }

    public Y getSecond() {
        return y;
    }
    
    @Override
    public boolean equals(Object o) {
        if(! (o instanceof PairXY)) return false;
        PairXY<X, Y> other = (PairXY<X, Y>)o;
        return x.equals(other.x)
            && y.equals(other.y);
    }
    
    @Override
    public int hashCode(){
      return x.hashCode() + 31*y.hashCode();
    }   
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public int compare(PairXY<X, Y> arg0, PairXY<X, Y> arg1) {
        
        return 0;
    }

}
