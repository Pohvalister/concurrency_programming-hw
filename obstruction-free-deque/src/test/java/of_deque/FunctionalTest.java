package of_deque;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class FunctionalTest {
    private static Random R = new Random(0);

    @Test
    public void test() {
        Deque deque = new ObstructionFreeDeque(1_000_000);
        ArrayDeque<Integer> javaDeque = new ArrayDeque<>();
        for (int i = 0; i < 1_000_000; i++) {
            int op = R.nextInt(6);
            int x = R.nextInt(30);
            switch (op) {
            case 0:
                // push first
                javaDeque.addFirst(x);
                deque.pushFirst(x);
                break;
            case 1:
                // peek first
                if (!javaDeque.isEmpty())
                    assertEquals((int) javaDeque.peekFirst(), deque.peekFirst());
                break;
            case 2:
                // pop first
                if (!javaDeque.isEmpty())
                    assertEquals((int) javaDeque.pollFirst(), deque.popFirst());
                break;
            case 4:
                // push last
                javaDeque.addLast(x);
                deque.pushLast(x);
                break;
            case 5:
                // peek last
                if (!javaDeque.isEmpty())
                    assertEquals((int) javaDeque.peekLast(), deque.peekLast());
                break;
            case 6:
                // pop last
                if (!javaDeque.isEmpty())
                    assertEquals((int) javaDeque.pollLast(), deque.popLast());
                break;
            }
        }
    }
}