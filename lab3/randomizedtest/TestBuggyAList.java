package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> al1 = new AListNoResizing<>();
        BuggyAList<Integer> al2 = new  BuggyAList<>();
        al1.addLast(4);
        al2.addLast(4);
        al1.addLast(5);
        al2.addLast(5);
        al1.addLast(6);
        al2.addLast(6);
        assertEquals(al1.removeLast(), al2.removeLast());
        assertEquals(al1.removeLast(), al2.removeLast());
        assertEquals(al1.removeLast(), al2.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        int N = 1000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                broken.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
                assertEquals(L.size(), broken.size());
            } else if (operationNumber == 2 && L.size() > 0 && broken.size() > 0) {
                assertEquals(L.getLast(), broken.getLast());
            } else if (operationNumber == 3 && L.size() > 0 && broken.size() > 0) {
                assertEquals(L.removeLast(), broken.removeLast());
            }
        }
    }
}
