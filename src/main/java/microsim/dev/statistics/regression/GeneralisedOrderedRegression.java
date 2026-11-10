package microsim.dev.statistics.regression;

import java.util.LinkedHashMap;
import java.util.SequencedMap;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

/// Generalised Ordered Discrete Variable regression model.
///
/// Outcomes should be given in order of increasing likelihood.
///
/// @param scores compute the score of each outcome for the target, see e.g.
///               [LinearFit]. Outcomes should be ordered by increasing
///               likelihood.
/// @param model  computes a probability from a score. See e.g. [Models].
public record GeneralisedOrderedRegression<E, T>(SequencedMap<E, ? extends Function<? super T, Double>> scores,
        DoubleUnaryOperator model) {

    /// Compute probabilities of all given outcomes.
    public SequencedMap<E, Double> probabilities(T target) {
        var probs = new LinkedHashMap<E, Double>(this.scores.size());
        var probHere = 1.0;
        var probPreceding = 1.0;

        var allButLast = this.scores.entrySet().stream().limit(this.scores.size() - 1).toList();

        for (var entry : allButLast) {
            var event = entry.getKey();
            var score = entry.getValue().apply(target);
            probHere = model.applyAsDouble(score);
            if (probHere > probPreceding) {
                // FIXME: shouldn't we error out?
                probs.put(event, -1.0);
            } else {
                probs.put(event, probPreceding - probHere);
                probPreceding = probHere;
            }
        }
        probs.put(scores.lastEntry().getKey(), probPreceding);

        return probs;
    }
}
