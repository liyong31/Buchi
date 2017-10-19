package util;

import java.util.Random;

public class ISetTest {
	
	public static void main(String[] args) {
		
		ISetBits bits = new ISetBits();	
		ISetTIntSet tISets = new ISetTIntSet();
		ISetTreeSet treeSets = new ISetTreeSet();
		
		int num = 9100_000;
		
		Random random = new Random(System.currentTimeMillis());
		for(int i = 0; i < num; i ++) {
			if(random.nextBoolean()) {
				bits.set(i);
				tISets.set(i);
				treeSets.set(i);
			}
		}
		
		System.out.println("test bits");
		testIterator(bits);
		ISetBits cpb = (ISetBits) bits.clone();
		cpb.set(7000_000 - 1);
		testEq(bits, cpb);
		testSubset(bits, cpb);
		
		
		System.out.println("test TISet");
		testIterator(tISets);
		ISetTIntSet cpi = (ISetTIntSet) tISets.clone();
		cpi.set(7000_000 - 1);
		testEq(tISets, cpi);
		testSubset(tISets, cpi);
	
		
		System.out.println("test TreeSet");
		testIterator(treeSets);
		ISetTreeSet cpt = (ISetTreeSet) treeSets.clone();
		cpt.set(7000_000 - 1);
		testEq(treeSets, cpt);
		testSubset(treeSets, cpt);
	}
	
	private static void testIterator(ISet set) {
		long start = System.currentTimeMillis();
		int num = 0;
		for(int n : set) {
		    num ++;
		}
		long end = System.currentTimeMillis();
		System.out.println("<iter> time = " + (end - start) + " num=" + num);
	}

	private static void testEq(ISet a, ISet b) {
		long start = System.currentTimeMillis();
        System.out.println("isEq = " + a.contentEq(b));
		long end = System.currentTimeMillis();
		System.out.println("<eq> time = " + (end - start));
	}
	
	private static void testSubset(ISet a, ISet b) {
		long start = System.currentTimeMillis();
        System.out.println("isSubset = " + a.subsetOf(b));
		long end = System.currentTimeMillis();
		System.out.println("<subset> time = " + (end - start));
	}



}
