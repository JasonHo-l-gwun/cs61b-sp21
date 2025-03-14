    package deque;

    import java.util.Comparator;
    import java.util.Iterator;

    public class MaxArrayDeque<T> extends ArrayDeque<T> {
        private Comparator<T> comparator;

        public MaxArrayDeque(Comparator<T> c){
            comparator = c;
        }

        public T max() {
            if (isEmpty()) {
                return null;
            }

            T maxItem = get(0);
            Iterator<T> iterator = iterator();
            while (iterator.hasNext()){
                T item = iterator.next();
                if (comparator.compare(item, maxItem) > 0) {
                    maxItem = item;
                }
            }

            return maxItem;
        }

        public T max(Comparator<T> c) {
            if (isEmpty()) {
                return null;
            }

            T maxItem = get(0);
            Iterator<T> iterator = iterator();
            while (iterator.hasNext()){
                T item = iterator.next();
                if (c.compare(item, maxItem) > 0) {
                    maxItem = item;
                }
            }

            return maxItem;
        }
    }
