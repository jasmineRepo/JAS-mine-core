package microsim.statistics.weighted;

import lombok.NonNull;

/**
 * Used by statistical object to access array of integer values.
 */
public interface WeightedIntArraySource {
    /**
     * Return the currently cached array of integer values.
     *
     * @return An array of double or a null pointer if the source is empty.
     */
    int @NonNull [] getIntArray();

    double @NonNull [] getWeights();
}
