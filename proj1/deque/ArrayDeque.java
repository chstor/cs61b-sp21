package deque;

import java.util.Iterator;

/**
 * @author : bing
 * @date : 2025-01-25 15:08
 * @modyified By :
 */
public class ArrayDeque<T> implements Deque<T>,Iterable<T> {
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
            return items[(prev + (wizPos++))%items.length];
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
        next = size;
    }
    @Override
    public void addFirst(T item) {
        if(size == items.length){
            resize((int)(size * 1.5));
        }
        prev = (prev - 1 + items.length) % items.length;
        items[prev] = item;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if(size == items.length){
            resize((int)(size * 1.5));
        }
        items[next] = item;
        next = (next + 1 + items.length) % items.length;
        size += 1;
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
        items[prev] = null;
        prev = (prev + 1)%items.length;
        size -= 1;
        if(items.length >= 16 && size*1.0/items.length < 0.25) resize(size);
        return item;
    }

    @Override
    public T removeLast() {
        if(size == 0) return null;
        next = (next - 1 + items.length)%items.length;
        T item = items[next];
        items[next] = null;
        size -= 1;
        if(items.length >= 16 && size*1.0/items.length < 0.25) resize(size);
        return item;
    }

    @Override
    public T get(int index) {
        if(index < 0 || index >= size) return null;
        return items[(prev + index)%items.length];
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    public boolean equals(Object o){
        if(o == null) return false;
        if(!(o instanceof Deque)) return false;
        Deque<T> other = (Deque<T>) o;
        if(other.size() != this.size()) return false;
        int i = 0;
        for(T t : this){
            if(!t.equals(other.get(i))) return false;
            i += 1;
        }
        return true;
    }
}
