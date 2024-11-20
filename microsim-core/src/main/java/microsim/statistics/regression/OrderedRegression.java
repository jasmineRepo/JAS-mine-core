package microsim.statistics.regression;

import microsim.data.MultiKeyCoefficientMap;
import microsim.statistics.IDoubleSource;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

import static microsim.statistics.regression.RegressionUtils.populateMultinomialCoefficientMap;


/*****************************************************************
 * Ordered Discrete Variable Models
 *
 * Let y define a discrete valued set {y_0,y_1,..., y_n}
 *   Each y_i is an indicator variable for discrete value i, where higher values of i reflect some natural ordering of the set
 * ystar = Xb - e
 * y_j = 1 if cut_j-1 < ystar <= cut_j and 0 otherwise
 * P(y_j=1|X) = P(cut_j-1<ystar).P(ystar<=cut_j) = P(cut_j-1<ystar).(1 - P(cut_j<ystar))
 *  = P(cut_j-1<ystar) - P(cut_j-1<ystar).P(cut_j<ystar) = P(cut_j-1<ystar) - P(cut_j<ystar) = F(Xb-cut_j-1) - F(Xb-cut_j)
 *  NB: P(cut_j-1<ystar).P(cut_j<ystar) = P(cut_j<ystar) because cut_j-1 <= cut_j
 *****************************************************************/
public class OrderedRegression<E1 extends Enum<E1> & IntegerValuedEnum> implements IDiscreteChoiceModel {

    MultiKeyCoefficientMap map;
    private List<E1> eventList;
    ProbabilityCalculator calculator;


    public OrderedRegression(RegressionType type, Class<E1> enumType, MultiKeyCoefficientMap map) {
        this.map = map;
        eventList = Arrays.asList(enumType.getEnumConstants());
        eventList.sort(Comparator.comparingInt(IntegerValuedEnum::getValue));
        if (eventList.size()<3)
            throw new RuntimeException("OrderedRegression requires at least three events");
        calculator = new ProbabilityCalculator(type);
    }

    public double getCoefficient(String regressor) {

        if (map.getKeysNames().length == 1) {

            if (map.getValuesNames().length == 1) {
                return ((Number)(map.getValue(regressor))).doubleValue();
            } else {
                String columnName = RegressionColumnNames.COEFFICIENT.toString();
                return ((Number)(map.getValue(regressor, columnName))).doubleValue();
            }
        } else {
            throw new RuntimeException("attempt to access individual regression coefficients not currently supported for multinomial coefficient maps");
        }
    }

    public <E extends Enum<E> & IntegerValuedEnum, E2 extends Enum<E2>> Map<E,Double> getProbabilities(IDoubleSource iDblSrc, Class<E2> Regressors) {
        // probabilities are obtained for discrete alternatives of dependent variable in increasing order of the feasible set
        // P(y_j|X) = F(Xb-cut_j-1) - F(Xb-cut_j)

        Map<E, Double> probs = new HashMap<>();
        double probHere, probPreceding = 1.0;
        for (int ii = 0; ii < eventList.size()-1; ii++) {

            E event = (E) eventList.get(ii);
            String key = Strings.concat("Cut", Integer.toString(ii+1));
            double val = -getCoefficient(key);
            probHere = calculator.getProbability(map, iDblSrc, Regressors, val);
            if (probHere > probPreceding) {
                throw new RuntimeException("estimated cuts for ordered regression must be increasing in categories");
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
