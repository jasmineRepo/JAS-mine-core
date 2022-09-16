package microsim.statistics;

import lombok.NonNull;

/**
 * Used by statistical object to access integer data. Each variable must have a unique integer id.
 */
public interface IntSource {
    /**
     * Return the integer value corresponding to the given variableID
     *
     * @param variableID A unique identifier for a variable.
     * @return The current integer value of the required variable.
     */
    int getIntValue(final @NonNull Enum<?> variableID);

    enum Variables {
        Default
    }
}
