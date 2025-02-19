package bstmap;

import java.util.*;

public class BSTMap <K extends Comparable<K>,V> implements Map61B<K, V> {
    private Node root;
    private class Node {
        public K key;
        public V value;
        public Node left;
        public Node right;
        int N;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.N = 1;
        }
    }

    @Override
    public void clear() {
        while (root != null) {
            remove(root.key);
        }
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }

    private boolean containsKey(Node x, K key) {
        if (x == null) return false;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) return containsKey(x.left, key);
        else if (cmp > 0) return containsKey(x.right, key);
        else return true;
    }

    @Override
    public V get(K key) {
        Node t = get(root,key);
        if (t == null) return null;
        return t.value;
    }

    private Node get(Node x, K key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) return get(x.left, key);
        else if (cmp > 0) return get(x.right, key);
        else return x;
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        else return x.N;
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    private Node put(Node x, K key, V value) {
        if (x == null) return new Node(key,value);
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = put(x.left, key, value);
        else if (cmp > 0) x.right = put(x.right, key, value);
        else x.value = value;
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        keySetHelper(root, keySet);
        return keySet;
    }

    private void keySetHelper(Node node, Set<K> keySet) {
        if (node == null) return;
        keySetHelper(node.left, keySet);
        keySet.add(node.key);
        keySetHelper(node.right, keySet);
    }

    @Override
    public V remove(K key) {
        if (containsKey(key)) {
            V value = get(key);
            root = remove(root, key);
            return value;
        }
        return null;
    }

    private Node remove(Node x, K key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = remove(x.left, key);
        else if (cmp > 0) x.right = remove(x.right, key);
        else {
            if (x.left == null) {
                return x.right;
            } else if (x.right == null) {
                return x.left;
            } else {
                Node t = x;
                x = min(x.right);
                x.right = deleteMin(x.right);
                x.left = t.left;
            }
        }
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    private Node min(Node x) {
        if (x.left == null) return x;
        else return min(x.left);
    }

    private Node max(Node x) {
        if (x.right == null) return x;
        else return max(x.right);
    }

    private Node deleteMin(Node x) {
        if (x == null) return null;
        x.left = deleteMin(x.left);
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    public V remove(K key, V value) {
        V val = get(key);
        if (val == value) remove(key);
        return val;
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTIterator();
    }

    private void keys(Node x, Queue<K> queue) {
        if (x == null) return;
        keys(x.left, queue);
        queue.add(x.key);
        keys(x.right, queue);
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(Node x) {
        if (x == null) return;
        printInOrder(x.left);
        System.out.println(x.key);
        printInOrder(x.right);
    }

    private class BSTIterator implements Iterator{
        private Queue<K> queue;

        BSTIterator() {
            queue = new LinkedList<>();
            keys(root, queue);
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public K next() {
            return queue.remove();
        }
    }
}
