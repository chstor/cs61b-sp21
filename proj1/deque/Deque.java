package deque;

import java.util.Iterator;

/**
 * @author : bing
 * @date : 2025-01-24 16:55
 * @modyified By :
 */
public interface Deque<T> {
    public void addFirst(T item);
    public void addLast(T item);
    public int size();
    public void printDeque();
    public T removeFirst();
    public T removeLast();
    public T get(int index);
    default boolean isEmpty(){
        return size() == 0;
    }
}
