package microsim.dev.statistics.regression;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

/// Binomial regression.
///
/// This represents a regression for a binary outcome (pass/fail).
///
/// @param passScore computes the score for the "pass" outcome. See e.g.
///                  [LinearFit].
/// @param model     computes a probability from a score. See e.g. [Models].
public record BinomialRegression<T>(Function<? super T, Double> passScore, DoubleUnaryOperator model) {
    /// Compute probability of the given outcome.
    public double probability(T target, boolean outcome) {
        var score = this.passScore.apply(target);
        return model.applyAsDouble(score);
    }

    /// Build a binomial regression equivalent to this one but with
    /// probabilities of "pass" and "fail" events flipped.
    public BinomialRegression<T> flip() {
        return new BinomialRegression<>(this.passScore, s -> 1.0 - this.model.applyAsDouble(s));
    }
}
