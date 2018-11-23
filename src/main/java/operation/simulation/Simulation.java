package operation.simulation;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import automata.IBuchi;
import automata.State;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import util.ISet;
import util.PairXX;
import util.UtilISet;

public class Simulation {

    /**
     * A forward simulation relation for the state p and the state r denoted by
     * p R r exists only if 1. p is final implies r is final 2. for every (p, a,
     * p') \in \delta, there exists r' such that (r, a, r') \in \delta and r R
     * r'
     */
    public Set<PairXX<Integer>> getForwardSimulationRelation(IBuchi nba1, IBuchi nba2) {
        assert nba1.getAlphabetSize() == nba2.getAlphabetSize();
        // we just encode states
        int stateSize = nba1.getStateSize() + nba2.getStateSize();

        // add all states and alphabet for two automaton

        boolean[] isFinal = new boolean[stateSize];
        boolean[] isInit = new boolean[stateSize];
        boolean[][] fsim = new boolean[stateSize][stateSize];
        for (int i = 0; i < nba1.getStateSize(); i++) {
            isFinal[i] = nba1.isFinal(i);
            isInit[i] = nba1.isInitial(i);
        }

        for (int j = nba1.getStateSize(); j < stateSize; j++) {
            isFinal[j] = nba2.isFinal(j - nba1.getStateSize());
            isInit[j] = nba1.isInitial(j - nba1.getStateSize());
        }
        for (int i = 0; i < stateSize; i++) {
            State si = null;
            if (i < nba1.getStateSize()) {
                si = (State) nba1.getState(i);
            } else {
                si = (State) nba2.getState(i - nba1.getStateSize());
            }
            for (int j = i; j < stateSize; j++) {
                State sj = null;
                if (j < nba1.getStateSize()) {
                    sj = (State) nba1.getState(j);
                } else {
                    sj = (State) nba2.getState(j - nba1.getStateSize());
                }
                // i R j only if i is in F implies j is in F and i is covered by
                // j
                fsim[i][j] = (!isFinal[i] || isFinal[j]) && sj.forwardCovers(si);
                fsim[j][i] = (isFinal[i] || !isFinal[j]) && si.forwardCovers(sj);
            }
        }
        return getForwardSimulationRelation(nba1, nba2, fsim);
    }

    public Set<PairXX<Integer>> getForwardSimulationRelation(IBuchi nba1, IBuchi nba2, boolean[][] fsim) {

        // implement the HHK algorithm
        int n_states = nba1.getStateSize() + nba2.getStateSize();
        int n_symbols = nba1.getAlphabetSize();

        // fsim[u][v]=true iff v in fsim(u) iff v forward-simulates u

        int[][][] pre = new int[n_symbols][n_states][];
        int[][][] post = new int[n_symbols][n_states][];
        int[][] pre_len = new int[n_symbols][n_states];
        int[][] post_len = new int[n_symbols][n_states];

        TIntObjectMap<TIntIntMap> preds = new TIntObjectHashMap<>();
        TIntObjectMap<TIntObjectMap<ISet>> succs = new TIntObjectHashMap<>();
        for (int c = 0; c < nba1.getAlphabetSize(); c++) {
            if (!preds.containsKey(c)) {
                preds.put(c, new TIntIntHashMap());
            }
            if (!succs.containsKey(c)) {
                succs.put(c, new TIntObjectHashMap<>());
            }
            TIntIntMap pred = preds.get(c);
            TIntObjectMap<ISet> succ = succs.get(c);
            for (int s = 0; s < nba1.getStateSize(); s++) {
                ISet ts = nba1.getState(s).getSuccessors(c);
                succ.put(s, ts);
                for (int t : ts) {
                    if (pred.containsKey(t)) {
                        pred.adjustValue(t, 1);
                    } else {
                        pred.put(t, 1);
                    }
                }
            }
        }

        for (int c = 0; c < nba1.getAlphabetSize(); c++) {
            TIntIntMap pred = preds.get(c);
            TIntObjectMap<ISet> succ = succs.get(c);
            for (int s = 0; s < nba2.getStateSize(); s++) {
                ISet ts = nba2.getState(s).getSuccessors(c);
                ISet mts = UtilISet.newISet();
                for (int t : ts) {
                    int tp = nba1.getStateSize() + t;
                    if (pred.containsKey(tp)) {
                        pred.adjustValue(tp, 1);
                    } else {
                        pred.put(tp, 1);
                    }
                    mts.set(tp);
                }
                succ.put(s + nba1.getStateSize(), mts);
            }
        }
        // Initialize memory of pre/post
        for (int c = 0; c < n_symbols; c++) {
            for (int p = 0; p < n_states; p++) {
                post_len[c][p] = 0;
                if (succs.get(c).get(p).cardinality() != 0)
                    post[c][p] = new int[succs.get(c).get(p).cardinality()];
                pre_len[c][p] = 0;
                if (preds.get(c).get(p) != 0)
                    pre[c][p] = new int[preds.get(c).get(p)];
            }
        }

        // state[post[s][q][r]] is in post_s(q) for 0<=r<adj_len[s][q]
        // state[pre[s][q][r]] is in pre_s(q) for 0<=r<adj_len[s][q]
        for (int c = 0; c < n_symbols; c++) {
            for (int p = 0; p < n_states; p++) {
                ISet next = succs.get(c).get(p);
                if (next.cardinality() != 0) {
                    for (int q = 0; q < n_states; q++) {
                        if (next.get(q)) {
                            // if p --a--> q, then p is in pre_a(q), q is in
                            // post_a(p)
                            pre[c][q][pre_len[c][q]++] = p;
                            post[c][p][post_len[c][p]++] = q;
                        }
                    }
                }
            }
        }

        int[] todo = new int[n_states * n_symbols];
        int todo_len = 0;

        int[][][] remove = new int[n_symbols][n_states][n_states];
        int[][] remove_len = new int[n_symbols][n_states];
        for (int a = 0; a < n_symbols; a++) {
            for (int p = 0; p < n_states; p++)
                if (pre_len[a][p] > 0) // p is in a_S
                {
                    Sharpen_S_a: for (int q = 0; q < n_states; q++) // {all q}
                                                                    // --> S_a
                    {
                        if (post_len[a][q] > 0) /// q is in S_a
                        {
                            for (int r = 0; r < post_len[a][q]; r++)
                                if (fsim[p][post[a][q][r]]) // q is in
                                                            // pre_a(sim(p))
                                    continue Sharpen_S_a; // skip q
                            remove[a][p][remove_len[a][p]++] = q;
                        }
                    }
                    if (remove_len[a][p] > 0)
                        todo[todo_len++] = a * n_states + p;
                }
        }
        int[] swap = new int[n_states];
        int swap_len = 0;
        boolean using_swap = false;

        while (todo_len > 0) {
            todo_len--;
            int v = todo[todo_len] % n_states;
            int a = todo[todo_len] / n_states;
            int len = (using_swap ? swap_len : remove_len[a][v]);
            remove_len[a][v] = 0;

            for (int j = 0; j < pre_len[a][v]; j++) {
                int u = pre[a][v][j];

                for (int i = 0; i < len; i++) {
                    int w = (using_swap ? swap[i] : remove[a][v][i]);
                    if (fsim[u][w]) {
                        fsim[u][w] = false;
                        for (int b = 0; b < n_symbols; b++)
                            if (pre_len[b][u] > 0) {
                                Sharpen_pre_b_w: for (int k = 0; k < pre_len[b][w]; k++) {
                                    int ww = pre[b][w][k];
                                    for (int r = 0; r < post_len[b][ww]; r++)
                                        if (fsim[u][post[b][ww][r]]) // ww is in
                                                                     // pre_b(sim(u))
                                            continue Sharpen_pre_b_w; // skip ww

                                    if (b == a && u == v && !using_swap)
                                        swap[swap_len++] = ww;
                                    else {
                                        if (remove_len[b][u] == 0)
                                            todo[todo_len++] = b * n_states + u;
                                        remove[b][u][remove_len[b][u]++] = ww;
                                    }

                                }
                            }
                    } // End of if(fsim[u][w])
                }
            }
            if (swap_len > 0) {
                if (!using_swap) {
                    todo[todo_len++] = a * n_states + v;
                    using_swap = true;
                } else {
                    swap_len = 0;
                    using_swap = false;
                }
            }

        }

        Set<PairXX<Integer>> fsim2 = new TreeSet<PairXX<Integer>>();
        for (int p = 0; p < n_states; p++)
            for (int q = 0; q < n_states; q++)
                if (fsim[p][q]) // q is in sim(p), q simulates p
                    fsim2.add(new PairXX<Integer>(p, q));
        return fsim2;
    }

}
