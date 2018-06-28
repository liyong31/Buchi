package operation.determinize.ldba;

import java.util.LinkedList;

import automata.Buchi;
import automata.DPA;
import automata.IBuchi;
import automata.StateDA;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.IUnaryOp;
import util.ISet;
import util.UtilISet;

public class LDBA2DPA extends DPA implements IUnaryOp<IBuchi, DPA> {

    private final IBuchi mOperand;
    private final TObjectIntMap<StateDPA> mStateIndices = new TObjectIntHashMap<>();

    public LDBA2DPA(IBuchi operand) {
        super(operand.getAlphabetSize());
        this.mOperand = operand;
        computeInitialStates();
    }
    
    protected void computeInitialStates() {
        ISet D = mOperand.getInitialStates().clone();
        D.and(mOperand.getFinalStates()); // goto C
        ISet N = mOperand.getInitialStates().clone();
        N.andNot(D);
        // we have to get the indexed
        final int label = 1;
        ParallelRuns runs = new ParallelRuns(N);
        for(int s : D) {
            runs.addLabel(s, label);
        }
        StateDPA init = getOrAddState(runs);
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
    public DPA getResult() {
        return this;
    }

    public StateDPA getStateDet(int id) {
        return (StateDPA) getState(id);
    }

    protected StateDPA getOrAddState(ParallelRuns ndb) {

        StateDPA state = new StateDPA(this, 0, ndb);

        if (mStateIndices.containsKey(state)) {
            return getStateDet(mStateIndices.get(state));
        } else {
            int index = getStateSize();
            StateDPA newState = new StateDPA(this, index, ndb);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
//            if (ndb.getBSet().overlap(mOperand.getFinalStates()))
//                setFinal(index);
            return newState;
        }
    }
    
    private static void explore(DPA dra) {

        LinkedList<StateDA> walkList = new LinkedList<>();
        walkList.add(dra.getState(dra.getInitialState()));

        ISet visited = UtilISet.newISet();

        while (!walkList.isEmpty()) {
            StateDA s = walkList.remove();
            if (visited.get(s.getId()))
                continue;
            visited.set(s.getId());

            for (int letter = 0; letter < dra.getAlphabetSize(); letter++) {
                int succ = s.getSuccessor(letter);
                if (!visited.get(succ)) {
                    walkList.addFirst(dra.getState(succ));
                }
            }
        }
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
        
        LDBA2DPA deted = new LDBA2DPA(buchi);
        explore(deted);
        
        System.out.println(deted.toDot());
        
        Buchi b2 = new Buchi(2);
        b2.addState();
        b2.addState();
        
        b2.setInitial(0);
        b2.setFinal(1);
        
        // 
        b2.getState(0).addSuccessor(0, 0);
        b2.getState(0).addSuccessor(1, 0);
        b2.getState(0).addSuccessor(1, 1);
        
        b2.getState(1).addSuccessor(0, 0);
        b2.getState(1).addSuccessor(0, 1);
        
        System.out.println(b2.toDot());
        
        deted = new LDBA2DPA(b2);
        explore(deted);
        
        System.out.println(deted.toDot());
        
        Buchi A = new Buchi(2);
        A.addState();
        A.addState();
        A.addState();
        A.addState();
        
        A.setInitial(0);
        A.setFinal(1);
        A.setFinal(2);
        
        // 
        A.getState(0).addSuccessor(0, 0);
        A.getState(0).addSuccessor(1, 0);
        A.getState(0).addSuccessor(0, 1);
        A.getState(0).addSuccessor(1, 2);
        
        A.getState(1).addSuccessor(0, 1);
        A.getState(1).addSuccessor(1, 3);
        
        A.getState(2).addSuccessor(0, 3);
        A.getState(2).addSuccessor(1, 2);
        
        A.getState(3).addSuccessor(0, 3);
        A.getState(3).addSuccessor(1, 3);
        
        System.out.println(A.toDot());
        deted = new LDBA2DPA(A);
        explore(deted);
        
        System.out.println(deted.toDot());
    }

}
