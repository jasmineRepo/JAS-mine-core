package microsim.alignment.outcome;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.Predicate;

/**
 * 
 * Abstract class for BINARY OUTCOME alignment methods (for Binary
 * probability alignment, see microsim.alignment.probability package;
 * for Multiple choice probability alignment, see microsim.alignment.multi
 * package). 
 * 
 * Acknowledgements: Partly based on
 * "Evaluating Binary Alignment Methods in Microsimulation Models", by Jinjing
 * Li and Cathal O�Donoghue, Journal of Artificial Societies and Social
 * Simulation, 2014 (http://jasss.soc.surrey.ac.uk/17/1/15/15.pdf)
 * 
 * General principle of operation of this class: a) Some variable is defined for
 * the agents (the variable can be a probability or an outcome). b) Alignment is
 * called to modify that variable, possibly making use of a specific process of
 * the agents.
 * 
 * Hence: 1) ALIGNMENT OF PROBABILITIES: this requires a process is specified
 * that computes individual probabilities of the occurrence of a given event.
 * Alignment is then called to modify those probabilities. A second process is
 * specified to determine the occurrence of the event given the (modified)
 * probabilities. INPUTS: agentList: the list of agents; variableName: the name
 * of the variable containing the probability of the event; targetShare or
 * targetNumber: the share or number of positive outcomes in the population to
 * be aligned, to be used as target; method: the alignment method to be used.
 * 
 * 2) ALIGNMENT OF OUTCOMES: this requires a single process is specified that
 * computes the probability of an event and the determines its occurrence.
 * INPUTS: agentList: the list of agents; variableName: the name of the variable
 * containing the probability of the event; process: the name of the process to
 * be used by the alignment method to modify the variable at the individual
 * level; targetShare or targetNumber: the share or number of positive outcomes
 * in the population to be aligned, to be used as target; method: the alignment
 * method to be used.
 * 
 * @author Matteo Richiardi
 * @author Ross Richardson 
 * This version: July 2015
 * 
 * @param <T>
 */
public abstract class AbstractOutcomeAlignment<T> {

	public abstract void align(Collection<T> agents, Predicate filter, AlignmentOutcomeClosure<T> closure, double targetShare, int maxResamplingAttempts);
	
	public abstract void align(Collection<T> agents, Predicate filter, AlignmentOutcomeClosure<T> closure, int targetNumber, int maxResamplingAttempts);
	
}
