package microsim.statistics;

import lombok.NonNull;

/**
 * A collection filter is used by the {@link CrossSection} class to filter elements of the source collection.
 * <p>Imagine to have a list of agents of the class {@link microsim.agent.Agent}, which contains a {@code boolean}
 * variable {@code isFemale} and an {@code int} variable {@code age}.</p> <p>In order to obtain the list of ages of the
 * males in the collection, the user can crate a class implementing {@link CollectionFilter}.</p>
 * <p>During the cross section iteration of the list, every object is passed to the
 * {@link CollectionFilter#isFiltered(Object)} method and the corresponding value is read only when the filter returns
 * true.</p>
 */
public interface CollectionFilter {
    /**
     * Returns a {@code boolean} value when the passed {@code objectToFilter} must be considered by the
     * {@link CrossSection} iterator.
     *
     * @param objectToFilter The current object in the {@link CrossSection} iteration of the source collection.
     * @return true if objectToFilter has the required status, false otherwise.
     */
    boolean isFiltered(final @NonNull Object objectToFilter);
}
