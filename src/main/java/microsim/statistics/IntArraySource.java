package microsim.statistics;

import lombok.NonNull;

/**
 * Used by statistical objects to access arrays of integer values.
 */
public interface IntArraySource {
    /**
     * Returns a particular array of {@code integer} values.
     *
     * @return an array of {@code integer}.
     */
    int @NonNull [] getIntArray();
}
