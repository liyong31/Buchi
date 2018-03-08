package operation.difference;

import automata.GeneralizedBuchi;
import automata.IBuchi;
import automata.IGeneralizedBuchi;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import operation.complement.ncsb.ComplementNcsb;
import operation.minus.IMminus;

/**
 * We use Antichain to compute the difference 
 * */
public class Difference extends GeneralizedBuchi implements IMminus {
    
    private final IGeneralizedBuchi mFstOperand;
    private final IBuchi mSndOperand;
    private final ComplementNcsb mSndComplement;
    private final TObjectIntMap<ProductState> mStateMap;
    private Boolean mIsEmpty;
    
    public Difference(IGeneralizedBuchi fstOperand, IBuchi sndOperand) {
        super(fstOperand.getAlphabetSize());
        assert fstOperand.getAlphabetSize() == sndOperand.getAlphabetSize();
        this.mFstOperand = fstOperand;
        this.mSndOperand = sndOperand;
        this.mSndComplement = new ComplementNcsb(sndOperand);
        this.mStateMap = new TObjectIntHashMap<>();
        this.mAccSize = fstOperand.getAccSize() + 1;
        computeInitialStates();
    }
    
    public void explore() {
        AsccAntichain ascc = new AsccAntichain(this);
        mIsEmpty = ascc.mIsEmpty;
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
    public IGeneralizedBuchi getFirstOperand() {
        return mFstOperand;
    }

    @Override
    public IBuchi getSecondOperand() {
        return mSndOperand;
    }

    @Override
    public IGeneralizedBuchi getResult() {
        return this;
    }
    
    public Boolean isEmpty() {
        return mIsEmpty;
    }

    @Override
    public String getName() {
        return "Difference";
    }

    @Override
    public ComplementNcsb getSecondComplement() {
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
