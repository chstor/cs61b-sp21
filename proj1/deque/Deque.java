package deque;

/**
 * @author : bing
 * @date : 2025-01-24 16:55
 * @modyified By :
 */
public interface Deque<T> {
    void addFirst(T item);

    void addLast(T item);

    int size();

    void printDeque();

    T removeFirst();

    T removeLast();

    T get(int index);

    default boolean isEmpty() {
        return size() == 0;
    }
}
