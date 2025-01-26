package deque;

import java.util.Comparator;

/**
 * @author : bing
 * @date : 2025-01-25 21:21
 * @modyified By :
 */
public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private final Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c){
        super();
        this.comparator = c;
    }
    public T max(){
        return max(this.comparator);
    }
    public T max(Comparator<T> c){
        if(isEmpty()) return null;
        T maxItem = get(0);
        for(int i = 1; i < size(); i ++){
            T t = get(i);
            if(c.compare(t,maxItem) > 0) maxItem = t;
        }
        return maxItem;
    }
}
