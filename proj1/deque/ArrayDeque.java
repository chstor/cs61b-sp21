package deque;

import java.util.Iterator;

/**
 * @author : bing
 * @date : 2025-01-25 15:08
 * @modyified By :
 */
public class ArrayDeque<T> implements Deque<T> {
    private T[] items;
    private int size;
    private int prev;
    private int next;

    private class ArrayDequeIterator implements Iterator<T>{
        private int wizPos;
        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
        public T next() {
            return items[(prev + wizPos)%items.length];
        }
    }
    public ArrayDeque() {
        items = (T[])new Object[8];
        size = 0;
        prev = 0;
        next = 0;
    }

    private void resize(int cap){
        T[] newItems = (T[])new Object[cap];
        for(int i = 0; i < size; i += 1){
            newItems[i] = items[(prev + i)%items.length];
        }
        items = newItems;
        prev = 0;
        next = size - 1;
    }
    @Override
    public void addFirst(T item) {
        if(size == items.length){
            resize((int)(size * 1.5));
        }
        if(size != 0) prev = (prev - 1 + items.length) % items.length;
        items[prev] = item;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if(size == items.length){
            resize((int)(size * 1.5));
        }
        if(size != 0) next = (next + 1 + items.length) % items.length;
        items[next] = item;
        size += 1;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for(int i = 0; i < size; i += 1){
            System.out.print(items[(prev + i)%items.length] + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if(size == 0) return null;
        T item = items[prev];
        prev = (prev + 1)%items.length;
        size -= 1;
        if(items.length >= 16 && size*1.0/items.length < 0.25) resize(size);
        return item;
    }

    @Override
    public T removeLast() {
        if(size == 0) return null;
        T item = items[next];
        next = (next - 1 + items.length)%items.length;
        size -= 1;
        if(items.length >= 16 && size*1.0/items.length < 0.25) resize(size);
        return item;
    }

    @Override
    public T get(int index) {
        if(index < 0 || index >= size) return null;
        return items[(prev + index)%items.length];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
}
