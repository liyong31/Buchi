package automata;

public class LassoRun {
    
    private final Run mStem;
    private final Run mLoop;
    
    public LassoRun(Run stem, Run loop) {
        mStem = stem;
        mLoop = loop;
    }
    
    public Run getStem() {
        return mStem;
    }
    
    public Run getLoop() {
        return mLoop;
    }
    
    @Override
    public String toString() {
        return "(" + mStem + ", " + mLoop + ")";
    }
    

}
