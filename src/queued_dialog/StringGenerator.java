package queued_dialog;

import java.util.function.Supplier;

/**
 * Created by ivan on 01.06.17.
 */
class StringGenerator implements Supplier<String> {
    private int index;

    StringGenerator() {
        index = 0;
    }

    @Override
    public String get() {
        return "Message #" + Integer.toString(++index);
    }
}
