package microsim.matching;
//package microsim.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.math3.util.Pair;

import microsim.engine.SimulationEngine;
import microsim.statistics.regression.RegressionUtils;

public class IterativeRandomMatching<T> implements IterativeMatchingAlgorithm<T> {

//	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Pair<Set<T>, Set<T>> matching(Collection<T> collection1, Predicate<T> filter1, Comparator<T> comparator1,
			Collection<T> collection2, Predicate<T> filter2, MatchingScoreClosure<T> doubleClosure, MatchingClosure<T> matching) {

		LinkedHashSet<T> unmatchedCollection1 = new LinkedHashSet<T>();
		LinkedHashSet<T> unmatchedCollection2 = new LinkedHashSet<T>();
		Pair<Set<T>, Set<T>> unmatched = new Pair<>(unmatchedCollection1, unmatchedCollection2);

//		long numberMatchesMade = 0l;

		List<T> c1 = new ArrayList<T>();
		if (filter1 != null)
			CollectionUtils.select(collection1, filter1, c1);
		else
			c1.addAll(collection1);

		if(comparator1 != null) {
			Collections.sort(c1, comparator1);
		}
		else {	//As comparator is null, it means that no particular order is specified to iterate through collection c1
			try{		//Try to use 'natural ordering' of c1 if defined.
				Collections.sort(c1, comparator1);
			}
			catch (ClassCastException e) {
				Collections.shuffle(c1, SimulationEngine.getRnd());		//If cannot cast T to Comparator, then just randomize the collection c1
			}
		}

		List<T> c2 = new ArrayList<T>();
		if (filter2 != null)
			CollectionUtils.select(collection2, filter2, c2);
		else
			c2.addAll(collection2);

		if (CollectionUtils.intersection(c1, c2).size() > 0)
			throw new IllegalArgumentException("Matching algorithm cannot match not disjuctable collections");

//		int elems = Math.min(c1.size(), c2.size());
//		for (int i = 0; i < elems; i++) {
		for (int i = 0; i < c1.size(); i++) {		//Now check all agents in c1, because a match does not always occur (if, for example the matching score is infinity or NaN).
			T agent1 = c1.get(i);

			Map<T, Double> potentialMatches = new LinkedHashMap<T, Double>();
			for (T candidate : c2) {
				Double score = doubleClosure.getValue(agent1, candidate);
				if(Double.isFinite(score)) {	//Do not add people with scores of infinity or NaN
					potentialMatches.put(candidate, Math.exp(-score));		//Lower scores are more likely.  Note therefore that we shouldn't use a pseudo-normal (i.e. Math.exp(-score*score/2), because that would be a symmetric distribution, whereas we need one that favours one direction of the score (i.e. negative - a large negative score is more favoured than a small positive one, unlike in the normal dist.).
				}
			}

			//Randomly sample a partner with a probability associated with the score (lowest scores are more likely)
			if(!potentialMatches.isEmpty()) {		//If no potential partners, move on to the next agent1
				T partner = RegressionUtils.event(potentialMatches, false);
				matching.match(agent1, partner);
				c2.remove(partner);
//				numberMatchesMade++;
			}
			else {
				unmatchedCollection1.add(agent1);
			}

		}

		unmatchedCollection2.addAll(c2);

//		if(numberMatchesMade == 0) {
//			throw new IllegalArgumentException("Error - no matches have occurred, check the arguments of the matching method!");
//		}

		return unmatched;
	}

	private IterativeRandomMatching() {

	}

	private static IterativeRandomMatching iterativeRandomMatching;

	public static IterativeRandomMatching getInstance() {
		if (iterativeRandomMatching == null)
			iterativeRandomMatching = new IterativeRandomMatching();

		return iterativeRandomMatching;
	}
}
