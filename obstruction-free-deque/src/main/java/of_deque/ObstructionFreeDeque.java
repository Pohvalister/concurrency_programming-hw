package of_deque;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

public class ObstructionFreeDeque<T> implements Deque<T> {
    private static final Object RN = new Object();
    private static final Object LN = new Object();
    private static final Object DN = new Object();

    private final int maxElements;
    private final AtomicStampedReference<Object>[] values;
    private final AtomicInteger leftPlace, rightPlace;

    public ObstructionFreeDeque(int maxElements) {
        this.maxElements = maxElements + 2;
        values = new AtomicStampedReference[this.maxElements];
        for (int i = 0; i < values.length - 1; i++) {
            values[i] = new AtomicStampedReference<>(LN, 0);
        }
        values[values.length - 1] = new AtomicStampedReference<>(RN, 0);

        leftPlace = new AtomicInteger(values.length - 1);
        rightPlace = new AtomicInteger(0);
    }

    private final class oracleAnswer {
        Object prevVal, currVal;
        int prevSt, currSt;
        int prev, curr, next, nnex;

        oracleAnswer(Object prevVal, int prevSt, Object currVal, int currSt, int curr, int neihbMove) {
            this.prevVal = prevVal;
            this.currVal = currVal;
            this.prevSt = prevSt;
            this.currSt = currSt;
            this.prev = cycle(curr - neihbMove);
            this.curr = curr;
            this.next = cycle(curr + neihbMove);
            this.nnex = cycle(curr + neihbMove + neihbMove);
        }
    }

    private int cycle(int val) {
        return (val + this.maxElements) % this.maxElements;
    }

    private oracleAnswer oracle(boolean isRight) {
        int neihbMove;
        Object checkNull;
        AtomicInteger considerPlace;
        if (isRight) {
            neihbMove = 1;
            checkNull = RN;
            considerPlace = rightPlace;
        } else {
            neihbMove = -1;
            checkNull = LN;
            considerPlace = leftPlace;
        }

        modifOracle:
        while (true) {
            int k;

            findOracle:
            while (true) {
                k = considerPlace.get();
                Object cur = values[cycle(k)].getReference();

                if (cur != checkNull && cur != DN) {
                    considerPlace.compareAndSet(k, cycle(k + neihbMove));
                    continue findOracle;
                }

                Object neihgb = values[cycle(k - neihbMove)].getReference();
                if (!neihgb.equals(checkNull))
                    break findOracle;
                else
                    considerPlace.compareAndSet(k, cycle(k - neihbMove));
            }

            //window
            int prev = cycle(k - neihbMove);
            int curr = cycle(k);

            //collecting all info
            int[] prevSt = new int[1];
            int[] currSt = new int[1];
            Object prevVal = values[cycle(prev)].get(prevSt);
            Object currVal = values[cycle(curr)].get(currSt);

            if (currVal == checkNull && prevVal != checkNull)
                return new oracleAnswer(prevVal, prevSt[0], currVal, currSt[0], k, neihbMove);
            if (currVal == DN && prevVal != checkNull && prevVal != DN)
                if (values[prev].compareAndSet(prevVal, prevVal, prevSt[0], prevSt[0] + 1))
                    if (values[curr].compareAndSet(currVal, checkNull, currSt[0], currSt[0] + 1))
                        return new oracleAnswer(prevVal, prevSt[0] + 1, checkNull, currSt[0] + 1, k, neihbMove);
        }

    }

    private void push(boolean isRight, T x) {
        Object fNull, sNull;
        if (isRight) {
            fNull = RN;
            sNull = LN;
        } else {
            fNull = LN;
            sNull = RN;
        }

        while (true) {
            oracleAnswer oAnsw = oracle(isRight);
            int[] nextSt = new int[1];
            Object nextVal = values[oAnsw.next].get(nextSt);

            if (nextVal == fNull)
                if (values[oAnsw.prev].compareAndSet(oAnsw.prevVal, oAnsw.prevVal, oAnsw.prevSt, oAnsw.prevSt + 1))
                    if (values[oAnsw.curr].compareAndSet(oAnsw.currVal, x, oAnsw.currSt, oAnsw.currSt + 1))
                        return;

            if (nextVal == sNull)
                if (values[oAnsw.curr].compareAndSet(oAnsw.currVal, fNull, oAnsw.currSt, oAnsw.currSt + 1))
                    values[oAnsw.next].compareAndSet(nextVal, DN, nextSt[0], nextSt[0] + 1);

            if (nextVal == DN) {
                int[] nnexSt = new int[1];
                Object nnexVal = values[oAnsw.nnex].get(nnexSt);

                if (nnexVal != RN && nnexVal != LN && nnexVal != DN)
                    if (values[oAnsw.prev].compareAndSet(oAnsw.prevVal, oAnsw.prevVal, oAnsw.prevSt, oAnsw.prevSt))
                        if (values[oAnsw.curr].compareAndSet(oAnsw.currVal, oAnsw.currVal, oAnsw.currSt, oAnsw.currSt))
                            throw new IllegalStateException();

                if (nnexVal == sNull)
                    if (values[oAnsw.nnex].compareAndSet(nnexVal, nnexVal, nnexSt[0], nnexSt[0] + 1))
                        values[oAnsw.next].compareAndSet(nextVal, fNull, nextSt[0], nextSt[0] + 1);
            }

        }

    }

    private T get(boolean isRight, boolean isRemain) {
        Object fNull, sNull;
        if (isRight) {
            fNull = RN;
            sNull = LN;
        } else {
            fNull = LN;
            sNull = RN;
        }

        while (true) {
            oracleAnswer oAnsw = oracle(isRight);
            if (oAnsw.prevVal == sNull || oAnsw.prevVal == DN)
                if (values[oAnsw.prev].compareAndSet(oAnsw.prevVal, oAnsw.prevVal, oAnsw.prevSt, oAnsw.prevSt))
                    throw new NoSuchElementException();

            if (values[oAnsw.curr].compareAndSet(oAnsw.currVal, fNull, oAnsw.currSt, oAnsw.currSt + 1))
                if (values[oAnsw.prev].compareAndSet(oAnsw.prevVal, (isRemain ? oAnsw.prevVal : fNull), oAnsw.prevSt, oAnsw.prevSt + 1))
                    return (T) oAnsw.prevVal;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException if the deque is full
     */
    @Override
    public void pushFirst(T x) {
        push(false, x);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException if the deque is full
     */
    @Override
    public void pushLast(T x) {
        push(true, x);
    }

    @Override
    public T peekFirst() {
        return get(false, true);
    }

    @Override
    public T peekLast() {
        return get(true, true);
    }

    @Override
    public T popFirst() {
        return get(false, false);
    }

    @Override
    public T popLast() {
        return get(true, false);
    }
}