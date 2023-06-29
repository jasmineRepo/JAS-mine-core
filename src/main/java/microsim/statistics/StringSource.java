package microsim.statistics;

import lombok.NonNull;

/**
 * Used by statistical object to access string data. Each variable must have a unique integer id.
 */
public interface StringSource {
    /**
     * The default variable id.
     */
    int DEFAULT = 0;

    /**
     * Return the double value corresponding to the given variableID
     *
     * @param variableID A unique identifier for a variable.
     * @return The current double value of the required variable.
     */
    @NonNull String getStringValue(final @NonNull Enum<?> variableID);
}
