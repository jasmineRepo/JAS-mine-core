package microsim.dev.statistics.regression;

import org.apache.commons.math3.analysis.function.Sigmoid;
import org.apache.commons.math3.distribution.NormalDistribution;

/// Collection of models to pass as
public class Models {
    private static Sigmoid sigmoid = new Sigmoid();
    private static NormalDistribution normal = new NormalDistribution(null, 0.0, 1.0);

    /// Probability of the given `score` with a logit model.
    public static double logit(double score) {
        return Models.sigmoid.value(score);
    }

    /// Probability of the given `score` with a probit model.
    public static double probit(double score) {
        return Models.normal.cumulativeProbability(score);
    }
}
