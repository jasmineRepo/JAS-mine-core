package microsim.statistics.regression;

import microsim.data.MultiKeyCoefficientMap;
import microsim.statistics.IDoubleSource;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.HashMap;
import java.util.Map;


/*****************************************************************
 * Manages calculation of probabilities for dichotomous choice models
 * See McFadden, D. (1984), "Econometric analysis of qualitative choice models". In Z. Griliches and M.D. Intriligator, Handbook of Econometrics
 *
 * ystar = Xb - e  (can deduct e because assume e is symmetric)
 * y = 1 if (ystar>=0), and 0 otherwise
 * P(y=1|X) = P(ystar>=0|X) = P(Xb-e>=0) = F(Xb)
 * Probit: e follows standard normal distribution;  F(Xb) = CDF of standard normal distribution
 * Logit: e follows logistic distribution;          F(Xb) = 1 / (1 + exp(-Xb))
 *****************************************************************/
public class ProbabilityCalculator {

    RegressionType type;
    NormalDistribution normalDistribution = new NormalDistribution();

    public ProbabilityCalculator(RegressionType type) {
        this.type = type;
    }

    public <E extends Enum<E>> double getScore(MultiKeyCoefficientMap map, IDoubleSource iDblSrc, Class<E> Regressors) {
        // Xb

        double score;
        if (map.getKeysNames().length == 1)
            score = LinearRegression.computeScore(map, iDblSrc, Regressors, true);            //No additional conditioning regression keys used, so no need to check for them
        else
            score = LinearRegression.computeScore(map, iDblSrc, Regressors);        //Additional conditioning regression keys used (map has more than one key in the multiKey, so need to use reflection (perhaps slow) in order to extract the underlying agents' properties e.g. gender or civil status, in order to determine the relevant regression co-efficients.  If time is critical, consider making the underlying agent (the IDoubleSource) also implement the IObjectSource interface, which uses a faster method to retrieve information about the agent instead of reflection.

        return score;
    }

    public double getProbability(double score) {
        double probability;
        if (type.getValue()==0)
            probability = 1.0 / (1.0 + Math.exp(-score));
        else
            probability = normalDistribution.cumulativeProbability(score);
        return probability;
    }

    public <E extends Enum<E>> double getProbability(MultiKeyCoefficientMap map, IDoubleSource iDblSrc, Class<E> Regressors) {

        return getProbability(map, iDblSrc, Regressors, 0.0);
    }
    public <E extends Enum<E>> double getProbability(MultiKeyCoefficientMap map, IDoubleSource iDblSrc, Class<E> Regressors, double adjust) {
        // P(y=1|X) = P(e<=XB) = F(XB)

        double score = getScore(map, iDblSrc, Regressors) + adjust;
        return getProbability(score);
    }
}
