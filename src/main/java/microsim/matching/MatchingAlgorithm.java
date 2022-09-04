package microsim.matching;

import java.util.Collection;
import java.util.Comparator;

import org.apache.commons.collections4.Predicate;

public interface MatchingAlgorithm<T> {

//	@SuppressWarnings("rawtypes")
	public void matching(Collection<T> collection1, Predicate<T> filter1, Comparator<T> comparator1,
			Collection<T> collection2, Predicate<T> filter2, MatchingScoreClosure<T> doubleClosure, MatchingClosure<T> matching);

}
