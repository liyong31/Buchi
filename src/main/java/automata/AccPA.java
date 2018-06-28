package automata;


import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;
import util.ISet;
import util.UtilISet;

// Rabin automata
public class AccPA implements IAcc {

    private final TIntIntMap mPs;
    
    public AccPA() {
        this.mPs = new TIntIntHashMap();
    }
    
    @Override
    public AccType getType() {
        return AccType.PARITY;
    }
    
    public void setColor(int state, int color) {
        mPs.put(state, color);
    }
    
    public int getColor(int state) {
        return mPs.get(state);
    }
    
    public ISet getStatesByColor(int color) {
        ISet states = UtilISet.newISet();
        TIntIntProcedure procedure = new TIntIntProcedure() {
            @Override
            public boolean execute(int state, int c) {
                if(c == color) {
                    states.set(state);
                }
                return true;
            }
        };
        mPs.forEachEntry(procedure);
        return states;
    }

    @Override
    public void simplify() {
        
    }

}
