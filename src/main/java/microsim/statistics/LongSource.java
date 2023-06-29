package microsim.statistics;

import lombok.NonNull;

/**
 * Used by statistical object to access long data. Each variable must have a unique integer id.
 */
public interface LongSource {
    /**
     * Return the long value corresponding to the given variableID
     *
     * @param variableID A unique identifier for a variable.
     * @return The current long value of the required variable.
     */
    long getLongValue(final @NonNull Enum<?> variableID);

    enum Variables {
        Default
    }
}
