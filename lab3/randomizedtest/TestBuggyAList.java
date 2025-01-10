package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> aListNoResizing = new AListNoResizing<>();
        BuggyAList buggyAList = new BuggyAList();
        for(int i = 4; i <= 6; i += 1){
            aListNoResizing.addLast(i);
            buggyAList.addLast(i);
        }
        for(int i = 0; i < 3; i += 1){
            assertEquals(aListNoResizing.removeLast(),buggyAList.removeLast());
        }
    }
    @Test
    public void randomizedTest(){
        BuggyAList<Integer> L = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
            }else{
                if(L.size() == 0) continue;
                if(operationNumber == 2){
                    System.out.println("getLast(" + L.getLast() + ")");
                }else{
                    System.out.println("removeLast(" + L.removeLast() + ")");
                }
            }
        }
    }
}
