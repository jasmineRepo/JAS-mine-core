package microsim.alignment.probability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import microsim.engine.SimulationEngine;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class SBDAlignment<T> extends AbstractProbabilityAlignment<T> {
		
	@Override
	public void align(List<T> agentList, Predicate filter, AlignmentProbabilityClosure<T> closure, double targetShare) {
		if (targetShare < 0 || targetShare > 1) {
			System.err.println("target probability must lie in [0,1]");
			System.exit(0);
		}
		
		List<T> list = new ArrayList<T>();		
		if (filter != null)
			CollectionUtils.select(agentList, filter, list);
		else
			list.addAll(agentList);
		
		Collections.shuffle(list);
		int n = list.size();
		
		Map<T, Double> map = new HashMap<T, Double>();
		for (int i=0; i<n; i++) {
			T agent = list.get(i); 
			double p = closure.getProbability(agent);
			double r = SimulationEngine.getRnd().nextDouble();
			map.put(agent, new Double(p-r));
		}
		sortByComparator(map, false); // true for ascending order
		for (int i=0; i<n; i++) {
			T agent = list.get(i); 
			if (i<targetShare*n) 
				closure.align(agent, 1.0);				
			else
				closure.align(agent, 0.0);				
		}
		
	}
	
}
