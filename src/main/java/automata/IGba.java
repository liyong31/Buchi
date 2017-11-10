package automata;

import util.ISet;

public interface IGba extends IBuchi {

    @Override
    default public void setFinal(int id) {
        throw new UnsupportedOperationException("GBA do not support GBA");
    }
    
    @Override
    default public boolean isFinal(int id) {
        return !getAccSet(id).isEmpty();
    }
    
    int getAccSize();

    void setAccSize(int size);
    
    void setFinal(int state, int index);
    
    ISet getAccSet(int state);

    default boolean isFinal(int state, int index) {
        return getAccSet(state).get(index);
    }
}
