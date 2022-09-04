package microsim.statistics;

/**
 * An updatable source is a class implementing one of the *Source interfaces, which needs
 * to be updated to refresh its data. Typically, a data source which retrieves data from
 * other source keeps them into a cache and refreshes the cache when the consumer needs
 * a new refresh.<br>
 * A CrossSection, for instance, is an updatable source, since it collects data from a
 * collection. Accessing its source interface, the consumer can obtain the latest cached
 * data, but if it wants updated ones it has to invoke the <i>updateSource()</i> method of
 * the CrossSection.<br>
 * Each statistical object contained in the <i>jas.statistics</i> package automatically refreshes
 * the UpdatableSource source before retrieving the latest data.
 */
public interface UpdatableSource {
	/**
	 * Force the source to update its currently cached data.
	 */
	void updateSource();
}
