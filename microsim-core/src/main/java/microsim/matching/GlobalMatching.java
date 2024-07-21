package microsim.matching;
//package microsim.matching;

import microsim.statistics.regression.RegressionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.math3.util.Pair;

import java.util.*;

/**
 * MATCHING CLASS BASED ON THE ITERATIVE RANDOM MATCHING CLASS
 *
 * Whereas the IterativeRandomMatching class matches units in collection1 to those in collection2 in order of collection1
 * the current class evaluates all potential match combinations between collection1 and collection2 and proceeds to
 * select matches from lowest to highest "score". The routine is consequently agnostic concerning the order of each collection.
 */
public class GlobalMatching<T> {

	public Pair<Set<T>, Set<T>> matching(Collection<T> collection1, Predicate<T> filter1, Collection<T> collection2,
										 Predicate<T> filter2, MatchingScoreClosure<T> doubleClosure, MatchingClosure<T> matching) {

		LinkedHashSet<T> unmatchedCollection1 = new LinkedHashSet<T>();
		LinkedHashSet<T> unmatchedCollection2 = new LinkedHashSet<T>();
		Pair<Set<T>, Set<T>> unmatched = new Pair<>(unmatchedCollection1, unmatchedCollection2);

		Set<T> c1 = new HashSet<T>();
		if (filter1 != null)
			CollectionUtils.select(collection1, filter1, c1);
		else
			c1.addAll(collection1);

		Set<T> c2 = new HashSet<T>();
		if (filter2 != null)
			CollectionUtils.select(collection2, filter2, c2);
		else
			c2.addAll(collection2);

		if (CollectionUtils.intersection(c1, c2).size() > 0)
			throw new IllegalArgumentException("Matching algorithm cannot match not disjuctable collections");

		// evaluate list of global candidate pairs
		List<GlobalMatchingPair> candidates = new ArrayList<>();
		for (T agent1 : c1) {

			for (T agent2 : c2) {

				Double score = doubleClosure.getValue(agent1, agent2);
				if (Double.isFinite(score)) {

					GlobalMatchingPair pair = new GlobalMatchingPair(agent1, agent2, score);
					candidates.add(pair);
				}
			}
		}

		// sort candidate pairs from best to worst candidate matches
		Collections.sort(candidates, new GlobalMatchingPairComparator());

		// allocate matches
		for (int ii=0; ii < candidates.size(); ii++) {
			GlobalMatchingPair pair = candidates.get(ii);
			T agent1 = (T) pair.getAgent1();
			T agent2 = (T) pair.getAgent2();
			if (c1.contains(agent1) && c2.contains(agent2)) {
				matching.match(agent1, agent2);
				c1.remove(agent1);
				c2.remove(agent2);
			}
		}

		unmatchedCollection1.addAll(c1);
		unmatchedCollection2.addAll(c2);

		return unmatched;
	}

	private GlobalMatching() {
		
	}
	
	private static GlobalMatching globalMatching;
	
	public static GlobalMatching getInstance() {
		if (globalMatching == null)
			globalMatching = new GlobalMatching();
		
		return globalMatching;
	}
}
