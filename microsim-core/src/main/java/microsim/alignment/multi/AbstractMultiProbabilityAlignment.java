package microsim.alignment.multi;

import java.util.List;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.LinkedHashMap;
//import java.util.LinkedList;
//import java.util.Map;
//import java.util.Map.Entry;

import org.apache.commons.collections.Predicate;

/**
 * Multiple choice alignment methods.
 * 
 * 
 * General principle of operation of this class: a) A variable is 
 * defined for the agents. b) Alignment is called to modify that variable, 
 * possibly making use of a specific process of the agents.
 * 
 * ALIGNMENT OF PROBABILITIES: this requires a process to be specified
 * that computes a set of probabilities of the potential outcomes of a given event.
 * Alignment is then called to modify those probabilities. A second process is
 * specified to determine the actual outcome of the event given the (modified)
 * probabilities of the potential outcomes. 
 * 
 * @author Matteo Richiardi and Ross Richardson
 * 
 * @param <T> - the Type parameter usually representing the agent class.
 * 
 */
public abstract class AbstractMultiProbabilityAlignment<T> {
	
	/**
	 * 
	 * @param agentList - a list of agents to potentially be aligned
	 * @param filter - filters the agentList so that only the relevant sub-population 
	 * 	of agents is aligned
	 * @param closure - specifies the method returning the unaligned probabilities 
	 * 	of outcomes for the agent and the method that samples the aligned probabilities
	 *  to specify the outcome.
	 * @param targetShare - a set of target shares of the relevant sub-population 
	 *  (specified as a proportion of the filtered population) for which the outcomes 
	 *  (defined by the AlignmentMultiProbabilityClosure) must be true.
	 * 
	 */
	public abstract void align(List<T> agentList, Predicate filter, AlignmentMultiProbabilityClosure<T> closure, double[] targetShare);

}
