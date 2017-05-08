package microsim.alignment.probability;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.Predicate;

/**
 * Binary alignment methods. Acknowledgements: Partly based on
 * "Evaluating Binary Alignment Methods in Microsimulation Models", by Jinjing
 * Li and Cathal O'Donoghue, Journal of Artificial Societies and Social
 * Simulation, 2014 (http://jasss.soc.surrey.ac.uk/17/1/15/15.pdf)
 * 
 * 
 * General principle of operation of this class: a) Some variable is defined for
 * the agents (the variable can be a probability or an outcome). b) Alignment is
 * called to modify that variable, possibly making use of a specific process of
 * the agents.
 * 
 * ALIGNMENT OF PROBABILITIES: this requires a process is specified
 * that computes individual probabilities of the occurrence of a given event.
 * Alignment is then called to modify those probabilities. A second process is
 * specified to determine the occurrence of the event given the (modified)
 * probabilities. 
 *
 * @param agentList - a list of agents to potentially be aligned
 * @param filter - filters the agentList so that only the relevant sub-population 
 * 	of agents is aligned
 * @param closure - specifies the method returning the unaligned probability 
 * 	of the positive outcome for the agent and the method that samples the aligned 
 *  probabilities to specify the outcome.
 * @param targetShare - a target share of the relevant sub-population 
 *  (specified as a proportion of the filtered population) for which the outcome 
 *  (defined by the AlignmentProbabilityClosure) must be true.
 * @param <T> - the Type parameter usually representing the agent class.
 * 
 * @author Matteo Richiardi This version: July 2014
 * 
 * @param <T>
 * 
 */
public abstract class AbstractProbabilityAlignment<T> {
	
	public abstract void align(List<T> agentList, Predicate filter, AlignmentProbabilityClosure<T> closure, double targetShare);

	protected Map<T, Double> sortByComparator(Map<T, Double> unsortedMap,
			final boolean ascendingOrder) {

		List<Entry<T, Double>> list = new LinkedList<Entry<T, Double>>(
				unsortedMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<T, Double>>() {
			public int compare(Entry<T, Double> o1, Entry<T, Double> o2) {
				if (ascendingOrder) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());

				}
			}
		});

		// Maintaining insertion order with the help of LinkedList
		Map<T, Double> sortedMap = new LinkedHashMap<T, Double>();
		for (Entry<T, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
}
