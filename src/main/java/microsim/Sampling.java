package microsim;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;

/// Collection of functions to perform random sampling.
public class Sampling {
    /// Weighted sampling of values. The sampled values are _not_ unique.
    ///
    /// Note that if all weights are zero, this returns an empty list.
    ///
    /// @param rng      the random generator to use for sampling.
    /// @param values   the list of values to sample.
    /// @param weight   compute sampling weight for values.
    /// @param nsamples number of samples.
    /// @return the list of samples.
    public static <T> List<T> weighted(RandomGenerator rng, List<T> values, Function<T, Double> weight, int nsamples) {
        var indices = IntStream.range(0, values.size()).toArray();
        var weights = values.stream().map(weight).toList();
        weights.forEach(w -> {
            if (w < 0) {
                throw new RuntimeException("weights should be >=0");
            }
        });

        var totWeight = weights.stream().mapToDouble(Double::doubleValue).sum();
        if (totWeight == 0.0) {
            return List.of();
        }

        var probabilities = weights.stream().mapToDouble(w -> w / totWeight).toArray();
        var dist = new EnumeratedIntegerDistribution(rng, indices, probabilities);
        var i_samples = dist.sample(nsamples);
        return Arrays.stream(i_samples).mapToObj(i -> values.get(i)).toList();
    }
}
