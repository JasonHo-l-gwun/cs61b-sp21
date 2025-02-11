package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private class Node<T> {
        T item;
        Node<T> prev;
        Node<T> next;

        public Node(T item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }
    private Node<T> sentinel;
    private int size;

    public LinkedListDeque(){
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    };

    @Override
    public void addFirst(T item){
        Node<T> first = new Node<>(item, sentinel, sentinel.next);
        sentinel.next.prev = first;
        sentinel.next = first;
        size++;
    }

    @Override
    public void addLast(T item){
        Node<T> last = new Node<>(item, sentinel.prev, sentinel);
        sentinel.prev.next = last;
        sentinel.prev = last;
        size++;
    }

    @Override
    public int size(){
        return size;
    }

    @Override
    public void printDeque(){
        Iterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();
            System.out.print(item + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T x = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return x;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T x = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next= sentinel;
        size--;
        return x;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node<T> curr = sentinel.next;
        for(int i = 0; i < index; ++i){
            curr = curr.next;
        }
        return curr.item;
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursiveHelper(sentinel.next, index);
    }

    /**
     * 帮助实现递归get的辅助方法
     * @param node 当前节点
     * @param index 当前索引
     * @return 索引处的元素
     */
    private T getRecursiveHelper(Node<T> node, int index) {
        if (node == sentinel) {
            return null;
        } else if (index == 0){
            return node.item;
        } else {
            return getRecursiveHelper(node.next, index - 1);
        }
    }

    @Override
    public boolean equals(Object o) {
        // 如果比较的对象是自身，直接返回 true
        if (this == o) {
            return true;
        }

        // 如果 obj 为 null 或者不是 LinkedListDeque 的实例，返回 false
        if (o == null || !(o instanceof LinkedListDeque)) {
            return false;
        }

        LinkedListDeque<?> other = (LinkedListDeque<?>) o;

        // 如果两个队列的大小不同，返回 false
        if (this.size != other.size) {
            return false;
        }

        // 使用迭代器遍历两个队列，逐个比较元素
        Iterator<T> thisIterator = this.iterator();
        Iterator<?> otherIterator = other.iterator();

        while (thisIterator.hasNext()) {
            T thisItem = thisIterator.next();
            Object otherItem = otherIterator.next();

            // 如果对应位置的元素不相等，返回 false
            if (!thisItem.equals(otherItem)) {
                return false;
            }
        }

        // 如果所有元素都相等，返回 true
        return true;
    }

    @Override
    public Iterator iterator() {
        return new LinkedListIterator();
    }


    /** 迭代器类实现 */
    private class LinkedListIterator implements Iterator<T> {
        private Node<T> current;

        LinkedListIterator(){
            current = sentinel.next;
        }

        @Override
        public boolean hasNext() {
            return !(current == sentinel);
        }

        @Override
        public T next() {
            T item = current.item;
            current = current.next;
            return item;
        }
    }
}
