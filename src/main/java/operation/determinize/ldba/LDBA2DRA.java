package operation.determinize.ldba;

import java.util.LinkedList;
import java.util.Map;

import automata.Buchi;
import automata.DRA;
import automata.IBuchi;
import automata.StateDA;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.IUnaryOp;
import util.ISet;
import util.UtilISet;

public class LDBA2DRA extends DRA implements IUnaryOp<IBuchi, DRA> {

    private final IBuchi mOperand;
    private final TObjectIntMap<StateDRA> mStateIndices = new TObjectIntHashMap<>();
    protected int mMaxLabel = 0;
    
    public LDBA2DRA(IBuchi operand) {
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
        final int label = 0;
        ParallelRuns runs = new ParallelRuns(N);
        for(int s : D) {
            runs.addLabel(s, label);
        }
        StateDRA init = getOrAddState(runs);
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
    public DRA getResult() {
        return this;
    }

    public StateDRA getStateDet(int id) {
        return (StateDRA) getState(id);
    }
    
    public int getLabelSize() {
        return mMaxLabel + 1;
    }

    protected StateDRA getOrAddState(ParallelRuns runs) {
        StateDRA state = new StateDRA(this, 0, runs);
        if (mStateIndices.containsKey(state)) {
            return getStateDet(mStateIndices.get(state));
        } else {
            int index = getStateSize();
            StateDRA newState = new StateDRA(this, index, runs);
            int id = this.addState(newState);
            mStateIndices.put(newState, id);
            //this.getAcceptance().addE(state, label);
            return newState;
        }
    }
    
    public void computeAcceptance() {
        ISet finalStates = mOperand.getFinalStates();
        for(int sid = 0; sid < getStateSize(); sid ++) {
            Map<Integer, ISet> labelStates = getStateDet(sid).getLabelStates();
            for(int label = 0; label < mMaxLabel; label ++) {
                if(!labelStates.containsKey(label)) {
                    this.getAcceptance().addE(sid, label);
                }else if(finalStates.overlap(labelStates.get(label))){
                    this.getAcceptance().addF(sid, label);
                }
            }
        }
    }
    
    private static void explore(LDBA2DRA dra) {

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
        
        dra.computeAcceptance();
        
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
        
        LDBA2DRA deted = new LDBA2DRA(buchi);
        explore(deted);
        
        System.out.println(deted.toDot());
        System.out.println("max label: " + (deted.getLabelSize() - 1));

        
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
        
        deted = new LDBA2DRA(b2);
        explore(deted);
        
        System.out.println(deted.toDot());
        System.out.println("max label: " + (deted.getLabelSize() - 1));
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
        deted = new LDBA2DRA(A);
        explore(deted);
        
        System.out.println(deted.toDot());
        System.out.println("max label: " + (deted.getLabelSize() - 1));
        System.out.println("acc label:\n" + (deted.getAcceptance()));

    }

}
