package test;

import automata.Buchi;
import automata.IBuchi;
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
	
	   public static Buchi getA1() {
	        
	        Buchi buchi = new Buchi(2);
	        IState aState = buchi.addState();
	        IState bState = buchi.addState();
	        IState cState = buchi.addState();
	        
	        aState.addSuccessor(0, aState.getId()); 
	        aState.addSuccessor(1, aState.getId()); 
	        aState.addSuccessor(1, bState.getId());     

	        bState.addSuccessor(1, bState.getId());
//	      bState.addSuccessor(0, aState.getId());
	        bState.addSuccessor(0, cState.getId());
	        
	        cState.addSuccessor(0, cState.getId());
	        cState.addSuccessor(1, cState.getId());
	        
	        buchi.setFinal(bState);
	        buchi.setFinal(cState);
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
	
	   public static Buchi getB1() {
	        Buchi buchi = new Buchi(2);
	        IState aState = buchi.addState();
	        IState bState = buchi.addState();
	        IState cState = buchi.addState();
	        
	        aState.addSuccessor(0, bState.getId());     

	        bState.addSuccessor(0, bState.getId());
	        bState.addSuccessor(1, aState.getId());
	        
	        aState.addSuccessor(1, cState.getId());
	        cState.addSuccessor(0, cState.getId());
            cState.addSuccessor(1, cState.getId());
	        
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
	
	
	public static Buchi getF() {
        Buchi A = new Buchi(2);
        A.addState();
        A.addState();
        A.addState();
        A.addState();
        
        A.setInitial(0);
        A.setFinal(1);
        A.setFinal(2);
        
        // 
        A.getState(0).addSuccessor(0, 0);
        A.getState(0).addSuccessor(1, 0);
        A.getState(0).addSuccessor(0, 1);
        A.getState(0).addSuccessor(1, 2);
        
        A.getState(1).addSuccessor(0, 1);
        A.getState(1).addSuccessor(1, 3);
        
        A.getState(2).addSuccessor(0, 3);
        A.getState(2).addSuccessor(1, 2);
        
        A.getState(3).addSuccessor(0, 3);
        A.getState(3).addSuccessor(1, 3);
        return A;
	}
	
	public static Buchi getG() {
        Buchi buchi = new Buchi(2);
        
        buchi.addState();
        buchi.addState();
        buchi.addState();
        
        buchi.getState(0).addSuccessor(0, 0);
        buchi.getState(0).addSuccessor(1, 0);
        buchi.getState(0).addSuccessor(0, 1);
        buchi.getState(0).addSuccessor(1, 1);
        
        buchi.getState(1).addSuccessor(0, 2);
        buchi.getState(1).addSuccessor(1, 1);
        
        buchi.getState(2).addSuccessor(0, 2);
        buchi.getState(2).addSuccessor(1, 2);
        
        buchi.setFinal(1);
        buchi.setInitial(0);
        return buchi;
	}
	
    public static Buchi getH() {
        Buchi buchi = new Buchi(2);

        buchi.addState();
        buchi.addState();
        buchi.addState();
        buchi.addState();

        buchi.getState(0).addSuccessor(0, 0);
        buchi.getState(0).addSuccessor(1, 0);
        buchi.getState(0).addSuccessor(0, 1);

        buchi.getState(1).addSuccessor(0, 2);
        buchi.getState(1).addSuccessor(0, 3);

        buchi.getState(2).addSuccessor(0, 2);
        buchi.getState(2).addSuccessor(1, 1);
        
        buchi.getState(3).addSuccessor(1, 2);

        buchi.setFinal(2);
        buchi.setInitial(0);
        return buchi;
    }

}
