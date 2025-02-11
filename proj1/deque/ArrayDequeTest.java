package deque;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    /* 测试往空队列加入元素 */
    public void addEmptyTest() {
        ArrayDeque<Integer> ald1 = new ArrayDeque<>();
        ald1.addFirst(1);
        assertEquals(1,ald1.size());
        ald1.addFirst(2);
        assertEquals(2,ald1.size());
        ald1.addLast(3);
        assertEquals(3,ald1.size());
    }

    @Test
    /* 测试往空队列加入和删除元素 */
    public void addRemoveTest() {
        ArrayDeque<Integer> ald1 = new ArrayDeque<>();
        ald1.addFirst(1);
        ald1.addFirst(2);
        ald1.removeFirst();
        assertEquals(1,ald1.size());
        ald1.removeLast();
        assertEquals(0,ald1.size());
    }

    @Test
    /* 测试删除空队列中的元素 */
    public void removeEmpty() {
        ArrayDeque<Integer> ald1 = new ArrayDeque<>();
        ald1.addLast(1);
        ald1.removeFirst();
        ald1.removeLast();
        assertEquals(0,ald1.size());
    }

    @Test
    /* 测试是否可以加入多类型的参数 */
    public void multipleParamTest() {

        ArrayDeque<String> ald1 = new ArrayDeque<String>();
        ArrayDeque<Double> ald2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> ald3 = new ArrayDeque<Boolean>();

        ald1.addFirst("string");
        ald2.addFirst(3.14159);
        ald3.addFirst(true);

        String s = ald1.removeFirst();
        double d = ald2.removeFirst();
        boolean b = ald3.removeFirst();
    }

    @Test
    /* 测试remove一个空的队列是否返回null */
    public void removeEmptyNullTest() {
        ArrayDeque<String> ald1 = new ArrayDeque<String>();
        assertEquals(null,ald1.removeFirst());
        assertEquals(null,ald1.removeLast());
    }

    @Test
    /* 加入大量数据检测是否添加正确 */
    public void bigLLDequeTest() {

        ArrayDeque<Integer> ald1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            ald1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) ald1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) ald1.removeLast(), 0.0);
        }
    }

    @Test
    /* 检查printDeque方法 */
    public void printDequeTest() {
        ArrayDeque<Integer> ald1 = new ArrayDeque<Integer>();
        ald1.addFirst(1);
        ald1.addFirst(2);
        ald1.addLast(3);
        ald1.addLast(5);
        ald1.addLast(9);
        // 现在队列内容是 2 1 3 5 9
        // 创建一个 ByteArrayOutputStream 来捕获 System.out 的输出
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        // 将 System.out 重定向到 outputStream
        System.setOut(printStream);
        // 调用 printDeque() 方法，它将打印内容到 outputStream
        ald1.printDeque();
        // 获取打印的输出内容
        String output = outputStream.toString().trim(); // 去掉结尾的空格

        // 验证输出内容是否符合预期
        assertEquals("2 1 3 5 9", output);
    }

    @Test
    /* 测试get方法是否正确,get方法返回索引index的元素,如果越界返回null */
    public void getTest() {
        ArrayDeque<String> ald1 = new ArrayDeque<>();
        ald1.addFirst("am");
        ald1.addFirst("I");
        ald1.addLast("Jason");
        ald1.addLast("Ho");
        ald1.addLast(".");
        // 现在队列内容是 I am Jason Ho .
        assertEquals("I", ald1.get(0));
        assertEquals("Jason", ald1.get(2));
        assertEquals("Ho", ald1.get(3));
    }

    @Test
    /* 测试equals方法是否正确 */
    public void testEquals() {
        ArrayDeque<String> ald1 = new ArrayDeque<>();
        ArrayDeque<String> ald2 = ald1;
        ald1.addFirst("I");
        ald1.addLast("am");
        ald1.addLast("Jason");
        ald1.addLast("Ho");

        ArrayDeque<String> ald3 = new ArrayDeque<>();
        ald3.addFirst("I");
        ald3.addLast("am");
        ald3.addLast("Jason");
        ald3.addLast("Ho");

        ArrayDeque<String> ald4 = new ArrayDeque<>();
        ald4.addFirst("I");
        ald4.addLast("am");
        ald4.addLast("Cristiano");
        ald4.addLast("Ronaldo");

        ArrayDeque<Integer> ald5 = new ArrayDeque<>();
        ald5.addFirst(1);
        ald5.addLast(2);
        ald5.addLast(3);
        ald5.addLast(4);

        assertTrue(ald1.equals(ald2));
        assertTrue(ald1.equals(ald3));
        assertTrue(!ald1.equals(ald4));
        assertTrue(!ald1.equals(ald5));
    }
}
