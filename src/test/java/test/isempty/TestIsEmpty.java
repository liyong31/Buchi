package test.isempty;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import automata.IBuchi;
import operation.isempty.IsEmpty;
import test.BAStore;

public class TestIsEmpty {
	
	
	@Test
	public void testAIsEmpty() {
		IBuchi A = BAStore.getA();
		System.out.println(A.toDot());
		IsEmpty checker = new IsEmpty(A);
		assertEquals(false, checker.getResult());
		System.out.println("" + checker.getAcceptedLassoRun());
	} 
	
	@Test
	public void testBIsEmpty() {
		IBuchi B = BAStore.getB();
//		System.out.println(B.toDot());
		assertEquals(false, (new IsEmpty(B).getResult()).booleanValue());
	} 
	
	
	@Test
	public void testCIsEmpty() {
		IBuchi C = BAStore.getC();
		System.out.println(C.toDot());
		assertEquals(false, (new IsEmpty(C).getResult()).booleanValue());
	} 

	@Test
	public void testDIsEmpty() {
		IBuchi D = BAStore.getD();
//		System.out.println(D.toDot());
		assertEquals(false, (new IsEmpty(D).getResult()).booleanValue());
	} 
	
	@Test
	public void testEIsEmpty() {
		IBuchi E = BAStore.getE();
//		System.out.println(E.toDot());
		assertEquals(true, (new IsEmpty(E).getResult()).booleanValue());
	} 
}
