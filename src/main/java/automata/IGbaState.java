package automata;

import util.ISet;

public interface IGbaState extends IState {
    
    void setFinal(int index);

    ISet getAccSet();
    
    boolean isFinal(int index);
}
