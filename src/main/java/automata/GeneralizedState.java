package automata;

import util.ISet;
import util.UtilISet;

public class GeneralizedState extends State implements IGeneralizedState {
    
    private final ISet mLabel;
    
    public GeneralizedState(int id) {
        super(id);
        mLabel = UtilISet.newISet();
    }

    @Override
    public void setFinal(int index) {
        mLabel.set(index);
    }

    @Override
    public ISet getAccSet() {
        return mLabel.clone();
    }

    @Override
    public boolean isFinal(int index) {
        return mLabel.get(index);
    }
    

}
