package test.random;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import automata.Buchi;
import automata.Gba;
import automata.GbaToBa;
import automata.IBuchi;
import automata.IGba;
import automata.RandomBuchiGenerator;
import main.Options;
import operation.difference.Difference;
import operation.isempty.IsEmpty;
import operation.minus.Minus;

public class TestRandomGenerator {

	@Test
	public void testSemideterministic() {
		for (int i = 0; i < 20; i++) {
			assertEquals(true, RandomBuchiGenerator.getRandomSemideterministicBuchiAutomaton(100, 20, 10, 10, 10).isSemiDeterministic());
		}
	}
	
	public static IGba getGba() {
	    Gba gba = new Gba(2);
	    final int size = 10;
	    for(int i = 0; i < size; i ++) {
	        gba.addState();
	    }
	    gba.setAccSize(1);
	    gba.getState(0).addSuccessor(0, 1);
	    gba.getState(0).addSuccessor(0, 4);
	    gba.setFinal(0, 0);
	    
	    gba.getState(1).addSuccessor(0, 8);
	    
	    gba.getState(2).addSuccessor(0, 1);
	    gba.getState(2).addSuccessor(1, 0);
	    
	    gba.getState(3).addSuccessor(0, 5);
	    gba.getState(3).addSuccessor(1, 5);
	    gba.getState(3).addSuccessor(0, 0);
	    
	    gba.getState(4).addSuccessor(0, 6);
	    gba.getState(4).addSuccessor(1, 7);
	    
	    gba.setInitial(5);
	    gba.getState(5).addSuccessor(1, 7);
	    
	    gba.getState(6).addSuccessor(1, 2);
	    gba.getState(6).addSuccessor(1, 3);
	    gba.setFinal(6, 0);
	    
	    gba.getState(7).addSuccessor(0, 5);
	    gba.getState(7).addSuccessor(0, 2);
	    gba.getState(7).addSuccessor(1, 2);
	    gba.getState(7).addSuccessor(1, 4);
	    gba.setFinal(7, 0);
	    
	    gba.getState(8).addSuccessor(0, 0);
	    gba.getState(8).addSuccessor(0, 6);
	    
	    gba.getState(9).addSuccessor(1, 5);
	    
	    return gba;
	}
	
	public static IBuchi getBuchi() {
	    Buchi buchi = new Buchi(2);
	    final int size = 5;
	    for(int i = 0; i < size; i ++) {
	        buchi.addState();
	    }
	    
	    buchi.getState(0).addSuccessor(0, 4);
	    buchi.getState(0).addSuccessor(1, 1);
	    buchi.setInitial(0);
	    
	    buchi.getState(1).addSuccessor(0, 1);
	    buchi.getState(1).addSuccessor(0, 2);
	    
	    buchi.getState(2).addSuccessor(0, 3);
	    buchi.getState(2).addSuccessor(1, 4);
	    buchi.setFinal(2);
	    
	    buchi.getState(3).addSuccessor(1, 2);
	    
	    buchi.getState(4).addSuccessor(0, 2);
	    buchi.getState(4).addSuccessor(1, 2);
	    
	    return buchi;
	}
	
	@Test
	public void testDifference() {
	    Options.mLazyS = true;
	    IGba program = getGba();
	    IBuchi ce = getBuchi();
	    Difference difference = new Difference(program, ce);
        difference.explore();
	}
	
	@Test
	public void testNCSB() {
		Options.mLazyS = true;
		while (true) {
			IGba program = RandomBuchiGenerator.getRandomGeneralizedBuchiAutomaton(10, 2, 3, 2, 2);
			IBuchi ce = RandomBuchiGenerator.getRandomSemideterministicBuchiAutomaton(5, 3, 2, 1, 2);
			Difference difference = new Difference(program, ce);
			difference.explore();
			GbaToBa gba2ba = new GbaToBa(program);
			Minus minus = new Minus(gba2ba, ce);
			Boolean diff_is_e = difference.isEmpty();
			IsEmpty minus_is_e = new IsEmpty(minus.getResult());
			if (!(diff_is_e.equals(minus_is_e.getResult()))) {
				System.out.println("Found difference in behaviour");
				System.out.println("Program automaton:\n\n" + program.toDot());
				System.out.println("\n\nCE automaton:\n\n" + ce.toDot());
				System.out.println("\n\nNCSB difference automaton:\n\n" + difference.toDot());
				System.out.println("\n\nMinus automaton:\n\n" + minus.getResult().toDot());
				System.out.println("Difference is empty? " + diff_is_e);
                System.out.println("Minus is empty? " + minus_is_e.getResult());
				assertEquals(false, true);
			}
		}
	}
}
