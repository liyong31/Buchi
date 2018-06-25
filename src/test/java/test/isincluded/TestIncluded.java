package test.isincluded;

import org.junit.Test;

import automata.IBuchi;
import operation.explore.Explore;
import operation.isincluded.IsIncludedExplore;
import operation.semideterminize.Semideterminize;
import test.BAStore;

public class TestIncluded {
    
    @Test
    public void testIncluded() {
        IBuchi A = BAStore.getA();
        IBuchi B = BAStore.getC();
        IsIncludedExplore explorer = new IsIncludedExplore(A, B);
        System.out.println("A:\n" + A.toBA());
        System.out.println("A:\n" + A.toDot());
        System.out.println("B:\n" + B.toBA());
        System.out.println(explorer.isIncluded());
//        A = new Semideterminize(A);
//        new Explore(A);
        System.out.println("A SDBA:\n" + A.toDot());
        System.out.println("A SDBA:\n" + A.toBA());
        explorer = new IsIncludedExplore(B, A);
        System.out.println(explorer.isIncluded());
    }
    
    @Test
    public void testSemidetermized() {
        IBuchi A = BAStore.getA();
        System.out.println("A:\n" + A.toBA());
        System.out.println("A:\n" + A.toDot());
        A = new Semideterminize(A);
        new Explore(A);
        System.out.println("A SDBA:\n" + A.toDot());
        System.out.println("A SDBA:\n" + A.toBA());
    }

}
