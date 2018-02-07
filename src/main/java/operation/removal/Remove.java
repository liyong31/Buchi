package operation.removal;

import java.util.LinkedList;

import automata.Buchi;
import automata.IBuchi;
import automata.IState;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import operation.IUnaryOp;
import util.ISet;
import util.UtilISet;

public class Remove implements IUnaryOp<IBuchi, IBuchi> {
    protected final IBuchi mOperand;
    protected IBuchi mResult;
    public Remove(IBuchi operand) {
        mOperand = operand;
        Buchi reach = new Buchi(mOperand.getAlphabetSize());
        // -----------------------------------------
        TIntIntMap map = new TIntIntHashMap();
        TIntObjectMap<ISet> mapReach = new TIntObjectHashMap<>();
        ISet visited = UtilISet.newISet();
        LinkedList<Integer> queue = new LinkedList<>();
        for(final int init : mOperand.getInitialStates() ) {
            getOrAddState(reach, operand, init, map);
            queue.add(init);
            visited.set(init);
        }
        
        ISet used = UtilISet.newISet();
        while(! queue.isEmpty()) {
            int lState = queue.remove();
            int rState = getOrAddState(reach, operand, lState, map);
            used.set(rState);
            for(int c = 0; c < operand.getAlphabetSize(); c ++) {
                for(int lSucc : operand.getState(lState).getSuccessors(c)) {
                    int rSucc = getOrAddState(reach, operand, lSucc, map);
                    // record outgoing transitions
                    reach.getState(rState).addSuccessor(c, rSucc);
                    ISet scSucc = mapReach.get(rSucc);
                    if(scSucc == null) {
                        scSucc = UtilISet.newISet();
                    }
                    // record incoming transitions
                    scSucc.set(rState);
                    mapReach.put(rSucc, scSucc);
                    if(! visited.get(lSucc)) {
                        queue.add(lSucc);
                        visited.set(lSucc);
                    }
                }
            }
        }
        
        ISet unused = UtilISet.newISet();
        // ---------------------------------------------------------
        // secondly remove all reachable states which are dead states
        while(true) {
            boolean changed = false;
            // find one state which is dead
            ISet temp = UtilISet.newISet();
            for(final int s : used) {
                IState st = reach.getState(s);
                boolean hasSucc = false;
                for(int c = 0; c < reach.getAlphabetSize(); c ++) {
                    for(int succ : st.getSuccessors(c)) {
                        if(unused.get(succ)) continue;
                        hasSucc = true;
                    }
                }
                if(! hasSucc) {
                    unused.set(s);
                    temp.set(s);
                    changed = true;
                }
            }
            if(! changed) {
                break;
            }else {
                used.andNot(temp);
            }
        }
        ISet reachedFinals = reach.getFinalStates();
        reachedFinals.and(used);
        if(reachedFinals.isEmpty()) {
            mResult = new Buchi(operand.getAlphabetSize());
            return ;
        }else {
            mResult = reach;
        }
        // ---------------------------------------------------------
        // thirdly collect all reachable states which can reach final states
        ISet backReached = UtilISet.newISet();
        while(true) {
            backReached.or(reachedFinals);
            ISet prevs = UtilISet.newISet();
            for(final int s : reachedFinals) {
                ISet sC = mapReach.get(s);
                if( sC == null && reach.getInitialStates().get(s)) {
                    continue;
                }else if(sC == null){
                    assert false : "State " + s + " has no predecessors";
                }
                // one step predecessors
                prevs.or(sC);
            }
            // new one step predecessors
            prevs.andNot(backReached);
            if(prevs.isEmpty()) {
                // no more predecessors
                break;
            }
            reachedFinals = prevs;
        }
        map.clear();
        // ---------------------------------------------------------
        // finally construct the new automaton
        mResult = new Buchi(operand.getAlphabetSize());
        queue = new LinkedList<>();
        visited.clear();
        for(final int init : reach.getInitialStates() ) {
            getOrAddState(mResult, reach, init, map);
            queue.add(init);
            visited.set(init);
        }
        
        while(! queue.isEmpty()) {
            int lState = queue.remove();
            // ignore unused states
            if(! backReached.get(lState)) continue;
            int rState = getOrAddState(mResult,  reach, lState, map);
            for(int c = 0; c < reach.getAlphabetSize(); c ++) {
                for(int lSucc : reach.getState(lState).getSuccessors(c)) {
                    if(! backReached.get(lSucc)) continue;
                    int rSucc = getOrAddState(mResult, reach, lSucc, map);
                    // record outgoing transitions
                    mResult.getState(rState).addSuccessor(c, rSucc);
                    if(! visited.get(lSucc)) {
                        queue.add(lSucc);
                        visited.set(lSucc);
                    }
                }
            }
        }
    }
    
    private int getOrAddState(IBuchi result, IBuchi input, int state, TIntIntMap map) {
        if(map.containsKey(state)) {
            return map.get(state);
        }
        IState rState = result.addState();
        map.put(state, rState.getId());
        if(input.isInitial(state)) {
            result.setInitial(rState.getId());
        }
        if(input.isFinal(state)) {
            result.setFinal(rState.getId());
        }
        return rState.getId();
    }

    @Override
    public String getName() {
        return "Remove";
    }

    @Override
    public IBuchi getOperand() {
        return mOperand;
    }

    @Override
    public IBuchi getResult() {
        return mResult;
    }

}
