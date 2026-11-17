package microsim.dev.statistics.regression;

import java.util.List;
import java.util.function.Function;

/// Linear fit score over the given set of dependent variables.
public record LinearFit<T>(List<DependentVar<T>> vars, double shift) implements Function<T, Double> {
    @Override
    public Double apply(T value) {
        return this.vars.stream()
                .mapToDouble(var -> var.evaluate(value) * var.coefficient())
                .sum() + this.shift;
    }
}
