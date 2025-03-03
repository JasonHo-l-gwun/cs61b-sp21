package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Jason Ho
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int M; // the number of buckets
    private int N; // the number of elements
    private double loadFactor;
    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.M = initialSize;
        this.loadFactor = maxLoad;
        this.N = 0;
        this.buckets = createTable(M);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = (Collection<Node>[]) new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }


    @Override
    public void clear() {
        buckets = createTable(M);
        N = 0;
    }

    @Override
    public boolean containsKey(K key) {
        int hash = hash(key);
        for (Node node : buckets[hash]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    private int hash(K key) {
        return Math.floorMod((key.hashCode()), M);
    }
    @Override
    public V get(K key) {
        int hash = hash(key);
        for (Node node : buckets[hash]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return N;
    }

    @Override
    public void put(K key, V value) {
        int hash = hash(key);
        for (Node node : buckets[hash]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        buckets[hash].add(createNode(key, value));
        N++;

        if ((double) N / M > loadFactor) {
            resize(M * 2);
        }
    }

    private void resize(int cap) {
        Collection<Node>[] oldBuckets = buckets;
        M = cap;
        buckets = createTable(cap);
        N = 0;

        for (Collection<Node> bucket : oldBuckets) {
            for (Node node : bucket) {
                int hash = hash(node.key);
                buckets[hash].add(node);
                N++;
            }
        }
    }
    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                keySet.add(node.key);
            }
        }
        return keySet;
    }

    @Override
    public V remove(K key) {
        int hash = hash(key);
        Iterator<Node> iter = buckets[hash].iterator();
        while (iter.hasNext()) {
            Node node = iter.next();
            if (node.key.equals(key)) {
                iter.remove();
                N--;
                return node.value;
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        int hash = hash(key);
        Iterator<Node> iter = buckets[hash].iterator();
        while (iter.hasNext()) {
            Node node = iter.next();
            if (node.key.equals(key) && node.value.equals(value)) {
                iter.remove();
                N--;
                return node.value;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new HashMapItorator();
    }

    private class HashMapItorator implements Iterator<K> {
        private Iterator<Collection<Node>> bucketIter = Arrays.asList(buckets).iterator();
        private Iterator<Node> nodeIter = Collections.emptyIterator();

        @Override
        public boolean hasNext() {
            while (!nodeIter.hasNext() && bucketIter.hasNext()) {
                nodeIter = bucketIter.next().iterator();
            }
            return nodeIter.hasNext();
        }

        @Override
        public K next() {
            return nodeIter.next().key;
        }
    }

}
