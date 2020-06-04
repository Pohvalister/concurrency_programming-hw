package stack;

import java.util.EmptyStackException;
import java.util.concurrent.atomic.AtomicReference;

public class StackImpl implements Stack {
    private class Node {
        final Node next;
        final int x;

        Node(int x, Node next) {
            this.next = next;
            this.x = x;
        }
    }

    private final Node NIL = new Node(0, null);
    final AtomicReference<Node> head = new AtomicReference<>(NIL);

    @Override
    public void push(int x) {
        while (true) {
            Node node = new Node(x, head.get());
            if (head.compareAndSet(node.next, node))
                break;
        }
    }

    @Override
    public int pop() {
        while (true) {
            Node node = head.get();
            if (node == NIL)
                throw new EmptyStackException();
            if (head.compareAndSet(node, node.next))
                return node.x;
        }
    }
}
