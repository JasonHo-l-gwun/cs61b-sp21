package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 3; // 标记First下一个要加的位置
        nextLast = 4; // 标记Last下一个要加的位置
    }

    /* 数组扩容 */
    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        for(int i = 0; i < size; ++i){
            a[i] = items[(nextFirst + 1 + i) % items.length];
        }
        nextFirst = capacity - 1;
        nextLast = size;
        items = a;
    }

    /* 返回队列是否是满的 */
    private boolean isFull() {
        return size == items.length;
    }

    @Override
    public void addFirst(T item) {
        if (isFull()) {
            resize(size * 2);
        }

        items[nextFirst] = item;
        nextFirst = (nextFirst - 1) % items.length;;
        size++;
    }

    @Override
    public void addLast(T item) {
        if (isFull()) {
            resize(size * 2);
        }

        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Iterator<T> iterator = iterator();
        while(iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        nextFirst = (nextFirst + 1) % items.length;
        T item = items[nextFirst];
        items[nextFirst] = null;
        size--;

        if (size >= 16 && size < items.length / 4) {
            resize(items.length / 2);
        }

        return item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        nextLast = (nextLast - 1) % items.length;
        T item = items[nextLast];
        items[nextLast] = null;
        size--;

        if (size >= 16 && size < items.length / 4) {
            resize(items.length / 2);
        }

        return item;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }

        return items[(nextFirst + 1 + index) % items.length];
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ArrayDeque)) {
            return false;
        }

        ArrayDeque<?> other = (ArrayDeque<?>) o;
        if (this.size != other.size) {
            return false;
        }

        Iterator<T> thisIterator = this.iterator();
        Iterator<?> otherIterator = other.iterator();

        while(thisIterator.hasNext()){
            T thisItem = thisIterator.next();
            Object otherItem = otherIterator.next();

            if (!thisItem.equals(otherItem)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayListIterator();
    }

    private class ArrayListIterator implements Iterator<T> {
        int current;

        ArrayListIterator() {
            current = nextFirst + 1;
        }

        @Override
        public boolean hasNext() {
            return current != nextLast;
        }

        @Override
        public T next() {
            T item = items[current];
            current = (current + 1) % items.length;
            return item;
        }
    }
}
