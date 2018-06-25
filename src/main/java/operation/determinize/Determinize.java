package operation.determinize;

import java.util.ArrayList;
import java.util.List;

import automata.Buchi;
import automata.IBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.IUnaryOp;
import operation.explore.Explore;
import util.ISet;
import util.UtilISet;

public class Determinize extends Buchi implements IUnaryOp<IBuchi, IBuchi> {

    private final IBuchi mOperand;
    private final TObjectIntMap<StateDet> mStateIndices = new TObjectIntHashMap<>();
    private final TObjectIntMap<Decker> mDeckerMap;
    private final List<Decker> mDeckerList;

    public Determinize(IBuchi operand) {
        super(operand.getAlphabetSize());
        this.mOperand = operand;
        this.mDeckerMap = new TObjectIntHashMap<>();
        this.mDeckerList = new ArrayList<>();
        computeInitialStates();
    }
    
    protected void computeInitialStates() {
        ISet D = mOperand.getInitialStates().clone();
        D.and(mOperand.getFinalStates()); // goto C
        ISet N = mOperand.getInitialStates().clone();
        N.andNot(D);
        // we have to get the indexed
        int label = 0;
        ISet DD = UtilISet.newISet();
        for(int s : D) {
            Decker decker = new Decker(s, label);
            int id = getDeckerId(decker);
            DD.set(id);
            label ++;
        }
        StateDet init = getOrAddState(new ND(N, DD));
        this.setInitial(init.getId());
    }

    @Override
    public String getName() {
        return "Determinize";
    }

    @Override
    public IBuchi getOperand() {
        return mOperand;
    }

    @Override
    public IBuchi getResult() {
        return this;
    }

    public StateDet getStateDet(int id) {
        return (StateDet) getState(id);
    }

    protected StateDet getOrAddState(ND ndb) {

        StateDet state = new StateDet(this, 0, ndb);

        if (mStateIndices.containsKey(state)) {
            return getStateDet(mStateIndices.get(state));
        } else {
            int index = getStateSize();
            StateDet newState = new StateDet(this, index, ndb);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
//            if (ndb.getBSet().overlap(mOperand.getFinalStates()))
//                setFinal(index);
            return newState;
        }
    }
    
    public Decker getDecker(int id) {
        assert id < mDeckerList.size();
        return mDeckerList.get(id);
    }

    public int getDeckerId(Decker decker) {
        if (mDeckerMap.containsKey(decker)) {
            return mDeckerMap.get(decker);
        }
        int id = mDeckerList.size();
        mDeckerList.add(decker);
        mDeckerMap.put(decker, id);
        return id;
    }

//    protected IntSet getFinalDeckers() {
//        return mFinalDeckers;
//    }
//
//    protected ISet generateDeckers(int state, ISet ) {
//        IntSet result = UtilIntSet.newIntSet();
//        for (final int upState : upStates.iterable()) {
//            result.set(getDoubleDeckerId(new DoubleDecker(downState, upState)));
//        }
//        return result;
//    }

    protected int getDeckerState(int decker) {
        return getDecker(decker).getState();
    }

    protected int getDeckerLabel(int decker) {
        return getDecker(decker).getLabel();
    }
    
    public static void main(String[] args) {
        IBuchi buchi = new Buchi(2);
        int fst = 0, snd = 1, thd = 2, fur = 3, fiv = 4;
        
        buchi.addState();
        buchi.addState();
        buchi.addState();
        buchi.addState();
        buchi.addState();
        
        // 
        buchi.setInitial(fst);
        buchi.setFinal(snd);
        buchi.setFinal(fiv);
        
        // 
        buchi.getState(fst).addSuccessor(0, fst);
        buchi.getState(fst).addSuccessor(1, fst);
        
        buchi.getState(fst).addSuccessor(1, snd);
        buchi.getState(fst).addSuccessor(1, fur);
        
        buchi.getState(snd).addSuccessor(0, thd);
        buchi.getState(snd).addSuccessor(1, snd);
        
        buchi.getState(thd).addSuccessor(0, thd);
        buchi.getState(thd).addSuccessor(1, fiv);
        
        buchi.getState(fur).addSuccessor(0, fst);
        buchi.getState(fur).addSuccessor(0, fiv);
        
        buchi.getState(fiv).addSuccessor(0, fiv);
        
        // 
        System.out.println(buchi.toDot());
        
        Determinize deted = new Determinize(buchi);
        new Explore(deted);
        
        System.out.println(deted.toDot());
    }

}
