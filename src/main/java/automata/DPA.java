package automata;

public class DPA extends DOA {
    private final AccPA mAcc;
    
    public DPA(int alphabetSize) {
        super(alphabetSize);
        this.mAcc = new AccPA();
    }

    @Override
    public AccPA getAcceptance() {
        return mAcc;
    }

}
