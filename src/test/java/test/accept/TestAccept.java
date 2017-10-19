package test.accept;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import automata.IBuchi;
import operation.accept.Accept;

import test.BAStore;

public class TestAccept {
	
	static int[] stem1 = {0, 0, 1, 1};
	static int[] loop1 = {0, 1};
	@Test
	public void testAAccept() {
		IBuchi A = BAStore.getA();
		System.out.println(A.toDot());
		
		assertEquals(false, (new Accept(A, Arrays.asList(0, 0, 1, 1), Arrays.asList(0, 1))).getResult());
		assertEquals(true, (new Accept(A, Arrays.asList(0, 0, 1, 0), Arrays.asList(0, 1))).getResult());
		assertEquals(true, (new Accept(A, Arrays.asList(0, 0), Arrays.asList(0, 1))).getResult());
	} 
	
//	@Test
//	public void testBAccept() {
//		IBuchi B = BAStore.getB();
////		System.out.println(B.toDot());
//		assertEquals(false, (new IsEmpty(B).getResult()).booleanValue());
//	} 
//	
//	
//	@Test
//	public void testCAccept() {
//		IBuchi C = BAStore.getC();
////		System.out.println(C.toDot());
//		assertEquals(false, (new IsEmpty(C).getResult()).booleanValue());
//	} 
//
//	@Test
//	public void testDAccept() {
//		IBuchi D = BAStore.getD();
////		System.out.println(D.toDot());
//		assertEquals(false, (new IsEmpty(D).getResult()).booleanValue());
//	} 
//	
//	@Test
//	public void testEAccept() {
//		IBuchi E = BAStore.getE();
////		System.out.println(E.toDot());
//		assertEquals(true, (new IsEmpty(E).getResult()).booleanValue());
//	} 
}
