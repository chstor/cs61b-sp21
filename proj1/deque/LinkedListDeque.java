package deque;

import java.util.Iterator;

/**
 * @author : bing
 * @date : 2025-01-24 16:56
 * @modyified By :
 */
public class LinkedListDeque<T> implements DequeAPI<T>{

    private Node sentinel;
    private int size;

    private class Node{
        public T item;
        public Node prev;
        public Node next;

        public Node() {
        }

        public Node(T item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private class LinkedListDequeIterator implements Iterator<T>{
        private int wizPos;
        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
        public T next() {
            wizPos += 1;
            return get(wizPos);
        }
    }

    public LinkedListDeque() {
        sentinel = new Node();
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        size += 1;
        Node p = new Node(item,sentinel,sentinel.next);
        if(sentinel.next == sentinel){
            sentinel.next = p;
            sentinel.prev = p;
        }else{
            sentinel.next.prev = p;
            sentinel.next = p;
        }
    }

    @Override
    public void addLast(T item) {
        size += 1;
        Node p = new Node(item,sentinel.prev,sentinel);
        if(sentinel.next == sentinel){
            sentinel.next = p;
            sentinel.prev = p;
        }else{
            sentinel.prev.next = p;
            sentinel.prev = p;
        }
    }

    @Override
    public boolean isEmpty() {
        if(size == 0) return true;
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = sentinel.next;
        while(p != sentinel){
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println("");
    }

    @Override
    public T removeFirst() {
        if(size == 0) return null;
        T item = sentinel.next.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        size -= 1;
        return item;
    }

    @Override
    public T removeLast() {
        if(size == 0) return null;
        T item = sentinel.prev.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        size -= 1;
        return item;
    }

    @Override
    public T get(int index) {
        if(index < 0 || index >= size) return null;
        Node p = sentinel.next;
        while(p != sentinel && index < size){
            p = p.next;
            index += 1;
        }
        return p.item;
    }

    public T getRecursive(int index){
        if(index < 0 || index >= size) return null;
        return getRecursiveHelper(sentinel.next,index);
    }
    private T getRecursiveHelper(Node sentinel,int index){
        if(index == 0) return sentinel.item;
        return getRecursiveHelper(sentinel.next,index - 1);
    }
    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }
}
