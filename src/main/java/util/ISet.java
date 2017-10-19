package util;

//In order to keep the original code as much as possible, we
//use the interface of BitSet
public interface ISet extends Iterable<Integer> {

    ISet clone();

    void andNot(ISet set);

    void and(ISet set);

    void or(ISet set);

    boolean get(int value);

    void set(int value);

    void clear(int value);

    void clear();

    boolean isEmpty();

    int cardinality();

    int hashCode();

    String toString();

    default boolean overlap(ISet set) {
        ISet a = null;
        ISet b = null;

        if (this.cardinality() <= set.cardinality()) {
            a = this;
            b = set;
        } else {
            a = set;
            b = this;
        }

        for (final int elem : a) {
            if (b.get(elem))
                return true;
        }
        return false;
    }

    boolean subsetOf(ISet set);

    boolean contentEq(ISet set);
    
    Object get();

}
