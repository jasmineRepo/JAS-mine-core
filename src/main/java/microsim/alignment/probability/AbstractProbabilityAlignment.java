package microsim.alignment.probability;

import org.apache.commons.collections4.Predicate;

import java.util.Collection;

/**
 * Abstract class for BINARY PROBABILITY alignment methods (for Binary
 * outcome alignment, see microsim.alignment.outcome package;
 * for Multiple choice probability alignment, see microsim.alignment.multi
 * package).
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
 * 
 * @author Matteo Richiardi This version: July 2014
 * 
 * @param <T> - the Type parameter usually representing the agent class.
 *
 */
public abstract class AbstractProbabilityAlignment<T> {

	/**
	 * Method to implement alignment.
	 * 
	 * @param agents - a collection of agents to potentially be aligned
	 * @param filter - filters the agentList so that only the relevant sub-population 
	 * 	of agents is aligned
	 * @param closure - specifies the method returning the unaligned probability 
	 * 	of the positive outcome for the agent and the method that samples the aligned 
	 *  probabilities to specify the outcome.
	 * @param targetShare - a target share of the relevant sub-population 
	 *  (specified as a proportion of the filtered population) for which the outcome 
	 *  (defined by the AlignmentProbabilityClosure) must be true.
	 */
	public abstract void align(Collection<T> agents, Predicate<T> filter, AlignmentProbabilityClosure<T> closure,
							   double targetShare);
}
