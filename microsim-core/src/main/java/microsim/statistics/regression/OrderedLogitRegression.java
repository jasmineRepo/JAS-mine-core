package microsim.statistics.regression;

import microsim.data.MultiKeyCoefficientMap;
import java.util.Random;

public class OrderedLogitRegression<T extends Enum<T>> extends OrderedCategoricalRegression {

    public OrderedLogitRegression(MultiKeyCoefficientMap map, Class<T> enumType) {
        super(map, enumType, RegressionType.OrderedLogit);
    }

    public OrderedLogitRegression(MultiKeyCoefficientMap map, Class<T> enumType, Random random) {
        super(map, enumType, RegressionType.OrderedLogit, random);
    }
}
