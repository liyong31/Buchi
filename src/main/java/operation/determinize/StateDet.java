package operation.determinize;

import automata.IBuchi;
import automata.State;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;
import util.ISet;
import util.UtilISet;

public class StateDet extends State {
    
    private ND mND;
    private final IBuchi mOperand;
    private final Determinize mDeterminized;
    
    public StateDet(Determinize determinized, int id, ND ndb) {
        super(id);
        this.mOperand = determinized.getOperand();
        this.mDeterminized = determinized;
        this.mND = ndb;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();
    
    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        // computing successors
        ISet nSuccs = UtilISet.newISet();
        ISet jSuccs = UtilISet.newISet();
        for(final int stateId : mND.getNSet()) {
            for(final int succId : mOperand.getState(stateId).getSuccessors(letter)) {
                if(mOperand.isFinal(succId)) {
                    jSuccs.set(succId);
                }else {
                    nSuccs.set(succId);
                }
            }
        }
        // now the nSuccs has been fixed, we have to compute the successors of D
        ISet dSuccs = UtilISet.newISet();
        TIntIntMap map = new TIntIntHashMap();
        ISet indices = UtilISet.newISet();
        for(final int deckerId : mND.getDSet()) {
            int stateId = mDeterminized.getDeckerState(deckerId);
            int label = mDeterminized.getDeckerLabel(deckerId);
            indices.set(label);
            for(final int succId : mOperand.getState(stateId).getSuccessors(letter)) {
                if(map.containsKey(succId)) {
                    int oldLabel = map.get(succId);
                    if(oldLabel > label) {
                        map.adjustValue(succId, label-oldLabel);
                    }
                }else {
                    map.put(succId, label);
                }
            }
        }
        // we generate deckers for D successors
        
        TIntIntProcedure procedure = new TIntIntProcedure() {
            @Override
            public boolean execute(int state, int label) {
                int id = mDeterminized.getDeckerId(new Decker(state, label));
                dSuccs.set(id);
                return true;
            }
        };
        map.forEachEntry(procedure);
        // now for jSuccs
        for(final int succId : jSuccs) {
            int label = 0;
            while(indices.get(label)) {
                label ++;
            }
            int id = mDeterminized.getDeckerId(new Decker(succId, label));
            dSuccs.set(id);
            indices.set(label);
        }
        // now we compute the successor
        StateDet succ = mDeterminized.getOrAddState(new ND(nSuccs, dSuccs));
        System.out.println(nd2Str(mND) + " -> " + nd2Str(succ.mND) + ": " + letter);
        super.addSuccessor(letter, succ.getId());
        return super.getSuccessors(letter);
    }
    
    private String nd2Str(ND nd) {
        StringBuilder builder = new StringBuilder();
        builder.append("(" + nd.getNSet().toString() + ", {");
        for(final int id : nd.getDSet()) {
            builder.append("(" + mDeterminized.getDeckerState(id) + "," + mDeterminized.getDeckerLabel(id) + "),");
        }
        builder.append("})");
        return builder.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof StateDet)) {
            return false;
        }
        StateDet other = (StateDet)obj;
        return  mND.equals(other.mND);
    }
    
    @Override
    public String toString() {
        return mND.toString();
    }
    

    @Override
    public int hashCode() {
        return mND.hashCode();
    }

}
