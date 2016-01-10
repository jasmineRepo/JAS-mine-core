package microsim.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class SimpleMatching implements MatchingAlgorithm {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void matching(Collection collection1, Predicate filter1, Comparator comparator1, 
			Collection collection2, Predicate filter2, MatchingScoreClosure doubleClosure, MatchingClosure matching) {
		
		List c1 = new ArrayList();		
		if (filter1 != null)
			CollectionUtils.select(collection1, filter1, c1);
		else
			c1.addAll(collection1);
		
		Collections.sort(c1, comparator1);
		
		List c2 = new ArrayList();		
		if (filter2 != null)
			CollectionUtils.select(collection2, filter2, c2);
		else
			c2.addAll(collection2);
		
		if (CollectionUtils.intersection(c1, c2).size() > 0)
			throw new IllegalArgumentException("Matching algorithm cannot match not disjuctable collections");
		
		int elems = Math.min(c1.size(), c2.size());
		
		for (int i = 0; i < elems; i++) {
			Object agent1 = c1.get(i);
			
			SortedMap<Double, Object> sortedMap = new TreeMap<Double, Object>();
			for (Object candidate : c2) {
				Double score = doubleClosure.getValue(agent1, candidate);
				sortedMap.put(score, candidate);
			}
			Object partner = sortedMap.values().iterator().next();
			
			matching.match(agent1, partner);
			c2.remove(partner);
		}
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
