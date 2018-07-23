package test.random;

import static org.junit.Assert.assertEquals;

import java.io.PrintStream;

import org.junit.Test;
import org.junit.Ignore;

import automata.Buchi2GeneralizedBuchi;
import automata.Buchi;
import automata.GeneralizedBuchi;
import automata.IBuchi;
import automata.IGeneralizedBuchi;
import automata.IGeneralizedState;
import automata.IState;
import automata.RandomBuchiGenerator;
import main.Options;
import operation.complement.dba.ComplementDBA;
import operation.complement.ncsb.ComplementNcsbOtf;
import operation.complement.nsbc.ComplementNsbc;
import operation.complement.slice.ComplementSliceVW;
import operation.complement.tuple.ComplementTuple;
import operation.difference.Difference;
import operation.explore.Explore;
import operation.explore.OndraExplore;
import operation.isempty.IsEmpty;
import operation.minus.Minus;
import operation.removal.Remove;
import test.BAStore;

public class TestRandomGenerator {

	@Ignore
	@Test
	public void testSemideterministic() {
		for (int i = 0; i < 1000000; i++) {
			assertEquals(true, RandomBuchiGenerator.getRandomSemideterministicBuchiAutomaton(100, 20, 10, 10, 10).isSemiDeterministic());
		}
	}
	
	public static IGeneralizedBuchi getGba() {
	    GeneralizedBuchi gba = new GeneralizedBuchi(2);
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
	    IGeneralizedBuchi program = getGba();
	    IBuchi ce = getBuchi();
	    Difference difference = new Difference(program, ce);
        difference.explore();
	}
	
	@Test
	public void testBug() {
		Options.mLazyS = true;

		IGeneralizedBuchi program = new GeneralizedBuchi(2);
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
		
		System.out.println(program.toDot());
		System.out.println(cerid.toDot());
		
		Difference difference = new Difference(program, cerid);
		difference.explore();
		System.out.println("Difference is empty: " + difference.isEmpty());
	}
	
    @Test
    public void testQPrime() {
        Options.mLazyS = true;
        Options.mAntichain = true;
        IGeneralizedBuchi program = new GeneralizedBuchi(2);
        program.addState();
        program.addState();
        program.setInitial(0);

        program.setAccSize(1);
        program.setFinal(0, 0);
        program.setFinal(1, 0);

        program.getState(0).addSuccessor(0, 0);
        program.getState(0).addSuccessor(0, 1);
        program.getState(0).addSuccessor(1, 1);
        program.getState(1).addSuccessor(0, 0);

        IBuchi ce = new Buchi(2);
        ce.addState();
        ce.addState();
        ce.addState();
        ce.addState();
        ce.setInitial(0);
        ce.setFinal(2);
        ce.getState(0).addSuccessor(0, 0);
        ce.getState(0).addSuccessor(0, 2);
        ce.getState(0).addSuccessor(1, 1);
        ce.getState(1).addSuccessor(0, 1);
        ce.getState(1).addSuccessor(0, 3);
        ce.getState(2).addSuccessor(0, 3);
        ce.getState(2).addSuccessor(1, 3);
        ce.getState(3).addSuccessor(1, 2);

        System.out.println(program.toDot());
        System.out.println(ce.toDot());

        Difference difference = new Difference(program, ce);
        difference.explore();
        System.out.println("Difference is empty: " + difference.isEmpty());
    }
    
    @Test
    public void testOndraExploreSpecific() {
        GeneralizedBuchi program = new GeneralizedBuchi(2);
        program.setAccSize(2);
        program.addState();
        program.addState();
        program.addState();
        program.addState();
        program.addState();
        program.getState(0).addSuccessor(1, 2);
        program.setInitial(1);
        program.getState(1).addSuccessor(0, 0);
        program.getState(1).addSuccessor(1, 0);
        program.setFinal(2,0);
        program.setFinal(2,1);
        program.getState(3).addSuccessor(0, 2);
        program.getState(3).addSuccessor(1, 0);
        program.getState(4).addSuccessor(0, 0);
        program.getState(4).addSuccessor(0, 4);
        program.getState(4).addSuccessor(1, 2);
        program.getState(4).addSuccessor(1, 3);
        new OndraExplore(program);
    }
    
    @Test
    public void testOndraExplore() {
        while (true) {
            IGeneralizedBuchi program = RandomBuchiGenerator.getRandomGeneralizedBuchiAutomaton(4, 2, 2, 2, 2);
            try {
                new OndraExplore(program);
            } catch (Throwable t) {
                System.err.println(t.getMessage());
                System.out.println("\nProgram automaton:\n\n" + program.toDot());
                print(System.out, program, "program");
                System.out.println();
                System.exit(-1);
            }

        }
    }
    
    @Test
    public void testLemma() {
        Options.mLazyS = false;
        Options.mAntichain = false;
        while (true) {
            IBuchi ce = RandomBuchiGenerator.getRandomSemideterministicBuchiAutomaton(5, 3, 2, 1, 2);
            ComplementNcsbOtf complement = new ComplementNcsbOtf(ce);
            new Explore(complement);
            try {
                complement.testLemma();
            }catch (Throwable t) {
                System.exit(-1);
            }
        }
    }
    
    @Test
    public void testNCSB1() {
        Options.mLazyS = false;
        Options.mLazyB = false;
        Options.mVerbose = true;
        IBuchi input = BAStore.getA1();
        ComplementNcsbOtf complement = new ComplementNcsbOtf(input);
        complement.explore();
        System.out.println(complement.toDot());
        System.out.println(complement.toBA());
        
        ComplementNsbc complement2 = new ComplementNsbc(input);
        complement2.explore();
        System.out.println(complement2.toBA());
        System.out.println(complement2.toDot());
    }
    
    @Test
    public void testNCSB2() {
        Options.mLazyS = true;
        Options.mLazyB = true;
        Options.mVerbose = true;
        IBuchi input = BAStore.getF();
        System.out.println(input.toDot());
        ComplementNcsbOtf complement = new ComplementNcsbOtf(input);
        complement.explore();
        System.out.println(complement.toDot());
        System.out.println(complement.toBA());
        
        ComplementNsbc complement2 = new ComplementNsbc(input);
        complement2.explore();
        System.out.println(complement2.toDot());
        System.out.println(complement2.toBA());
    }
    
    @Test
    public void testComplementTuple() {
        IBuchi input = BAStore.getH();
        ComplementTuple complement = new ComplementTuple(input);
        complement.explore();
        System.out.println(complement.toDot());
        System.out.println(complement.toBA());
        IBuchi result = (new Remove(complement)).getResult();
        System.out.println(result.toBA());
    }
    
    @Test
    public void testComplementSliceVW() {
        IBuchi input = BAStore.getH();
        Options.mEnhancedSliceGuess = true;
        ComplementSliceVW complement = new ComplementSliceVW(input);
        complement.explore();
        System.out.println(complement.toDot());
        System.out.println(complement.toBA());
        IBuchi result = (new Remove(complement)).getResult();
        System.out.println(result.toBA());
    }
    
    @Test
    public void testNCSB3() {
        Options.mLazyS = false;
        Options.mLazyB = false;
        Options.mVerbose = true;
        IBuchi input = BAStore.getG();
        ComplementNcsbOtf complement = new ComplementNcsbOtf(input);
        complement.explore();
        System.out.println(complement.toDot());
    }
    
    @Test
    public void testDBAComplement() {
        IBuchi input = BAStore.getB();
        input.makeComplete();
        ComplementDBA complement = new ComplementDBA(input);
        complement.explore();
        System.out.println(input.toDot());
        System.out.println(complement.toDot());
    }
    
	@Test
	public void testNCSB() {
		Options.mLazyS = true;
		Options.mAntichain = true;
		while (true) {
			IBuchi program = RandomBuchiGenerator.getRandomBuchiAutomaton(2, 2, 2, 2);
			IGeneralizedBuchi programGBA = new Buchi2GeneralizedBuchi(program);
			IBuchi ce = RandomBuchiGenerator.getRandomSemideterministicBuchiAutomaton(5, 3, 2, 1, 2);
			Difference difference = new Difference(programGBA, ce);
			try {
				difference.explore();
			} catch (Throwable t) {
			    System.err.println(t.getMessage());
				System.out.println("\nProgram automaton:\n\n" + program.toDot());
				System.out.println("Program automaton as GBA:\n\n" + programGBA.toDot());
				System.out.println("\n\nCE automaton:\n\n" + ce.toDot());
                print(System.out, program, "program");
                System.out.println();
                print(System.out, ce, "ce");
				System.exit(-1);
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
	

	private static void print(PrintStream out, IBuchi ba, String name) {
	    final String gbaStr = "Gba";
	    final String baStr = "Buchi";
	    final String newStr = "new"; 
	    final String addState = "addState";
	    final String setAccSize = "setAccSize";
	    if(ba instanceof IGeneralizedBuchi) {
	        IGeneralizedBuchi gba = (IGeneralizedBuchi)ba;
	        out.println(gbaStr + " " + name + " = " + newStr + " " + gbaStr + "(" + ba.getAlphabetSize() + ");");
	        out.println(name + "." + setAccSize + "(" + gba.getAccSize() + ");");
	    }else {
	        out.println(baStr + " " + name + " = " + newStr + " " + baStr + "(" + ba.getAlphabetSize() + ");");
	    }
	    for(int i = 0; i < ba.getStateSize(); i ++) {
	        out.println(name + "." + addState + "();");
	    }
	    for(IState state : ba.getStates()) {
	        printState(out, ba, state, ba.getAlphabetSize(), name);
	    }
	}
	
	private static void printState(PrintStream out, IBuchi aut, IState state, int apSize, String name) {
	    final String getState = "getState";
	    final String addSucc = "addSuccessor";
	    final String setFinal = "setFinal";
	    final String setInit = "setInitial";
	    final int id = state.getId();
	    if(aut.isInitial(id)) {
	        out.println(name + "." + setInit + "(" + id + ");");
	    }
        if(state instanceof IGeneralizedState) {
            IGeneralizedState gbaState = (IGeneralizedState)state;
            for(final int index : gbaState.getAccSet()) {
                out.println(name + "." + setFinal + "(" + id + "," + index + ");");
            }
        }else {
            out.println(name + "." + setFinal + "(" + id + ");");
        }
	    for(int letter = 0; letter < apSize; letter ++) {
            for(final int succ : state.getSuccessors(letter)) {
                out.println(name + "." + getState + "(" + id + ")." + addSucc + "(" + letter + ", " + succ + ");");
            }
        }
    }
}
