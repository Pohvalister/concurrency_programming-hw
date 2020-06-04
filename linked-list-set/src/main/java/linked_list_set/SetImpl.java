package linked_list_set;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class SetImpl implements Set {
    private class Node {
        AtomicMarkableReference<Node> next;
        int x;

        Node(int x, Node next) {
            this.next = new AtomicMarkableReference<>(next, false);
            this.x = x;
        }
    }

    private class Window {
        Node cur;
        Node nex;
    }

    private final Node head = new Node(Integer.MIN_VALUE,
            new Node(Integer.MAX_VALUE, null));

    /**
     * Returns the {@link Window}, where cur.x < x <= next.x
     */
    private Window findWindow(int key) {
        retry:
        while (true) {
            Window w = new Window();
            w.cur = head;
            w.nex = w.cur.next.getReference();
            boolean[] removed = new boolean[1];
            while (w.nex.x < key) {
                Node node = w.nex.next.get(removed);
                if (removed[0]) {
                    if (!w.cur.next.compareAndSet(w.nex, node, false, false)) {
                        continue retry;
                    }
                    w.nex = node;
                } else {
                    w.cur = w.nex;
                    w.nex = node;
                }
            }

            Node node = w.nex.next.get(removed);
            if (removed[0]) {
                if (!w.cur.next.compareAndSet(w.nex, node, false, false)) {
                    continue;
                }
                w.nex = node;
            }
            return w;

        }
    }

    @Override
    public boolean add(int x) {
        while (true) {
            Window w = findWindow(x);
            if (w.nex.x == x) {
                return false;
            } else {
                Node node = new Node(x, w.nex);
                if (w.cur.next.compareAndSet(w.nex, node, false, false))
                    return true;
            }
        }
    }

    @Override
    public boolean remove(int x) {
        while (true) {
            Window w = findWindow(x);
            if (w.nex.x != x)
                return false;
            else {
                Node node = w.nex.next.getReference();
                if (w.nex.next.compareAndSet(node, node, false, true)) {
                    w.cur.next.compareAndSet(w.nex, node, false, false);
                    return true;
                }
            }
        }
    }

    @Override
    public boolean contains(int x) {
        Window w = findWindow(x);
        return w.nex.x == x;
    }
}