package microsim.statistics.regression;

import microsim.data.MultiKeyCoefficientMap;
import microsim.statistics.IDoubleSource;

import java.util.*;

import static microsim.statistics.regression.RegressionUtils.populateMultinomialCoefficientMap;


/*****************************************************************
 * Multinomial Discrete Variable Models
 *
 * @author Justin van de Ven
 *
 * NOTE: Probit variant not currently supported
 *
 * Let y define a discrete valued set {y_0,y_1,..., y_n}
 *   Each y_i is an indicator variable for discrete value i, where the index i signifies nothing
 * Define P(y_i=1|X) = exp(Xb_i) / sum(exp(Xb_1),...exp(Xb_n)) for all i
 * Identification is permitted by normalising one category, k, such that exp(Xb_k) = 1.0
 *****************************************************************/
public class MultinomialRegression<E1 extends Enum<E1> & IntegerValuedEnum> implements IDiscreteChoiceModel {

    Map<E1, MultiKeyCoefficientMap> maps;
    private List<E1> eventList;
    ProbabilityCalculator calculator;


    public MultinomialRegression(RegressionType type, Class<E1> enumType, MultiKeyCoefficientMap multinomialCoefficients) {
        maps = populateMultinomialCoefficientMap(enumType, multinomialCoefficients);
        eventList = Arrays.asList(enumType.getEnumConstants());
        eventList.sort(Comparator.comparingInt(IntegerValuedEnum::getValue));
        if (eventList.size()<3)
            throw new RuntimeException("GeneralisedOrderedRegression requires at least three events");
        calculator = new ProbabilityCalculator(type);
    }

    public MultinomialRegression(RegressionType type, Class<E1> enumType, Map<E1, MultiKeyCoefficientMap> maps, boolean preProcessedFlag) {
        if (preProcessedFlag)
            this.maps = maps;
        else
            throw new RuntimeException("problem instantiating MultiLogitRegression");
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
        // P(y_i=1|X) = exp(Xb_i) / sum(exp(Xb_1),...exp(Xb_n))

        Map<E,Double> expScores = new HashMap<>();
        Map<E,Double> probs = new LinkedHashMap<>();
        double denominator = 1.0;
        int countEventProbs = 0;
        for (E event : (Set<E>)maps.keySet()) {
            double expScore = Math.exp(calculator.getScore(maps.get(event), iDblSrc, Regressors));
            expScores.put(event, expScore);
            denominator += expScore;
            countEventProbs++;
        }
        if (countEventProbs!=eventList.size()-1)
            throw new RuntimeException("Multinomial regression has been supplied with the wrong number of scores to construct probability");

        for (E event : (List<E>)eventList) {
            Double val = expScores.get(event);
            probs.put(event, Objects.requireNonNullElse(val, 1.0) / denominator);
        }

        return probs;
    }
}
