package microsim.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.math3.util.Pair;

import microsim.engine.SimulationEngine;

public class SimpleMatching<T> implements MatchingAlgorithm<T> {

//	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void matching(Collection<T> collection1, Predicate<T> filter1, Comparator<T> comparator1, 
			Collection<T> collection2, Predicate<T> filter2, MatchingScoreClosure<T> doubleClosure, MatchingClosure<T> matching) {
		
		long numberMatchesMade = 0l;
		
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
				Collections.shuffle(c1, SimulationEngine.getRnd());	//If cannot cast T to Comparator, then just randomize the collection c1 
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
			
			List<Pair<Double, T>> listToSort = new ArrayList<>();
			for (T candidate : c2) {
				Double score = doubleClosure.getValue(agent1, candidate);
				if(Double.isFinite(score)) {	//Do not add people with scores of infinity or NaN
					listToSort.add(new Pair<Double, T>(score, candidate));
				}
			}
			
			//List in ascending order of score
			listToSort.sort(new Comparator<Pair<Double, T>>(){
				@Override
				public int compare(Pair<Double, T> pair1, Pair<Double, T> pair2) {					
					return (int) Math.signum(pair1.getFirst() - pair2.getFirst());					
				}			
			});
			
//			for(Pair<Double, T> p: listToSort) {
//				System.out.println("Score: " + p.getFirst());
//			}
			if(!listToSort.isEmpty()) {		//If no potential partners, move on to the next agent1
				T partner = listToSort.get(0).getSecond();
				matching.match(agent1, partner);
				c2.remove(partner);
				numberMatchesMade++;
			}
			
		}

		if(numberMatchesMade == 0) {
			throw new IllegalArgumentException("Error - no matches have occurred, check the arguments of the matching method!");
		}
//		return numberMatchesMade;
	}

	private SimpleMatching() {
		
	}
	
	private static SimpleMatching simpleMatching;
	
	public static SimpleMatching getInstance() {
		if (simpleMatching == null)
			simpleMatching = new SimpleMatching();
		
		return simpleMatching;
	}
}