package microsim.dev.statistics.regression;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.math3.util.FastMath;

/// Multinomial regression.
///
/// This represents a regression for a multinomial outcome. This uses an
/// exponential weighting of scores to compute the probability of
/// each outcome (i.e. uses a logit model).
///
/// If `refEvent` is non-empty, it is given a score of 0.0 (i.e. a weight of
/// 1.0). This is useful when the logit model is "identified", i.e. arbitrarily
/// normalised so that only one set of scores gives the same outcome
/// probabilities.
///
/// @param scores   how to compute the score for each possible outcome. See
///                 e.g. [LinearFit].
/// @param refEvent an outcome used for "identification".
public record MultinomialRegression<E, T>(Map<E, ? extends Function<? super T, Double>> scores, Optional<E> refEvent) {
    /// Compute probabilities of all given outcomes.
    public Map<E, Double> probabilities(T target) {
        var probs = new HashMap<E, Double>(this.scores.size());

        this.refEvent.ifPresent(outcome -> {
            if (this.scores.containsKey(outcome)) {
                throw new IllegalArgumentException("refEvent is also in scores");
            }
            probs.put(outcome, 1.0);
        });
        var sumExpScore = refEvent.isPresent() ? 1.0 : 0.0;

        for (var entry : this.scores.entrySet()) {
            var outcome = entry.getKey();
            var score = this.scores.get(outcome).apply(target);
            var expScore = FastMath.exp(score);
            probs.put(outcome, expScore);
            sumExpScore += expScore;
        }

        for (var entry : probs.entrySet()) {
            var normalisedScore = entry.getValue() / sumExpScore;
            entry.setValue(normalisedScore);
        }

        return probs;
    }
}
