package microsim.dev.statistics.regression;

import java.util.function.Function;

/// A dependent variable and associated coefficient for regression.
public record DependentVar<T>(Function<? super T, Double> valueFor,
        double coefficient) {

    /// Value of the dependent variable for the given object.
    public Double evaluate(T obj) {
        return this.valueFor.apply(obj);
    }
}
