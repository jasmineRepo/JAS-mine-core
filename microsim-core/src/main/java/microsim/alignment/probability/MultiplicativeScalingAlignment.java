package microsim.alignment.probability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class MultiplicativeScalingAlignment<T> extends AbstractProbabilityAlignment<T> {

	@Override
	public void align(Collection<T> agents, Predicate filter, AlignmentProbabilityClosure<T> closure, double targetShare) {
		if (targetShare < 0. || targetShare > 1.) {
			throw new IllegalArgumentException("target probability must lie in [0,1]");
		}
		
		List<T> list = new ArrayList<T>();		
		if (filter != null)
			CollectionUtils.select(agents, filter, list);
		else
			list.addAll(agents);
		
		int n = list.size();
		double sum = 0;
		
		// compute total expected number of simulated positive outcomes
		sum = 0; 
		for (int i=0; i<n; i++) {
			T agent = list.get(i);
			sum += closure.getProbability(agent);
		}
		
		// compute correction factor
		double m = targetShare * n / sum; // multiplicative factor
		
		// correct individual probabilities
		for (int i=0; i<n; i++) {
			T agent = list.get(i);
			double val = closure.getProbability(agent);
			closure.align(agent, val * m);			
		}

	}	

}
