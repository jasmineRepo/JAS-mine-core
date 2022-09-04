package microsim.matching;
//package microsim.matching;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.math3.util.Pair;

public interface IterativeMatchingAlgorithm<T> {

//	@SuppressWarnings("rawtypes")
	public Pair<Set<T>, Set<T>> matching(Collection<T> collection1, Predicate<T> filter1, Comparator<T> comparator1,
			Collection<T> collection2, Predicate<T> filter2, MatchingScoreClosure<T> doubleClosure, MatchingClosure<T> matching);

}
