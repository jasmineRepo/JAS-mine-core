package microsim.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;

public class Aggregate {

	public static void applyToFilter(Collection<?> collection, Predicate predicate, Closure closure) {
		@SuppressWarnings("rawtypes")
		final List<?> filtered = new ArrayList();
		org.apache.commons.collections.CollectionUtils.select(collection, predicate, filtered);
		org.apache.commons.collections.CollectionUtils.forAllDo(filtered, closure);
	}

}
