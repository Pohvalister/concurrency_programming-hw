package skip_list;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class SkipList implements Set {
    private final int MAX_LEVEL = 30;
    private final Node head;

    public SkipList() {
        this.head = new Node(Integer.MIN_VALUE, MAX_LEVEL);
        final Node tail = new Node(Integer.MAX_VALUE, MAX_LEVEL);
        for (int i = 0; i < head.nextAndRemoved.length; i++)
            head.nextAndRemoved[i] = new AtomicMarkableReference<>(tail, false);
    }

    private class Node {
        final int x;
        final int topLevel;
        //final Node[] next;
        final AtomicMarkableReference<Node>[] nextAndRemoved;

        Node(int x, int height) {
            this.x = x;
            nextAndRemoved = new AtomicMarkableReference[height + 1];
            for (int i = 0; i < height + 1; i++)
                nextAndRemoved[i] = new AtomicMarkableReference<>(null, false);
            topLevel = height;
        }
    }

    private class Window {
        int levelFound = -1; // -1 if not found
        Node[] preds = new Node[MAX_LEVEL + 1];
        Node[] succs = new Node[MAX_LEVEL + 1];

        boolean found = false;
    }

    /**
     * Returns the {@link Window}, where {@code preds[l].x < x <= succs[l].x}
     * for every level {@code l}
     */
    private Window findWindow(int x) {
        Window w = new Window();

        retry:
        while (true) {
            Node pred = head;

            for (int level = MAX_LEVEL; level >= 0; level--) {
                Node cur = pred.nextAndRemoved[level].getReference();

                boolean[] removed = {false};
                while (true) {

                    Node succ = cur.nextAndRemoved[level].get(removed);
                    while (removed[0]) {
                        if (!pred.nextAndRemoved[level].compareAndSet(cur, succ, false, false)) {
                            continue retry;
                        }
                        cur = pred.nextAndRemoved[level].getReference();
                        succ = cur.nextAndRemoved[level].get(removed);
                    }

                    if (cur.x < x) {
                        pred = cur;
                        cur = succ;
                    } else {
                        break;
                    }
                }
                w.preds[level] = pred;
                w.succs[level] = cur;
                w.found = cur.x == x;

            }
            return w;
        }
    }

    @Override
    public boolean add(int x) {
        int topLevel = randomLevel();

        while (true) {
            Window w = findWindow(x);
            if (w.found)
                return false;

            Node newNode = new Node(x, topLevel);
            for (int level = 0; level <= topLevel; level++) {
                newNode.nextAndRemoved[level].set(w.succs[level], false);
            }

            if (!w.preds[0].nextAndRemoved[0].compareAndSet(w.succs[0], newNode, false, false))
                continue;
            for (int level = 1; level <= topLevel; level++)
                while (!w.preds[level].nextAndRemoved[level].compareAndSet(w.succs[level], newNode, false, false))
                    w = findWindow(x);

            return true;
        }
    }

    private int randomLevel() {
        return ThreadLocalRandom.current().nextInt(MAX_LEVEL);
    }

    @Override
    public boolean remove(int x) {

        Window w = findWindow(x);
        if (!w.found)
            return false;

        Node removingNode = w.succs[0];

        for (int level = removingNode.topLevel; level >= 1; level--) {

            boolean[] removed = {false};
            Node nextNode = removingNode.nextAndRemoved[level].get(removed);
            while (!removed[0]) {
                removingNode.nextAndRemoved[level].attemptMark(nextNode, true);
                nextNode = removingNode.nextAndRemoved[level].get(removed);
            }
        }

        boolean[] removed = {false};
        Node nextNode = w.succs[0].nextAndRemoved[0].get(removed);
        while (true) {
            if (removingNode.nextAndRemoved[0].compareAndSet(nextNode, nextNode, false, true)) {
                findWindow(x);
                return true;
            }
            nextNode = w.succs[0].nextAndRemoved[0].get(removed);
            if (removed[0])
                return false;
        }

    }

    @Override
    public boolean contains(int x) {
        Node pred = head;

        boolean answer=false;

        for (int level = MAX_LEVEL; level >= 0; level--) {
            Node cur = pred.nextAndRemoved[level].getReference();

            boolean[] removed = {false};
            while (true) {

                Node succ = cur.nextAndRemoved[level].get(removed);
                while (removed[0]) {
                    cur = succ;
                    succ = cur.nextAndRemoved[level].get(removed);
                }

                if (cur.x < x) {
                    pred = cur;
                    cur = succ;
                } else {
                    break;
                }
            }
            answer = cur.x == x;

        }
        return answer;
    }
}