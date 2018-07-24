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

package operation.complement.rank;

import automata.IBuchi;
import util.ISet;
import util.UtilISet;

public class UtilRank {
    
    private UtilRank() {
        
    }
    
    public static ISet collectSuccessors(IBuchi buchi, ISet set, int letter) {
        ISet succs = UtilISet.newISet();
        for(final int s :  set) {
            succs.or(buchi.getState(s).getSuccessors(letter));
        }
        return succs;
    }
    
    public static LevelRankingConstraint getRankedConstraint(IBuchi buchi, LevelRanking lvlRank, int letter) {
        LevelRankingConstraint constraint = new LevelRankingConstraint();
        for (final int s : lvlRank.getS()) {
            for (final int t : buchi.getState(s).getSuccessors(letter)) {
                constraint.addConstraint(t, lvlRank.getLevelRank(s), lvlRank.isInO(s),
                        lvlRank.isOEmpty());
            }
        }
        return constraint;
    }

}
