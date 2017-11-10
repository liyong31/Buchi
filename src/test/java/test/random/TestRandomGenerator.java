package test.random;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;

import automata.BaToGba;
import automata.Buchi;
import automata.Gba;
import automata.IBuchi;
import automata.IGba;
import automata.RandomBuchiGenerator;
import main.Options;
import operation.difference.Difference;
import operation.isempty.IsEmpty;
import operation.minus.Minus;

public class TestRandomGenerator {

	@Ignore
	@Test
	public void testSemideterministic() {
		for (int i = 0; i < 1000000; i++) {
			assertEquals(true, RandomBuchiGenerator.getRandomSemideterministicBuchiAutomaton(100, 20, 10, 10, 10).isSemiDeterministic());
		}
	}
	
	@Test
	public void testBug() {
		Options.mLazyS = true;

		IGba program = new Gba(2);
		program.addState();
		program.addState();
		program.setInitial(0);

		program.setAccSize(1);
		program.setFinal(0,0);
		program.setFinal(1,0);
		
		program.getState(0).addSuccessor(0, 0);
		program.getState(0).addSuccessor(0, 1);
		program.getState(1).addSuccessor(1, 0);
		program.getState(1).addSuccessor(1, 1);
		
		IBuchi ce = new Buchi(2);
		ce.addState();
		ce.addState();
		ce.addState();
		ce.addState();
		ce.setInitial(0);
		ce.setFinal(3);
		ce.getState(0).addSuccessor(0, 2);
		ce.getState(0).addSuccessor(0, 3);
		ce.getState(1).addSuccessor(0, 1);
		ce.getState(1).addSuccessor(1, 1);
		ce.getState(1).addSuccessor(1, 2);
		ce.getState(2).addSuccessor(0, 3);
		ce.getState(3).addSuccessor(0, 2);
		ce.getState(3).addSuccessor(1, 2);
		
		IBuchi cerid = new Buchi(2);
		cerid.addState();
		cerid.addState();
		cerid.addState();
		cerid.setInitial(0);
		cerid.setFinal(2);
		cerid.getState(0).addSuccessor(0, 1);
		cerid.getState(0).addSuccessor(0, 2);
		cerid.getState(1).addSuccessor(0, 2);
		cerid.getState(2).addSuccessor(0, 1);
		cerid.getState(2).addSuccessor(1, 1);
		
		Difference difference = new Difference(program, cerid);
		difference.explore();
		System.out.println("Difference is empty: " + difference.isEmpty());
	}
	
	@Ignore
	@Test
	public void testNCSB() {
		Options.mLazyS = true;
		while (true) {
			IBuchi program = RandomBuchiGenerator.getRandomBuchiAutomaton(2, 2, 2, 2);
			IGba programGBA = new BaToGba(program);
			IBuchi ce = RandomBuchiGenerator.getRandomSemideterministicBuchiAutomaton(4, 2, 2, 1, 2);
			Difference difference = new Difference(programGBA, ce);
			try {
				difference.explore();
			} catch (Throwable t) {
				System.out.println("Program automaton:\n\n" + program.toDot());
				System.out.println("Program automaton as GBA:\n\n" + programGBA.toDot());
				System.out.println("\n\nCE automaton:\n\n" + ce.toDot());
			}
			Minus minus = new Minus(program, ce);
			Boolean diff_is_e = difference.isEmpty();
			IsEmpty minus_is_e = new IsEmpty(minus.getResult());
			if (!(diff_is_e.equals(minus_is_e.getResult()))) {
				System.out.println("Found difference in behaviour");
				System.out.println("Program automaton:\n\n" + program.toDot());
				System.out.println("Program automaton as GBA:\n\n" + programGBA.toDot());
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
