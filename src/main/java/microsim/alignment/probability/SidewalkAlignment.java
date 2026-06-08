package microsim.alignment.probability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import microsim.engine.SimulationEngine;

public class SidewalkAlignment<T> extends AbstractProbabilityAlignment<T> {

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
		
		Collections.shuffle(list, SimulationEngine.getRnd());
		int n = list.size();
		double sum = 0;
		
		for (int i=0; i<n; i++) {
			T agent = list.get(i);
			
			int oldSum = (int)sum;
			
			// update cumulated probability
			sum += closure.getProbability(agent);
			
			// set individual probability to 1 if there is a change of the integer part of the cumulated probability, 0 otherwise
			if ((int)sum != oldSum) { 
				closure.align(agent, 1.0);
			}
			else { 
				closure.align(agent, 0.0);
			}
		}
		
	}

}
