package microsim.dev.statistics.regression;

import java.util.LinkedHashMap;
import java.util.SequencedMap;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

/// Ordered Discrete Variable regression model.
///
/// This compares the score of the target with cut-off values for each possible
/// outcome, and compute probabilities using the given `model`. Outcomes should
/// be given in order of increasing likelihood.
///
/// @param cuts  cut-off values for each outcome, this should be ordered by
///              increasing likelihood.
/// @param score computes the score for the target, see e.g. [LinearFit].
/// @param model computes a probability from a score. See e.g. [Models].
public record OrderedRegression<E, T>(SequencedMap<E, Double> cuts, Function<? super T, Double> score,
        DoubleUnaryOperator model) {

    /// Compute probabilities of all given outcomes.
    public SequencedMap<E, Double> probabilities(T target) {
        var probs = new LinkedHashMap<E, Double>(this.cuts.size());
        var score = this.score.apply(target);
        var probHere = 0.0;
        var probPreceding = 0.0;

        var allButLast = this.cuts.entrySet().stream().limit(this.cuts.size() - 1).toList();

        for (var entry : allButLast) {
            var cutVal = entry.getValue();
            probHere = model.applyAsDouble(cutVal - score);
            if (probHere < probPreceding) {
                throw new RuntimeException(
                        "estimated cuts for ordered regression must be sorted in increasing likelihood");
            }
            probs.put(entry.getKey(), probHere - probPreceding);
            probPreceding = probHere;
        }
        probs.put(cuts.lastEntry().getKey(), 1.0 - probPreceding);

        return probs;
    }
}
