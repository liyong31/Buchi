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

import java.util.LinkedList;

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
    
    /**
     * the rank of the successor is not larger than its predecessor
     * **/
    public static LevelRankingConstraint getRankedConstraint(IBuchi buchi, LevelRanking lvlRank, int letter) {
        LevelRankingConstraint constraint = new LevelRankingConstraint();
        for (final int s : lvlRank.getS()) {
            for (final int t : buchi.getState(s).getSuccessors(letter)) {
                int rank = lvlRank.getLevelRank(s);
                // final state should have even rank
                if(LevelRanking.isOdd(rank) && buchi.isFinal(t)) {
                    rank = Math.max(LevelRanking.ZERO, rank - 1);
                }
                constraint.addConstraint(t, rank, lvlRank.isInO(s), lvlRank.isOEmpty());
            }
        }
        return constraint;
    }
    
    /**
     * compute permutation of an array 
     * **/
    
    public static LinkedList<int[]> permute(int[] array) {
        LinkedList<int[]> perms = new LinkedList<int[]>();
        int[] prefix = new int[0];
        permute(perms, prefix, array);
        return perms;
    }
    
    private static void permute(LinkedList<int[]> permutation, int[] prefix, int[] array) {
        int n = array.length;
        if(n == 0) {
            permutation.addFirst(prefix);
        } else {
            for(int i = 0; i < n; i ++) {
                int[] newPrefix = new int[prefix.length + 1];
                for(int j = 0; j < prefix.length; j ++) {
                    newPrefix[j] = prefix[j];
                }
                // get ith number
                newPrefix[prefix.length] = array[i];
                // remove ith number in the array
                int[] newArray = new int[n - 1];
                for(int j = 0; j < i; j ++) {
                    newArray[j] = array[j];
                }
                for(int j = i + 1; j < n; j ++) {
                    newArray[j-1] = array[j];
                }
                // put the ith number in array as the first element,
                // compute the permuation of remaining numbers
                permute(permutation, newPrefix, newArray);
            }
        }
    }
    
    

}
