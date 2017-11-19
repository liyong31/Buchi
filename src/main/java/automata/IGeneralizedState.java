package automata;

import util.ISet;

public interface IGeneralizedState extends IState {
    
    void setFinal(int index);

    ISet getAccSet();
    
    boolean isFinal(int index);
}
