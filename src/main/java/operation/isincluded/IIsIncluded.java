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

package operation.isincluded;

import automata.IBuchi;
import operation.IBinaryOp;
import operation.complement.ncsb.ComplementNcsbOtf;
import operation.complement.ncsb.StateNcsbOtf;

public interface IIsIncluded extends IBinaryOp<IBuchi, Boolean>{
    ComplementNcsbOtf getSecondComplement();
    StateNcsbOtf getComplementState(int state);
    default String getName() {
        return "IsIncluded";
    }
}