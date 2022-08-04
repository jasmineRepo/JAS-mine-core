package microsim.alignment.probability;

import microsim.alignment.AlignmentUtils;
import org.apache.commons.collections4.Predicate;

import java.util.Collection;
import java.util.List;

import static jamjam.Sum.sum;

/**
 * A class for multiplicative scaling alignment method. All probabilities are extracted from the collection
 * {@code agents} using provided {@code filter}. Further steps involve calculating the expected number of agents in this
 * state, computing the correction multiplicative factor, and correcting individual probabilities via {@code closure}.
 * @param <T> A class usually representing an agent.
 * @see <a href="https://www.jasss.org/17/1/15.html">Jinjing Li and Cathal O'Donoghue, Evaluating Binary Alignment
 * Methods in Microsimulation Models, Journal of Artificial Societies and Social Simulation 17 (1) 15</a>
 */
public class MultiplicativeScalingAlignment<T> implements AlignmentUtils<T> {

	public void align(Collection<T> agents, Predicate<T> filter, AlignmentProbabilityClosure<T> closure,
					  double targetProbability) {
		// TODO input validation, split the code
		if (targetProbability < 0. || targetProbability > 1.)
			throw new IllegalArgumentException("Target probability must lie in [0,1]");

		List<T> agentList = extractAgentList(agents, filter);

		double[] probabilities = new double[agentList.size()];

		for (var agentId = 0; agentId < agentList.size(); agentId++)
			probabilities[agentId] = closure.getProbability(agentList.get(agentId));
		double sum = sum(probabilities);

		double mFactor = targetProbability * agentList.size() / sum;

		for (T agent : agentList) closure.align(agent, closure.getProbability(agent) * mFactor);
	}
}
