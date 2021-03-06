package microsim.alignment.probability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import microsim.engine.SimulationEngine;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

public class SBDAlignment<T> extends AbstractProbabilityAlignment<T> {
		
	@Override
	public void align(Collection<T> agents, Predicate<T> filter, AlignmentProbabilityClosure<T> closure, double targetShare) {
		if (targetShare < 0. || targetShare > 1.) {
			throw new IllegalArgumentException("target probability must lie in [0,1]");
		}
		
		List<T> list = new ArrayList<T>();		
		if (filter != null)
			CollectionUtils.select(agents, filter, list);
		else
			list.addAll(agents);
		
		int n = list.size();
		
		Map<T, Double> map = new HashMap<T, Double>();
		for (int i=0; i<n; i++) {
			T agent = list.get(i); 
			double p = closure.getProbability(agent);
			double r = SimulationEngine.getRnd().nextDouble();
			map.put(agent, new Double(p-r));
		}
		map = sortByComparator(map, false); // true for ascending order.			//Returns a LinkedHashMap, that maintains the order of insertion.
		
		int i = 0;
		for (T agent : map.keySet()) {
			
			if (i <= targetShare*n) { 
				closure.align(agent, 1.0);
			}
			else { 
				closure.align(agent, 0.0);
			}
			i++;
		}
		
	}
	
}
