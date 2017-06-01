package queued_dialog;

import java.util.function.Supplier;

/**
 * Created by ivan on 01.06.17.
 */

/**
 * Dialog class generate two threads with {@link DialogClient} and {@link DialogServer} with {@link DialogQueue},
 *  with specified minimal latency to avoid busy wait
 * @param <T> type of messages
 */
public class Dialog<T> implements AutoCloseable {
    /**
     * Minimal latency in millis for client and server to avoid busy wait
     */
    public static final long MINIMAL_LATENCY = 10;

    private final Thread clientThread;
    private final Thread serverThread;


    /**
     *
     * @param clientLatency client's latency {@link DialogClient}
     * @param serverMinLatency server's min latency {@link DialogServer}
     * @param serverMaxLatency server's max latency {@link DialogServer}
     * @param messageGenerator message generator, mustn't be null
     * @param queueSizeLimit maximal size of {@link DialogQueue}
     * @throws IllegalArgumentException in case of bad arguments, see: {@link DialogServer} and {@link DialogClient}
     */
    public Dialog(long clientLatency, long serverMinLatency, long serverMaxLatency, Supplier<T> messageGenerator, int queueSizeLimit)
            throws IllegalArgumentException {

        DialogQueue<T> queue = new DialogQueue<>(queueSizeLimit);
        DialogClient<T> client = new DialogClient<>(queue, clientLatency);
        DialogServer<T> server = new DialogServer<>(queue, serverMinLatency, serverMaxLatency, messageGenerator);
        clientThread = new Thread(client);
        serverThread = new Thread(server);
    }

    /**
     * Start client's and server's threads
     */
    public void start() {
        clientThread.start();
        serverThread.start();
    }

    /**
     * Interrupt client's and server's threads
     */
    @Override
    public void close() {
        clientThread.interrupt();
        serverThread.interrupt();
    }

    /**
     * Join to client's and server's threads
     */
    public void join() {
        try {
            clientThread.join();
            serverThread.join();
        } catch (InterruptedException e) {
            System.out.println("Execute thread was stopped");
        }
    }

    private static void printUsages() {
        System.out.println("Usage: client's latency, server's min latency, server's max latency");
    }


    /**
     * Entry point of program. Receive client's latency, server's min latency and server's max latency
     * @param args arguments of command line
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Wrong number of arguments");
            printUsages();
            return;
        }

        long clientLatency;
        long serverMinLatency;
        long serverMaxLatency;
        try {
            clientLatency = Long.parseLong(args[0]);
            serverMinLatency = Long.parseLong(args[1]);
            serverMaxLatency = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("Wrong number format: " + e.getMessage());
            printUsages();
            return;
        }

        try (Dialog<String> dialog =
                     new Dialog<>(clientLatency, serverMinLatency, serverMaxLatency, new StringGenerator(), 10)){
            dialog.start();
            dialog.join();
            //Thread.sleep(3000);
        } catch (IllegalArgumentException e) {
            System.out.println("Wrong argument: " + e.getMessage());
        } //catch (InterruptedException ignored) {}
    }
}


