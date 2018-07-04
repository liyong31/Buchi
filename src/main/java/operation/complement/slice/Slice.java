package operation.complement.slice;

import operation.complement.tuple.Color;
import operation.complement.tuple.OrderedSets;


/**
 * Color definition in the paper
 *    NONE -   ? undefined
 *    ZERO -   0 belong to skeleton (accepting in the original Buchi automaton, guessed to die out)
 *    ONE  -   1 non-skeleton       (nonaccepting, guessed to be infinite branch)
 *    TWO  -   * put on hold        (newly emerging branch being marked)
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

}
