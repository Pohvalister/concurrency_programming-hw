package msqueue;

import java.util.concurrent.atomic.AtomicReference;

public class MSQueue implements Queue {
    private AtomicReference<Node> head;
    private AtomicReference<Node> tail;

    public MSQueue() {
        Node dummy = new Node(0);
        this.head = new AtomicReference<>(dummy);
        this.tail = new AtomicReference<>(dummy);
    }

    @Override
    public void enqueue(int x) {
        Node newTail = new Node(x);
        while (true) {
            Node curTail = tail.get();
            if (tail.get().next.compareAndSet(null, newTail)) {
                tail.compareAndSet(curTail, newTail);
                return;
            } else {
                tail.compareAndSet(curTail, curTail.next.get());
            }
        }
    }

    @Override
    public int dequeue() {
        while (true) {
            Node curHead = head.get();
            Node curTail = tail.get();

            if (curHead != curTail) {
                Node curHeadNext = curHead.next.get();
                if (head.compareAndSet(curHead, curHeadNext)) {
                    return curHeadNext.x;
                } else {
                }
            } else {
                if (curTail.next.get() == null) return 0;// throw new NoSuchElementException()
                tail.compareAndSet(curTail, curTail.next.get());
            }
        }
    }

    @Override
    public int peek() {
        while (true) {
            Node curHead = head.get();
            Node curTail = tail.get();

            if (curHead != curTail) {
                return curHead.next.get().x;
            } else {
                if (curTail.next.get() == null) return 0;//throw new NoSuchElementException()
                tail.compareAndSet(curTail, curTail.next.get());
            }

        }
    }

    private class Node {
        final int x;
        AtomicReference<Node> next;

        Node(int x) {
            this.x = x;
            next = new AtomicReference<>(null);
        }
    }
}