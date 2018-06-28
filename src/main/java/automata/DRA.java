package automata;

public class DRA extends DOA {
    private final AccRA mAcc;
    
    public DRA(int alphabetSize) {
        super(alphabetSize);
        this.mAcc = new AccRA();
    }

    @Override
    public AccRA getAcceptance() {
        return mAcc;
    } 

}
