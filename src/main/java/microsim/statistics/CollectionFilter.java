package microsim.statistics;

/**
 * A collection filter is used by the CrossSection class to filter elements of 
 * the source collection.
 * <p>Imagine to have a list of agents of the class MyAgent, which contains a boolean variable
 * isFemale and an int variable age.</p>
 * <p>In order to obtain the list of ages of the males in the collection, the user can
 * crate a class inheriting from ICollectionFilter</p>
 * <p>During the cross section iteration of the list, the isFiltered method is passed the 
 * current object and the relative value is read only if the filter returns true.</p>
 */
public interface CollectionFilter {
	/**
	 * Return if the passed object must be considered by the CrossSection iterator.
	 * @param object The current object in the CrossSection iteration of the source collection.
	 * @return True if object has the required status, false otherwise.
	 */
	boolean isFiltered(Object object);
}
