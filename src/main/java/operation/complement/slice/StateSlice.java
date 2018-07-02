package operation.complement.slice;

import java.util.List;

import automata.IBuchi;
import automata.State;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import util.ISet;
import util.UtilISet;

public class StateSlice extends State {

    private final ComplementSlice mComplement;
    private final PreorderSets mPSets;
    
    public StateSlice(ComplementSlice complement, int id, PreorderSets psets) {
        super(id);
        this.mComplement = complement;
        this.mPSets = psets;
    }
    
    private ISet mVisitedLetters = UtilISet.newISet();

    @Override
    public ISet getSuccessors(int letter) {
        if(mVisitedLetters.get(letter)) {
            return super.getSuccessors(letter);
        }
        mVisitedLetters.set(letter);
        //1. compute ordered sets
        List<ISet> orderedSets = mPSets.getOrderedSets();
        ISet succs = UtilISet.newISet();
        IBuchi operand = mComplement.getOperand();
        TIntObjectMap<ISet> map = new TIntObjectHashMap<>();
        for(int i = 0; i < orderedSets.size(); i ++) {
            ISet succsF = UtilISet.newISet();
            ISet succsNotF = UtilISet.newISet();
            for(int state : orderedSets.get(i)) {
                ISet osuccs = UtilISet.newISet();
                for(int succ : operand.getState(state).getSuccessors(letter)) {
                    // ignore previously appeared states
                    if(succs.get(succ)) continue;
                    if(operand.isFinal(succ)) {
                        succsF.set(succ);
                    }else {
                        succsNotF.set(succ);
                    }
                    succs.set(succ);
                    osuccs.set(succ);
                }
                map.put(state, osuccs);
            }
            if(!succsF.isEmpty()) {
                orderedSets.add(succsF);
            }
            if(!succsNotF.isEmpty()) {
                orderedSets.add(succsNotF);
            }
        }
        //2. compute the guesses
        
        
        return null;
    }

}
