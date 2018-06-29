package automata;

import java.util.ArrayList;

import util.ISet;
import util.UtilISet;

// Rabin automata
public class AccRA implements IAcc {

    private final ArrayList<ISet> mEs;
    private final ArrayList<ISet> mFs;
    
    public AccRA() {
        this.mEs = new ArrayList<>();
        this.mFs = new ArrayList<>();
    }
    
    @Override
    public AccType getType() {
        return AccType.RABIN;
    }
    
    public void addE(int state, int label) {
        while(mEs.size() <= label) {
            mEs.add(UtilISet.newISet());
            mFs.add(UtilISet.newISet());
        }
        // now we add labels
        mEs.get(label).set(state);
    }
    
    public void addF(int state, int label) {
        while(mFs.size() <= label) {
            mEs.add(UtilISet.newISet());
            mFs.add(UtilISet.newISet());
        }
        // now we add labels
        mFs.get(label).set(state);
    }
    
    public int size() {
        return mEs.size();
    }
    
    public boolean isInE(int state, int label) {
        assert mEs.size() > label;
        return mEs.get(label).get(state);
    }
    
    public boolean isInF(int state, int label) {
        assert mFs.size() > label;
        return mFs.get(label).get(state);
    }

    @Override
    public void simplify() {
        
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < mEs.size(); i ++) {
            builder.append("E" + i + ": " + mEs.get(i) + "\n");
            builder.append("F" + i + ": " + mFs.get(i) + "\n\n");
        }
        return builder.toString();
    }

}
