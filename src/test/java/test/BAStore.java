package test;

import automata.Buchi;
import automata.IState;

public class BAStore {
	
	public static Buchi getA() {
		
		Buchi buchi = new Buchi(2);
		IState aState = buchi.addState();
		IState bState = buchi.addState();
		
		aState.addSuccessor(0, aState.getId());	
		aState.addSuccessor(0, bState.getId());		

		bState.addSuccessor(0, bState.getId());
//		bState.addSuccessor(0, aState.getId());
		bState.addSuccessor(1, aState.getId());
		bState.addSuccessor(0, aState.getId());
		
		buchi.setFinal(bState);
		buchi.setInitial(aState);
		
		return buchi;
	}
	
	public static Buchi getB() {
		Buchi buchi = new Buchi(2);
		IState aState = buchi.addState();
		IState bState = buchi.addState();
		
		aState.addSuccessor(0, bState.getId());		

		bState.addSuccessor(0, bState.getId());
		bState.addSuccessor(1, aState.getId());
		
		buchi.setFinal(bState);
		buchi.setInitial(aState);
		
		return buchi;
	}
	
	public static Buchi getC() {
		Buchi buchi = new Buchi(2);
		IState aState = buchi.addState();
		IState bState = buchi.addState();
		
		aState.addSuccessor(0, bState.getId());		

		bState.addSuccessor(1, aState.getId());
		
		buchi.setFinal(bState);
		buchi.setInitial(aState);
		return buchi;
	}
	
	// full set
	public static Buchi getD() {
		Buchi buchi = new Buchi(1);
		IState aState = buchi.addState();
		
		aState.addSuccessor(0, aState.getId());		
		
		buchi.setFinal(aState);
		buchi.setInitial(aState);
		return buchi;
	}
	
	// empty
	public static Buchi getE() {
		Buchi buchi = new Buchi(1);
		IState aState = buchi.addState();
		
		aState.addSuccessor(0, aState.getId());	
		
		buchi.setInitial(aState);
		return buchi;
	}

}
