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

package operation.complement.slice;

import operation.complement.tuple.Color;
import operation.complement.tuple.OrderedSets;


/**
 * Decoration definition in the paper
 *    NONE -   ? undefined
 *    ZERO -   0 belong to skeleton (runs have to die out before next reset slice)
 *    ONE  -   1 non-skeleton       (runs are in the infinite branches)
 *    TWO  -   * put on hold        (newly emerging branch from infinite branches being marked
 *                                   to die out after next reset slice)
 * Reset slice is the slice whose components do not have 0-decoration
 * 
 * */
public class Slice extends OrderedSets {

    public Slice(boolean colored) {
        super(colored);
    }
    
    @Override
    public boolean isFinal()  {
        if(this.isColored()) {
            for(final Color color : mColors) {
                if(color == Color.ZERO) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * Only components labelled with 0 and * can be merged
     * */
    @Override
    protected boolean isMergeableColor(Color color) {
        return color == Color.ZERO || color == Color.TWO;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<");
        for (int i = 0; i < mOSets.size(); i++) {
            builder.append("(" + mOSets.get(i) + "," + toString(getColor(i)) + ")");
            if(i != mOSets.size() - 1) {
                builder.append(",");
            }
        }
        builder.append(">");
        return builder.toString(); 
    }
    
    private String toString(Color color) {
        String result;
        switch(color) {
        case NONE:
            result = "?";
            break;
        case ZERO:
            result = "0";
            break;
        case ONE:
            result = "1";
            break;
        case TWO:
            result = "*";
            break;
        default:
            throw new UnsupportedOperationException("Unsupported color");
        }
        return result;
    }
    
    @Override
    public Color getColor(int index) {
        if(mColors.isEmpty()) return Color.NONE;
        return this.mColors.get(index);
    }
    
    public static Color getDieout() {
        return Color.ZERO;
    }
    
    public static Color getInfinite() {
        return Color.ONE;
    }
    
    public static Color getMarked() {
        return Color.TWO;
    }
    
    public static Color getNone() {
        return Color.NONE;
    }

}
