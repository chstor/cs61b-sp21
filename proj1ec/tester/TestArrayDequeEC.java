package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

/**
 * @author : bing
 * @date : 2025-01-27 20:17
 * @modyified By :
 */
public class TestArrayDequeEC {
    @Test
    public void addRemoveTest(){
        StudentArrayDeque<Integer> studentDeque = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> solutionDeque = new ArrayDequeSolution<>();
        StringBuilder operationSequence = new StringBuilder();
        for (int i = 0; i < 10000; i += 1) {
            int operation = StdRandom.uniform(0,4);
            if (operation == 0 && !solutionDeque.isEmpty()) { // removeFirst
                Integer expected = solutionDeque.removeFirst();
                Integer actual = studentDeque.removeFirst();
                operationSequence.append("removeFirst()\n");
                assertEquals(operationSequence.toString(), expected, actual);
            } else if (operation == 1 && !solutionDeque.isEmpty()) { // removeLast
                Integer expected = solutionDeque.removeLast();
                Integer actual = studentDeque.removeLast();
                operationSequence.append("removeLast()\n");

                assertEquals(operationSequence.toString(), expected, actual);
            } else if (operation == 2) { // addFirst
                int randVal = StdRandom.uniform(0, 100);
                solutionDeque.addFirst(randVal);
                studentDeque.addFirst(randVal);
                operationSequence.append("addFirst(").append(randVal).append(")\n");
            } else if (operation == 3) { // addLast
                int randVal = StdRandom.uniform(0, 100);
                solutionDeque.addLast(randVal);
                studentDeque.addLast(randVal);
                operationSequence.append("addLast(").append(randVal).append(")\n");
            }
        }
    }
}
