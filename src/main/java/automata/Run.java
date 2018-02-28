/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi which is a simple version of SemiBuchi.
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

package automata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Run {
    
    List<Integer> mRun;
    List<Integer> mWord;
    
    public Run() {
        mRun = new ArrayList<>();
        mWord = new ArrayList<>();
    }
    
    public Run(Run run) {
        this();
        this.mRun.addAll(run.mRun);
        this.mWord.addAll(run.mRun);
    }
    
    public void append(int source, int letter, int target) {
        if(mRun.isEmpty()) {
            mRun.add(source);
            mRun.add(target);
            mWord.add(letter);
            return ;
        }
        // if it is not empty
        checkLastStateConsistency(source);
        mRun.add(target);
        mWord.add(letter);
    }
    
    public void preappend(int source, int letter, int target) {
        if(mRun.isEmpty()) {
            mRun.add(source);
            mRun.add(target);
            mWord.add(letter);
            return ;
        }
        // if it is not empty
        checkFirstStateConsistency(target);
        List<Integer> run = new ArrayList<>();
        run.add(source);
        run.addAll(mRun);
        mRun = run;
        List<Integer> word = new ArrayList<>();
        word.add(letter);
        word.addAll(mWord);
        mWord = word;
    }
    
    public void concatenate(Run run) {
        checkConsistency(getLastState(), run.getFirstState());
        List<Integer> states = new ArrayList<>();
        states.addAll(mRun);
        states.remove(mRun.size() - 1);
        states.addAll(run.mRun);
        mRun = states;
        List<Integer> letters = new ArrayList<>();
        letters.addAll(mWord);
        letters.addAll(run.mWord);
        mWord = letters;
    }
    
    public int getLastState() {
        return getStateAt(mRun.size() - 1);
    }
    
    public int getLastLetter() {
        return getLetterAt(mWord.size() - 1);
    }
    
    public int getStateAt(int index) {
        return getAt(mRun, index);
    }
    
    public int getLetterAt(int index) {
        return getAt(mWord, index);
    }
    
    public int getFirstState() {
        return getStateAt(0);
    }
    
    public int getFirstLetter() {
        return getLetterAt(0);
    }
    
    public List<Integer> getWord() {
        return Collections.unmodifiableList(mWord);
    }
    
    public List<Integer> getSequence() {
        return Collections.unmodifiableList(mRun);
    }
    
    private int getAt(List<Integer> list, int index) {
        if(index < 0 || index >= list.size()) 
            return -1;
        return list.get(index);
    }
    
    private void checkFirstStateConsistency(int state) {
        checkConsistency(getFirstState(), state);
    }
    
    private void checkLastStateConsistency(int state) {
        checkConsistency(getLastState(), state);
    }
    
    private void checkConsistency(int fstState, int sndState) {
        if(fstState != sndState)
            throw new RuntimeException("States not consistent");
    }
    
    public boolean isEmpty() {
        return mRun.size() == 0;
    }
    
    public int stateSize() {
        return mRun.size();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(isEmpty()) return "[]";
        builder.append("[ s" + getFirstState());
        for(int i = 0; i < stateSize() - 1; i ++) {
            builder.append("-L" + getLetterAt(i) + "-> s" + getStateAt(i + 1));
        }
        builder.append("]");
        return builder.toString();
    }
    

}
