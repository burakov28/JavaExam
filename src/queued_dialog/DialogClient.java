package queued_dialog;

import com.sun.istack.internal.NotNull;

import java.util.AbstractQueue;

/**
 * Created by ivan on 01.06.17.
 */

/**
 * Client's class for concurrent dialog and ask element every given number of millis for a new message
 * @param <T> type of messages
 */
public class DialogClient<T> implements Runnable {
    private final AbstractQueue<T> queue;
    private final long latency;

    /**
     * Run client
     */
    @Override
    public void run() {
        System.out.println("Client was started");
        try {
            while (!Thread.interrupted()) {
                Thread.sleep(latency);

                if (queue.size() != 0) {
                    T message = queue.poll();
                    System.out.println("Client receive message: " + message.toString());
                }
            }
        } catch (InterruptedException ignored) {}
        System.out.println("Client was interrupted");
    }

    /**
     * Constructor
     * @param queue DialogQueue for dialog
     * @param latency waiting time in millis between checking queue, must be not less than 10
     */
    public DialogClient(@NotNull DialogQueue<T> queue, long latency) {
        if (latency < 0) {
            throw new IllegalArgumentException("Client's latency must be non negative");
        }
        if (latency < Dialog.MINIMAL_LATENCY) {
            throw new IllegalArgumentException("Client wants to check queue too often");
        }
        if (queue == null) {
            throw new IllegalArgumentException("Client's queue mustn't be null");
        }

        this.queue = queue;
        this.latency = latency;
    }
}
