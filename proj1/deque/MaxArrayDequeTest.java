package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {

    @Test
    /* 测试构造函数 */
    public void constructorTest() {
        Comparator<Integer> c1 = Comparator.naturalOrder();
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<>(c1);
        mad1.addLast(1);
        assertEquals(1, mad1.size());
    }

    @Test
    /* 测试空max */
    public void MaxWithoutParamTest() {
        Comparator<String> c1 = Comparator.comparing(s -> s.length());
        MaxArrayDeque<String> mad1 = new MaxArrayDeque<>(c1);
        mad1.addLast("a");
        mad1.addFirst("aa");
        mad1.addLast("aaa");
        assertEquals("aaa", mad1.max());
    }

    @Test
    /* 测试有参数的max */
    public void MaxWithParamTest() {
        Comparator<String> c1 = Comparator.naturalOrder();
        MaxArrayDeque<String> mad1 = new MaxArrayDeque<>(c1);
        mad1.addLast("aaaa");
        mad1.addFirst("abc");
        mad1.addLast("acd");
        assertEquals("acd", mad1.max());
    }
}
