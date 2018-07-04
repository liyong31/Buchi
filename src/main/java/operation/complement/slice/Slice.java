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
