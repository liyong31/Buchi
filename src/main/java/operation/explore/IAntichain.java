package operation.explore;

public interface IAntichain extends Iterable<Integer> {
    
    // add a state in Antichain
    void set(int u);
    
    // check whether a state is covered by antichain
    boolean get(int u);
    
    // get the size of antichain
    int getSize();
    
    void clear();

}
