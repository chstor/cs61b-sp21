package deque;

import java.util.Iterator;

/**
 * @author : bing
 * @date : 2025-01-24 16:55
 * @modyified By :
 */
public interface DequeAPI<T> {
    public void addFirst(T item);
    public void addLast(T item);
    public boolean isEmpty();
    public int size();
    public void printDeque();
    public T removeFirst();
    public T removeLast();
    public T get(int index);
    public Iterator<T> iterator();
    public boolean equals(Object o);
}
