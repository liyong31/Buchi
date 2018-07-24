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

package operation.accept;

import java.util.List;

import automata.IBuchi;
import operation.UnaryOp;
import operation.intersect.Intersect;
import operation.isempty.IsEmpty;

public class Accept extends UnaryOp<IBuchi, Boolean>{
    private final IsEmpty mIsEmpty;
    public Accept(IBuchi operand, List<Integer> stem, List<Integer> loop) {
        super(operand);
        IBuchi lasso = new BuchiLasso(operand.getAlphabetSize(), stem, loop);
        Intersect intersect = new Intersect(operand, lasso);
        mIsEmpty = new IsEmpty(intersect);
    }

    @Override
    public String getName() {
        return "Accept";
    }
    
    public Boolean getResult() {
        return !mIsEmpty.getResult();
    }

}
