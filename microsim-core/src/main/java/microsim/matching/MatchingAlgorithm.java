package microsim.matching;

import java.util.Collection;
import java.util.Comparator;

import org.apache.commons.collections4.Predicate;

public interface MatchingAlgorithm {
	
	@SuppressWarnings("rawtypes")
	public void matching(Collection collection1, Predicate filter1, Comparator comparator1, 
			Collection collection2, Predicate filter2, MatchingScoreClosure doubleClosure, MatchingClosure matching);
	
}
