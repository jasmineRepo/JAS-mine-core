package microsim.collection;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;

public class Aggregate {

	public static <T> void applyToFilter(Iterable<T> collection, Predicate<T> predicate, Closure<T> closure) {
		final List<T> filtered = new ArrayList<T>();
		CollectionUtils.select(collection, predicate, filtered);
		IterableUtils.forEach(filtered, closure);
	}

}
