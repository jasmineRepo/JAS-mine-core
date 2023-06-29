package microsim.statistics;

import lombok.NonNull;

/**
 * Used by statistical object to access object data. Each variable must have a unique integer id.
 */
public interface ObjectSource {

    /**
     * Return the value corresponding to the given variableID
     *
     * @param variableID A unique identifier for a variable.
     * @return The current value of the required variable.
     */
    @NonNull Object getObjectValue(final @NonNull Enum<?> variableID);

    enum Variables {
        Default
    }
}
