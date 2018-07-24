/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi.
 * 
 * Buchi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Buchi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Buchi. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package operation.complement.ramsey;

import java.util.ArrayList;

import automata.IBuchi;

/**
 * <S1, ..., Sn> in the paper
 */
public class Profile extends ArrayList<RunSet> {

    private static final long serialVersionUID = 1L;
    private final IBuchi mBuchi;
    
    public Profile(IBuchi buchi) {
        super();
        this.mBuchi = buchi;
    }
    
    public boolean isInitial() {
        return super.isEmpty();
    }
    
    public Profile getSuccessorProfile(int letter) {
        if(isInitial()) {
            return getInitialSuccessor(letter);
        }else {
            return getNormalSuccessor(letter);
        }
    }
    
    private Profile getInitialSuccessor(int letter) {
        Profile profile = new Profile(this.mBuchi);
        for(int i = 0; i < mBuchi.getStateSize(); i ++) {
            RunSet Si = new RunSet();
            for(int succ : mBuchi.getState(i).getSuccessors(letter)) {
                Si.add(new RunPair(succ, false));
                if(mBuchi.isFinal(succ)) {
                    Si.add(new RunPair(succ, true));
                }
            }
            profile.add(Si);
        }
        return profile;
    }
    
    private Profile getNormalSuccessor(int letter) {
        Profile profile = new Profile(this.mBuchi);
        for(int i = 0; i < mBuchi.getStateSize(); i ++) {
            RunSet Ti = this.get(i);
            RunSet Si = new RunSet();
            for(RunPair pair : Ti) {
                for(int succ : mBuchi.getState(pair.getState()).getSuccessors(letter)) {
                    // <v, j> \in Ti => <u, 0> \in Si u \in \trans(v, a)
                    Si.add(new RunPair(succ, false));
                    // <v, 1> \in Ti => <u, 1> \in Si u \in \trans(v, a)
                    boolean acc = pair.isFinal();
                    // <v, j> \in Ti and u \in F => <u, 1> \in Si where u \in \trans(v, a)
                    acc = acc || mBuchi.isFinal(succ);
                    if(acc) {
                        Si.add(new RunPair(succ, true));
                    }
                }
            }
            profile.add(Si);
        }
        return profile;
    }
    
    /**
     *  current language X<sub>i</sub> and input periodic language X<sub>j</sub> 
     *  where L(D<sub>i</sub>) = X<sub>i</sub>
     * **/
    public boolean isDisjointWith(Profile pj) {
        // get all successors visited by initial states
        for(int init : mBuchi.getInitialStates()) {
            for(RunPair pair : this.get(init)) {
                int q = pair.getState();
                RunSet Sj = pj.get(q);
                // if q is reachable by initial state and q can visit itself via
                // final states
                if(Sj.contains(q, true)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     *  current language X<sub>i</sub> and language X<sub>j</sub>
     *  satisfy 
     *    </br>
     *    1. X<sub>i</sub> (X<sub>j</sub>)<sup>+</sup> = X<sub>i</sub>
     *      i.e., (p, q) in X<sub>i</sub> and (q, s) in X<sub>j</sub> indicates
     *      that (p, s) in X<sub>i</sub> [note final transition]
     *    </br>
     *    2. X<sub>j</sub> (X<sub>j</sub>)<sup>+</sup> = X<sub>j</sub>
     *      i.e., (p, q) in X<sub>j</sub> and (q, s) in X<sub>j</sub> indicates
     *      that (p, s) in X<sub>j</sub> [note final transition]
     *    </br>
     *  where L(D<sub>i</sub>) = X<sub>i</sub>
     *  
     *  defined in "BÜCHI COMPLEMENTATION AND SIZE-CHANGE TERMINATION"
     *  by SETH FOGARTY a AND MOSHE Y. VARDI in LMCS 2012
     * **/
    public boolean isProper(Profile pj) {
        // get all successors visited by initial states
        Profile pi = this;
        for(int s = 0; s < mBuchi.getStateSize(); s ++) {
            for(int p = 0; p < mBuchi.getStateSize(); p ++) {
                RunSet runIp = pi.get(p);
                RunSet runJp = pj.get(p);
                
                boolean iPsNotFinal = runIp.contains(s, false);
                boolean iPsFinal = runIp.contains(s, true);
                boolean jPsNotFinal = runJp.contains(s, false);
                boolean jPsFinal = runJp.contains(s, true);
                
                boolean hasIPsNotFinal = false;
                boolean hasIPsFinal = false;
                boolean hasJPsNotFinal = false;
                boolean hasJPsFinal = false;
                
                // there exists a q state
                for(int q = 0; q < mBuchi.getStateSize(); q ++) {
                    RunSet runJq = pj.get(q);
                    boolean ipq = runIp.contains(q, false);
                    boolean jqs = runJq.contains(s, false);
                    if(!hasIPsNotFinal && ipq && jqs) {
                        hasIPsNotFinal = true;
                    }
                    
                    boolean jqsFinal = runJq.contains(s, true);
                    if(!hasIPsFinal && ipq && jqs
                      && (runIp.contains(q, true) || jqsFinal)) {
                              hasIPsFinal = true;
                    }
                    
                    boolean jpq = runJp.contains(q, false);
                    if(!hasJPsNotFinal && jpq && jqs) {
                        hasJPsNotFinal = true;
                    }
                    
                    if(!hasJPsFinal && jpq && jqs
                            && (runJp.contains(q, true) || jqsFinal)) {
                        hasJPsFinal = true;
                    }
                }
                if (iPsNotFinal != hasIPsNotFinal || iPsFinal != hasIPsFinal
                  || jPsNotFinal != hasJPsNotFinal || jPsFinal != hasJPsFinal)
                    return false;
            }
        }
        return true;
    }

}
