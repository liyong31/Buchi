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

package operation.explore;

import java.util.LinkedList;

import automata.IBuchi;
import automata.IState;
import operation.IUnaryOp;
import util.ISet;
import util.UtilISet;

public class Explore implements IUnaryOp<IBuchi, IBuchi> {
    
    protected final IBuchi mOperand;
    protected boolean mExplored = false;
    
    public Explore(IBuchi operand) {
        mOperand = operand;
        explore();
    }

    @Override
    public IBuchi getOperand() {
        return mOperand;
    }

    @Override
    public IBuchi getResult() {
        return mOperand;
    }
    
    protected void explore() {
        
        if(mExplored) return ;
        
        mExplored = true;
        
        LinkedList<IState> walkList = new LinkedList<>();
        for(int init : mOperand.getInitialStates()) {
            walkList.addFirst(mOperand.getState(init));
        }
        
        ISet visited = UtilISet.newISet();
        
        while(! walkList.isEmpty()) {
            IState s = walkList.remove();
            if(visited.get(s.getId())) continue;
            visited.set(s.getId());
            
            for(int letter = 0; letter < mOperand.getAlphabetSize(); letter ++) {
                for(int succ : s.getSuccessors(letter)) {
                    if(! visited.get(succ)) {
                        walkList.addFirst(mOperand.getState(succ));
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return "DFS explore";
    }

}
