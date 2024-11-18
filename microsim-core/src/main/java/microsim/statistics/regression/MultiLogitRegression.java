package microsim.statistics.regression;

import java.util.*;

import microsim.data.MultiKeyCoefficientMap;
import microsim.statistics.IDoubleSource;

public class MultiLogitRegression<T extends Enum<T> & IntegerValuedEnum> {

	private MultiChoiceMap<T> mapObject;


	public MultiLogitRegression(Class<T> enumType, Map<T, MultiKeyCoefficientMap> maps) {
		mapObject = new MultiChoiceMap<>(enumType, maps);
	}


	public <E extends Enum<E>> Map<T,Double> getProbabilities(IDoubleSource iDblSrc, Class<E> Regressors) {

		Map<T, Double> scores = mapObject.getScores(iDblSrc, Regressors);
		Map<T, Double> probs = new HashMap<>();
		double denominator = 1.0;
		for (T event : scores.keySet()) {
			double expScore = Math.exp(scores.get(event));
			probs.put(event, expScore);
			denominator += expScore;
		}

		T[] events = mapObject.getEnumType().getEnumConstants();
		int countNullEventProbs = 0;
		for (int ii = 0; ii < events.length; ii++) {
			Double val = probs.get(events[ii]);
			if (val == null) {
				// missing should be the base of the multinomial logit - with probability 1 / (1 + sum exp(xb))
				countNullEventProbs++;
				if (countNullEventProbs > 1) {
					throw new RuntimeException("MultiLogitRegression has been constructed with a map that does not contain enough of the possible values of the type T.  The map should contain the full number of T values, or one less than the full number of T values (in which case, the missing value is considered the 'default' case whose regression betas are all zero).");
				} else {
					probs.put((T) events[ii], 1.0 / denominator);        //The normalised probability of the base case is 0.5/denominator as the 0.5 comes from applying the Logit transform (the cumulative standard normal distribution) to the score of 0, and the denominator is the sum of Logit transforms for all events.
				}
			} else {
				// all options other than the base have probability exp(xbi) / (1 + sum exp(xb))
				probs.put((T) events[ii], val / denominator);
			}
		}
		if (countNullEventProbs != 1)
			throw new RuntimeException("MultiLogitRegression did not include a base option.");

		return probs;
	}
}
