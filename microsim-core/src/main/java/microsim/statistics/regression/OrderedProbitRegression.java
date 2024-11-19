package microsim.statistics.regression;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;
import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.IDoubleSource;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OrderedProbitRegression<T extends Enum<T> & IntegerValuedEnum> extends OrderedCategoricalRegression {

    public OrderedProbitRegression(MultiKeyCoefficientMap map, Class<T> enumType) {
        super(map, enumType, RegressionType.OrderedProbit);
    }

    public OrderedProbitRegression(MultiKeyCoefficientMap map, Class<T> enumType, Random random) {
        super(map, enumType, RegressionType.OrderedProbit, random);
    }
}
