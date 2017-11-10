package operation.difference;

import automata.Gba;
import automata.IBuchi;
import automata.IGba;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.complement.Complement;
import operation.minus.IMminus;

/**
 * We use Antichain to compute the difference 
 * */
public class Difference extends Gba implements IMminus {
    
    private final IGba mFstOperand;
    private final IBuchi mSndOperand;
    private final Complement mSndComplement;
    private final TObjectIntMap<ProductState> mStateMap;
    
    public Difference(IGba fstOperand, IBuchi sndOperand) {
        super(fstOperand.getAlphabetSize());
        assert fstOperand.getAlphabetSize() == sndOperand.getAlphabetSize();
        this.mFstOperand = fstOperand;
        this.mSndOperand = sndOperand;
        this.mSndComplement = new Complement(sndOperand);
        this.mStateMap = new TObjectIntHashMap<>();
        this.mAccSize = fstOperand.getAccSize() + 1;
        computeInitialStates();
    }
    
    public void explore() {
        new AsccAntichain(this);
    }

    private void computeInitialStates() {
        for(final int fst : mFstOperand.getInitialStates()) {
            for(final int snd : mSndComplement.getInitialStates()) {
                ProductState state = this.getOrAddState(fst, snd);
                this.setInitial(state.getId());
            }
        }
    }
    
    

    @Override
    public IGba getFirstOperand() {
        return mFstOperand;
    }

    @Override
    public IBuchi getSecondOperand() {
        return mSndOperand;
    }

    @Override
    public IGba getResult() {
        return this;
    }

    @Override
    public String getName() {
        return "Difference";
    }

    @Override
    public Complement getSecondComplement() {
        return mSndComplement;
    }
    
    protected ProductState getProductState(int state) {
        return (ProductState) getState(state);
    }

    protected ProductState getOrAddState(int fst, int snd) {
        ProductState prod = new ProductState(this, fst, snd, 0);
        if(mStateMap.containsKey(prod)) {
            return getProductState(mStateMap.get(prod));
        }
        // add new state
        ProductState newState = new ProductState(this, fst, snd, getStateSize());
        int id = this.addState(newState);
        mStateMap.put(newState, id);
        // compute acceptig labels
        computeAcceptanceLabel(newState); 
        return newState;
    }
    
    private void computeAcceptanceLabel(ProductState state) {
        for(int index : mFstOperand.getAccSet(state.getFirstState())) {
            state.setFinal(index);
        }
        int fstSize = mFstOperand.getAccSize();
        if(mSndComplement.isFinal(state.getSecondState())) {
            state.setFinal(fstSize);
        }
    }

}
