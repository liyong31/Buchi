package main;

import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import automata.IBuchi;
import main.Options.Algorithm;
import operation.complement.Complement;
import operation.complement.ncsb.ComplementNcsb;
import operation.complement.ncsb.ComplementNcsbOtf;
import operation.complement.nsbc.ComplementNsbc;
import operation.complement.ramsey.ComplementRamsey;
import operation.complement.rank.ComplementRankKV;
import operation.complement.slice.ComplementSliceVW;
import operation.complement.tuple.ComplementTuple;
import operation.explore.Explore;
import operation.quotient.QuotientSimple;
import util.PairXX;

import util.parser.ParserType;
import util.parser.SingleParser;
import util.parser.UtilParser;
import util.parser.ats.ATSFileParser;

public class Main {
	
	private static final String FILE_EXT = "ats";
	private static final long TIME_LIMIT = 20;
	public static void main(String[] args) throws IOException {
		
		if(args.length < 1) {
			printUsage();
			System.exit(0);
		}
		
		System.gc();
		
		long time = TIME_LIMIT;
		boolean test = false;
		boolean complement = false;
		boolean inclusion = false;
		boolean difference = false;
		String fileOut = null;
		for(int i = 0; i < args.length; i ++) {
			if(args[i].equals("-test")) {
				test = true;
			}else if(args[i].equals("-to")) {
				time = Integer.parseInt(args[i + 1]);
				++ i;
			}else if(args[i].equals("-h")) {
				printUsage();
				System.exit(0);
			}else if(args[i].equals("-v")) {
				Options.mVerbose = true;
			}else if(args[i].equals("-set")) {
				int n = Integer.parseInt(args[i + 1]);
				if(n >= 0 && n <= 4) Options.mSet = n;
				++ i;
			}else if(args[i].equals("-lazys")) {
				Options.mLazyS = true;
			}else if(args[i].equals("-complement")) {
				complement = true;
				fileOut = args[i + 1];
				++ i;	
			}else if(args[i].equals("-lazyb")) {
				Options.mLazyB = true;
			}else if(args[i].equals("-lazyg")) {
			    Options.mEnhancedSliceGuess = true;
			}else if(args[i].equals("-diff")) {
				difference = true;
			}else if(args[i].equals("-incl")) {
				inclusion = true;
			}else if(args[i].equals("-gba")) {
				Options.mGBA = true;
			}else if(args[i].equals("-oe")) {
                Options.mOE = true;
            }else if(args[i].equals("-ca")) {
                Options.mComplete = true;
            }else if(args[i].equals("-ncsb")) {
                Options.mAlgo = Algorithm.NCSB;
            }else if(args[i].equals("-ncsbotf")) {
                Options.mAlgo = Algorithm.NCSBOTF;
            }else if(args[i].equals("-nsbc")) {
                Options.mAlgo = Algorithm.NSBC;
            }else if(args[i].equals("-slice")) {
                Options.mAlgo = Algorithm.SLICE;
            }else if(args[i].equals("-tuple")) {
                Options.mAlgo = Algorithm.TUPLE;
            }else if(args[i].equals("-ramsey")) {
                Options.mAlgo = Algorithm.RAMSEY;
            }else if(args[i].equals("-rank")) {
                Options.mAlgo = Algorithm.RANK;
            }else if(args[i].equals("-rmdead")) {
                Options.mRemoveDead = true;
            }else if(args[i].equals("-mg")) {
                Options.mDirectSimulation = true;
            }
			
		}
		time = time * 1_000; // miliseconds
		if(test) {
//			testBenchmarks(time);
		}else if(complement){
			complementBuchi(args, fileOut, time);
		}else if(inclusion){
			checkInclusion(args, time);
		}else if(difference) {
			computeDifference(args, time);
		}
		
		// force to exit
		System.exit(-1);
	}


	private static void printUsage() {
		
		System.out.println("Buchi v1: Library for Buchi automata");
		System.out.println("\nUsage:\n     <PROP> [options] <ATS-FILE> \n");
		System.out.println("Recommended use: java -jar SemiBuchi.jar -ascc -ac aut.ats");
		System.out.println("\nOptions:");
		System.out.println("-h: Show this page");
		System.out.println("-v: Verbose mode");
		System.out.println("-set k: 0 for BitSet, 1 for SparseBitSet\n"
				          + "       2 for TInSet, 3 for TreeSet and 4 for HashSet");
		System.out.println("-test: Test all benchmarks");
		System.out.println("-lazys: Delay word distribution to S");
		System.out.println("-lazyb: Delay word distribution to B");
		System.out.println("-lazyg: Delay word guess to the accepting component");
		System.out.println("-tarjan: Use Tarjan algorithm");
		System.out.println("-ca: complete input BA first");
		System.out.println("-mg: merge states in NSBC");
		System.out.println("-rabit: Use RABIT tool");
		System.out.println("-ascc: Use ASCC algorithm (Default)");
		System.out.println("-ac: Use Antichain optimization");
		System.out.println("-dfs: Use Double DFS algorithm");
		System.out.println("-diff: Compute Difference of two automata");
		System.out.println("-oe: Explore complement automata using algorithm proposed Ondra");
		System.out.println("-ncs: Prefer smaller N\\/C\\/S ");
		System.out.println("-incl: Check inclusion of two automata");
		System.out.println("-gba: Use generalized Buchi automata");
		System.out.println("-to k: Limit execution in k seconds (20 secs by default)");
		System.out.println("-complement <file-out>: Output complement of the last automaton");
		System.out.println("-ncsb: Original NCSB complementation");
		System.out.println("-ncsbotf: On-the-fly NCSB complementation");
		System.out.println("-nsbc: NCSB complementation");
		System.out.println("-ramsey: Ramsey-based complementation");
		System.out.println("-rank: Rank-based complementation");
		System.out.println("-tuple: Tuple-based complementation");
		System.out.println("-slice: Slice-based complementation");
		System.out.println("-rmdead: Remove dead states");
		
	}
	
	private static void computeDifference(String[] args, long time) {
		
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		if(Options.mVerbose) System.out.println("Time stamp: " + dateFormat.format(new Date()));
//		
//		File file = null;
//		TaskDifference task = null;
//		
//		boolean tarjan = false, antichain = false, ascc = false;
//		for(int i = 0; i < args.length; i ++) {
//			
//			if(args[i].endsWith(FILE_EXT)) {
//				file = new File(args[i]);
//			}if(args[i].equals("-tarjan")) {
//				tarjan = true;
//			}else if(args[i].equals("-ac")) {
//			    antichain = true;
//			}else if(args[i].equals("-ncs")) {
////                Options.smallerNCS = true;
//            }else if(args[i].equals("-ascc")) {
//                ascc = true;
//            }
//		}
//		
//		assert file != null;
//		
//		if(Options.mVerbose) System.out.println("Parsing file " + file.getName() + " ....");
//		ATSFileParser atsParser =  new ATSFileParser();
//		atsParser.parse(file.getAbsolutePath());
//		List<PairXX<IBuchi>> pairs = atsParser.getBuchiPairs();
//				
//		for(PairXX<IBuchi> pair : pairs) {
//			task = new TaskDifference(file.getName());
//			if(tarjan) {
//				task.setOperation(new BuchiWaDifferenceTarjan(pair.getFirst(), pair.getSecond()));
//			}else if(antichain) {
//			    task.setOperation(new BuchiWaDifferenceAntichain(pair.getFirst(), pair.getSecond()));
//			}else if(ascc){
//			    task.setOperation(new BuchiWaDifferenceAscc(pair.getFirst(), pair.getSecond()));
//			}else{
//	             System.err.println("Other algorithms not support yet");
//	             System.exit(-1);
//			}
//			
//            final String opName = "Computing Difference";
//            printTaskBegin(opName, task);
//			RunTask runTask = new RunTask(task, time);
//            runTask.execute();
//            printTaskEnd(task);
//		}
		
	}
	
	private static void checkInclusion(String[] args, long time) {
		
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		if(Options.mVerbose) System.out.println("Time stamp: " + dateFormat.format(new Date()));
//		
//		File file = null;
//		TaskInclusion task = null;
//		
//		boolean tarjan = false, antichain = false, dfs = false, rabit = false;
//		for(int i = 0; i < args.length; i ++) {
//			
//			if(args[i].endsWith(FILE_EXT)) {
//				file = new File(args[i]);
//			}if(args[i].equals("-tarjan")) {
//				tarjan = true;
//			}else if(args[i].equals("-ac")) {
//				antichain = true;
//			}else if(args[i].equals("-dfs")) {
//				dfs = true;
//			}else if(args[i].equals("-rabit")) {
//				rabit = true;
//			}
//		}
//		
//		assert file != null;
//		
//		if(Options.mVerbose) System.out.println("Parsing file " + file.getName() + " ....");
//		ATSFileParser atsParser =  new ATSFileParser();
//		atsParser.parse(file.getAbsolutePath());
//		List<PairXX<IBuchi>> pairs = atsParser.getBuchiPairs();
//				
//		for(PairXX<IBuchi> pair : pairs) {
//			task = new TaskInclusion(file.getName());
//			if(tarjan) {
//				task.setOperation(new BuchiInclusionComplement(pair.getFirst(), pair.getSecond()));
//			}else if(dfs) {
//				System.err.println("Not support yet");
//				System.exit(-1);
//			}else if(rabit){
//				task.setOperation(new BuchiInclusionRABIT(pair.getFirst(), pair.getSecond()));
//			}else {
//				if(antichain) {
//					task.setOperation(new BuchiInclusionASCCAntichain(pair.getFirst(), pair.getSecond()));
//				}else {
//					task.setOperation(new BuchiInclusionASCC(pair.getFirst(), pair.getSecond()));
//				}
//			}
//			
//			RunTask runTask = new RunTask(task, time);
//	        final String opName = "Checking inclusion";
//	        printTaskBegin(opName, task);
//	        runTask.execute();
//	        printTaskEnd(task);
//
//		}
		
	}
	
	
	private static void complementBuchi(String[] args, String fileOut, long time) {
	    
		File fileIn = null;
		SingleParser parser = null;
		for(int i = 0; i < args.length; i ++) {
			if(args[i].endsWith(FILE_EXT)) {
				fileIn = new File(args[i]);
				parser = UtilParser.getSinleParser(ParserType.ATS);
			}
			if(args[i].endsWith(".ba")) {
				fileIn = new File(args[i]);
				parser = UtilParser.getSinleParser(ParserType.BA);
			}
		}
		assert parser != null;
		
		parser.parse(fileIn.getAbsolutePath());
		automata.IBuchi buchi = parser.getBuchi();
        if(Options.mComplete) buchi.makeComplete();
		Complement buchiComplement = null;
		switch(Options.mAlgo) {
		case NCSBOTF:
		    buchiComplement = new ComplementNcsbOtf(buchi);
		    break;
		case RANK:
		    buchiComplement = new ComplementRankKV(buchi);
            break;
		case SLICE:
		    buchiComplement = new ComplementSliceVW(buchi);
		    break;
		case TUPLE:
            buchiComplement = new ComplementTuple(buchi);
            break;
		case RAMSEY:
            buchiComplement = new ComplementRamsey(buchi);
            break;
		case NSBC:
	         buchiComplement = new ComplementNsbc(buchi);
	         break;
		case NCSB:
            buchiComplement = new ComplementNcsb(buchi);
            break;
        default:
            buchiComplement = new ComplementRankKV(buchi);
            break;
		}
		TaskComplement task = new TaskComplement(fileIn.getName());
		task.setOperation(buchiComplement);
		RunTask runTask = new RunTask(task, time);
		final String opName = "Computing complement";
		printTaskBegin(opName, task);
		runTask.execute();
		printTaskEnd(task);
		
		if(fileOut == null || !(task.getResultValue().isNormal())) return;
		try {
			PrintStream out = new PrintStream(new FileOutputStream(fileOut));
			task.getResult().toBA(out, parser.getAlphabet());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(Options.mVerbose && !buchi.isSemiDeterministic() && Options.mAlgo == Algorithm.NCSBOTF) {
		    System.err.println("Result may not be correct");
		}
	}
	

	
	private static void printTaskBegin(String op, ITask task) {
        if(Options.mVerbose)  System.out.println(op + " by ALGORITHM " + task.getOperationName() + " ...");
	}
	
	private static void printTaskEnd(ITask task) {
        if (Options.mVerbose) {
            if (task.getResultValue().isNormal()) {
                System.out.println("Task completed by ALGORITHM " + task.getOperationName() + " ...");
            } else {
                System.out.println("Task failed by ALGORITHM " + task.getOperationName() + " ...");
            }
            System.out.println("\n" + task.toStringVerbose());
        }else {
            System.out.println(task.toString());
        }
	}

}
