package test.random;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import automata.RandomBuchiGenerator;

public class TestRandomGenerator {

	@Test
	public void testSemideterministic() {
		for (int i = 0; i < 1000000; i++) {
			assertEquals(true, RandomBuchiGenerator.getRandomSemideterministicBuchiAutomaton(100, 20, 10, 10, 10).isSemiDeterministic());
		}
	}
}
