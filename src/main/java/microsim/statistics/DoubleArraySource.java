package microsim.statistics;

import lombok.NonNull;

/**
 * Used by statistical objects to access arrays of double values.
 */
public interface DoubleArraySource {
    /**
     * Return the currently cached array of double values.
     *
     * @return An array of double or a null pointer if the source is empty.
     */
    double @NonNull [] getDoubleArray();
}
