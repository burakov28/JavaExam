package queued_dialog;

import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 * Created by ivan on 01.06.17.
 */

/**
 * Queue which provides concurrent work and guarantees that in any time in queue no more than given number of elements
 * @param <T> type of stored objects
 */
public class DialogQueue<T> extends AbstractQueue<T> {
    private final Queue<T> internalQueue;
    private final int sizeLimit;

    /**
     * Constructor
     * @param sizeLimit maximal number of elements in queue
     */
    public DialogQueue(int sizeLimit) {
        if (sizeLimit <= 0) {
            throw new IllegalArgumentException("Size Limit must be greater than zero");
        }
        internalQueue = new ArrayDeque<>();
        this.sizeLimit = sizeLimit;
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("Dialog queue doesn't have an iterator");
    }

    /**
     * Blocks queue and return its size
     * @return size of queue
     */
    @Override
    public int size() {
        synchronized (internalQueue) {
            return internalQueue.size();
        }
    }

    /**
     * Passive wait while in queue exactly initially given number of elements and after that insert given one
     * @param t element for insertion
     * @return true in case of successful insertion, false otherwise
     */
    @Override
    public boolean offer(T t) {
        synchronized (internalQueue) {
            try {
                while (internalQueue.size() == sizeLimit) {
                    internalQueue.wait();
                }

                int oldSize = internalQueue.size();
                boolean result = internalQueue.offer(t);
                if (oldSize == 0 && internalQueue.size() > 0) {
                    internalQueue.notify();
                }

                return result;
            } catch (InterruptedException e) {
                return false;
            }
        }
    }

    /**
     * Passive waits while queue empty and after that removes first element and returns it
     * @return removed element of null in case of error {@link AbstractQueue}
     */
    @Override
    public T poll() {
        synchronized (internalQueue) {
            try {
                while (internalQueue.size() == 0) {
                    internalQueue.wait();
                }

                int oldSize = internalQueue.size();
                T result = internalQueue.poll();
                if (oldSize == sizeLimit && internalQueue.size() != sizeLimit) {
                    internalQueue.notify();
                }
                return result;
            } catch (InterruptedException e) {
                return null;
            }
        }
    }

    /**
     * Passive waits while queue empty and after that returns the first element
     * @return the first element of null in case of error {@link AbstractQueue}
     */
    @Override
    public T peek() {
        synchronized (internalQueue) {
            try {
                while (internalQueue.size() == 0) {
                    internalQueue.wait();
                }

                return internalQueue.peek();
            } catch (InterruptedException e) {
                return null;
            }
        }
    }
}
