package microsim.statistics.regression;

import microsim.data.MultiKeyCoefficientMap;
import microsim.statistics.IDoubleSource;

import java.util.*;

import static microsim.statistics.regression.RegressionUtils.populateMultinomialCoefficientMap;


/*****************************************************************
 * Generalised Ordered Discrete Variable Models
 *
 * @author Justin van de Ven
 *
 * Let y define a discrete valued set {y_0,y_1,..., y_n}
 *   Each y_i is an indicator variable for discrete value i, where higher values of i reflect some natural ordering of the set
 * Define yhat_j = sum(y_j+1,...,y_n)
 * yhatstar_j = Xb_j - e_j
 * yhat_j = 1 if yhatstar_j>=0 and 0 otherwise
 * P(yhat_j=1|X) = P(yhatstar_j>=0|X) = P(Xb_j-e_j>=0) = F(Xb_j)
 *****************************************************************/
public class GeneralisedOrderedRegression<E1 extends Enum<E1> & IntegerValuedEnum> implements IDiscreteChoiceModel {

    Map<E1, MultiKeyCoefficientMap> maps;
    private List<E1> eventList;
    ProbabilityCalculator calculator;


    public GeneralisedOrderedRegression(RegressionType type, Class<E1> enumType, MultiKeyCoefficientMap multinomialCoefficients) {
        maps = populateMultinomialCoefficientMap(enumType, multinomialCoefficients);
        eventList = Arrays.asList(enumType.getEnumConstants());
        eventList.sort(Comparator.comparingInt(IntegerValuedEnum::getValue));
        if (eventList.size()<3)
            throw new RuntimeException("GeneralisedOrderedRegression requires at least three events");
        calculator = new ProbabilityCalculator(type);
    }


    public List<E1> getEventList() {return eventList;}

    public <E extends Enum<E> & IntegerValuedEnum, E2 extends Enum<E2>> double getProbability(E event, IDoubleSource iDblSrc, Class<E2> Regressors) {
        return getProbabilities(iDblSrc, Regressors).get(event);
    }

    public <E extends Enum<E> & IntegerValuedEnum, E2 extends Enum<E2>> Map<E,Double> getProbabilities(IDoubleSource iDblSrc, Class<E2> Regressors) {
        // probabilities are obtained for discrete alternatives of dependent variable in increasing order of the feasible set
        // P(y_j) = P(yhat_j-1|X) - P(yhat_j|X)

        Map<E, Double> probs = new HashMap<>();
        double probHere, probPreceding = 1.0;
        for (int ii = 0; ii < eventList.size()-1; ii++) {

            E event = (E) eventList.get(ii);
            probHere = calculator.getProbability(maps.get(event), iDblSrc, Regressors);
            if (probHere > probPreceding) {
                probs.put(event, -1.0);
            } else {
                double prob = probPreceding - probHere;
                probs.put(event, prob);
                probPreceding = probHere;
            }
        }
        probs.put((E) eventList.get(eventList.size()-1), probPreceding);

        return probs;
    }
}
