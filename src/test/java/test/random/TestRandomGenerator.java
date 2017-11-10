package test.random;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
