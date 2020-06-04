package of_deque;

/**
 * Deque interface.
 *
 * @author Nikita Koval
 */
public interface Deque<T> {

    /**
     * Inserts the specified element at the front of this deque
     *
     * @param x the element to add
     */
    void pushFirst(T x);

    /**
     * Inserts the specified element at the end of this deque
     * @param x the element to add
     */
    void pushLast(T x);

    /**
     * Retrieves, but does not remove, the first element of this deque
     * or throws NoSuchElementException if this deque is empty
     */
    T peekFirst();

    /**
     * Retrieves, but does not remove, the last element of this deque
     * or throws NoSuchElementException if this deque is empty
     */
    T peekLast();

    /**
     * Retrieves and removes the first element of this deque
     * or throws NoSuchElementException if this deque is empty
     */
    T popFirst();

    /**
     * Retrieves and removes the last element of this deque
     * or throws NoSuchElementException if this deque is empty
     */
    T popLast();
}
