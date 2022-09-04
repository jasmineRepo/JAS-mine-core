package microsim.alignment.probability;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import lombok.val;

import org.apache.commons.collections4.Predicate;

import microsim.alignment.AlignmentUtils;
import microsim.engine.SimulationEngine;

import static jamjam.Sum.cumulativeSum;

/**
 * set individual probability to 1 if there is a change of the integer part of the cumulated probability, 0 otherwise
 * @param <T>
 */
public class SidewalkAlignment<T> implements AlignmentUtils<T> {
// todo docs, input validation, test, original research
	public void align(Collection<T> agents, Predicate<T> filter, AlignmentProbabilityClosure<T> closure,
					  double targetProbability) {
		if (targetProbability < 0. || targetProbability > 1.)
			throw new IllegalArgumentException("Target probability must lie in [0,1]");

		List<T> list = extractAgentList(agents, filter);
		if (list.size() == 0)
			return;

		Collections.shuffle(list, SimulationEngine.getRnd());// fixme does not generate 1

		val cumulativeProb = cumulativeSum(list.stream().mapToDouble(closure::getProbability).toArray());
		val cProbTruncated = IntStream.range(0, list.size()).map(i -> (int) cumulativeProb[i]).toArray();

		for (var i = 0; i < list.size(); i++)
			closure.align(list.get(i), cProbTruncated[i] != (i == 0 ? 0 : cProbTruncated[i - 1]) ? 1.0 : 0.0);
	}
}
