/*
 * Written by Andrea Turrini (turrini@ios.ac.cn)
 * This file is part of the Buchi.
 * 
 * Buchi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Buchi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Buchi. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package automata;

import java.util.Random;

public class RandomBuchiGenerator {
	
	private static final Random rg = new Random();

	public static IBuchi getRandomSemideterministicBuchiAutomaton(
			final int stateSetSize,
			final int deterministicStateSetSize,
			final int alphabetSize, 
			final int acceptingStates, 
			final double density) {

		final int nondeterministicStateSetSize = stateSetSize - deterministicStateSetSize;
		final int transitionSetSize = (int) (stateSetSize * density);
		final IBuchi automaton = new Buchi(alphabetSize);
		
		for (int i = 0; i < stateSetSize; i++) {
			automaton.addState(automaton.makeState(i));
		}

		automaton.setInitial(rg.nextInt(stateSetSize));

		// the deterministic states are all in the range 
		// [ nondeterministicStateSetSize, stateSetSize)
		// so the accepting states should be all in this range 
		for (int i = 0; i < acceptingStates; i++) {
			automaton.setFinal(nondeterministicStateSetSize + rg.nextInt(deterministicStateSetSize));
		}
		
		for (int i = 0; i < transitionSetSize; i++) {
			final IState source = automaton.getState(rg.nextInt(stateSetSize));
			final int action = rg.nextInt(alphabetSize);
			if (source.getId() >= nondeterministicStateSetSize) {
				// source is a deterministic state, so it can't enable again the same letter 
				if (source.getEnabledLetters().contains(action)) {
					// the transition is not created, so we have to try again
					i--;
				} else {
					// the deterministic states are all in the range 
					// [ nondeterministicStateSetSize, stateSetSize)
					// so the target state should be all in this range 
					source.addSuccessor(action, nondeterministicStateSetSize + rg.nextInt(deterministicStateSetSize));
				}
			} else {
				source.addSuccessor(action, rg.nextInt(stateSetSize));
			}
		}
	
		return automaton;
	}
	
	public static IGeneralizedBuchi getRandomGeneralizedBuchiAutomaton(
			final int stateSetSize, 
			final int alphabetSize, 
			final int acceptingStates, 
			final double density, 
			final int acceptingSets) {

		final int transitionSetSize = (int) (stateSetSize * density);
		final GeneralizedBuchi automaton = new GeneralizedBuchi(alphabetSize);
		automaton.setAccSize(acceptingSets);
		
		for (int i = 0; i < stateSetSize; i++) {
			automaton.addState(automaton.makeState(i));
		}

		automaton.setInitial(rg.nextInt(stateSetSize));

		for (int i = 0; i < acceptingStates; i++) {
			automaton.setFinal(rg.nextInt(stateSetSize), rg.nextInt(acceptingSets));
		}
		
		for (int i = 0; i < transitionSetSize; i++) {
			automaton
				.getState(rg.nextInt(stateSetSize))
				.addSuccessor(rg.nextInt(alphabetSize), rg.nextInt(stateSetSize));
		}
	
		return automaton;
	}

	public static IBuchi getRandomBuchiAutomaton(
			final int stateSetSize, 
			final int alphabetSize, 
			final int acceptingStates, 
			final double density) {

		final int transitionSetSize = (int) (stateSetSize * density);
		final IBuchi automaton = new Buchi(alphabetSize);
		
		for (int i = 0; i < stateSetSize; i++) {
			automaton.addState(automaton.makeState(i));
		}

		automaton.setInitial(rg.nextInt(stateSetSize));

		for (int i = 0; i < acceptingStates; i++) {
			automaton.setFinal(rg.nextInt(stateSetSize));
		}
		
		for (int i = 0; i < transitionSetSize; i++) {
			automaton
				.getState(rg.nextInt(stateSetSize))
				.addSuccessor(rg.nextInt(alphabetSize), rg.nextInt(stateSetSize));
		}
	
		return automaton;
	}
	
}
