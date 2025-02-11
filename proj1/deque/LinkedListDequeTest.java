package deque;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

		assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
		lld1.addFirst("front");

		// The && operator is the same as "and" in Python.
		// It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

		lld1.addLast("middle");
		assertEquals(2, lld1.size());

		lld1.addLast("back");
		assertEquals(3, lld1.size());

		System.out.println("Printing out deque: ");
		lld1.printDeque();

    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
		// should be empty
		assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

		lld1.addFirst(10);
		// should not be empty
		assertFalse("lld1 should contain 1 item", lld1.isEmpty());

		lld1.removeFirst();
		// should be empty
		assertTrue("lld1 should be empty after removal", lld1.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {


        LinkedListDeque<String>  lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());


    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }


    }

    @Test
    /* 检查printDeque方法 */
    public void printDequeTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        lld1.addFirst(1);
        lld1.addFirst(2);
        lld1.addLast(3);
        lld1.addLast(5);
        lld1.addLast(9);
        // 现在队列内容是 2 1 3 5 9
        // 创建一个 ByteArrayOutputStream 来捕获 System.out 的输出
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        // 将 System.out 重定向到 outputStream
        System.setOut(printStream);
        // 调用 printDeque() 方法，它将打印内容到 outputStream
        lld1.printDeque();
        // 获取打印的输出内容
        String output = outputStream.toString().trim(); // 去掉结尾的空格

        // 验证输出内容是否符合预期
        assertEquals("2 1 3 5 9", output);
    }

    @Test
    /* 测试get方法是否正确,get方法返回索引index的元素,如果越界返回null */
    public void getTest() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<>();
        lld1.addFirst("am");
        lld1.addFirst("I");
        lld1.addLast("Jason");
        lld1.addLast("Ho");
        lld1.addLast(".");
        // 现在队列内容是 I am Jason Ho .
        assertEquals("I", lld1.get(0));
        assertEquals("Jason", lld1.get(2));
        assertEquals("Ho", lld1.get(3));
    }

    @Test
    /* 测试getRecursive方法是否正确,getRecursive方法返回索引index的元素,如果越界返回null */
    public void getRecursiveTest() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<>();
        lld1.addFirst("am");
        lld1.addFirst("I");
        lld1.addLast("Jason");
        lld1.addLast("Ho");
        lld1.addLast(".");
        // 现在队列内容是 I am Jason Ho .
        assertEquals("I", lld1.getRecursive(0));
        assertEquals("Jason", lld1.getRecursive(2));
        assertEquals("Ho", lld1.getRecursive(3));
    }

    @Test
    /* 测试equals方法是否正确 */
    public void testEquals() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<>();
        LinkedListDeque<String> lld2 = lld1;
        lld1.addFirst("I");
        lld1.addLast("am");
        lld1.addLast("Jason");
        lld1.addLast("Ho");

        LinkedListDeque<String> lld3 = new LinkedListDeque<>();
        lld3.addFirst("I");
        lld3.addLast("am");
        lld3.addLast("Jason");
        lld3.addLast("Ho");

        LinkedListDeque<String> lld4 = new LinkedListDeque<>();
        lld4.addFirst("I");
        lld4.addLast("am");
        lld4.addLast("Cristiano");
        lld4.addLast("Ronaldo");

        LinkedListDeque<Integer> lld5 = new LinkedListDeque<>();
        lld5.addFirst(1);
        lld5.addLast(2);
        lld5.addLast(3);
        lld5.addLast(4);

        assertTrue(lld1.equals(lld2));
        assertTrue(lld1.equals(lld3));
        assertTrue(!lld1.equals(lld4));
        assertTrue(!lld1.equals(lld5));
    }
}
