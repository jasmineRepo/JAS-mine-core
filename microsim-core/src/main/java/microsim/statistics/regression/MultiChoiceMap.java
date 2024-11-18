package microsim.statistics.regression;

import java.util.*;

import microsim.data.MultiKeyCoefficientMap;
import microsim.engine.SimulationEngine;
import microsim.statistics.IDoubleSource;
import org.apache.commons.collections4.keyvalue.MultiKey;

public class MultiChoiceMap<T extends IntegerValuedEnum> {

    private Class<T> enumType = null;
    private List<T> enumList = null;
    private Map<T, MultiKeyCoefficientMap> maps = null;


    public MultiChoiceMap(Class<T> enumType, Map<T, MultiKeyCoefficientMap> maps) {
        this.enumType = enumType;
        this.maps = maps;

        // validate input
        int count = 0;
        Set<String> covariateNames = new HashSet<String>();
        for (T event : maps.keySet()) {
            Set<Object> covariateSet = (maps.get(event)).keySet();
            for (Object covariate : covariateSet) {
                if (count == 0) {
                    covariateNames.add(covariate.toString());
                } else {
                    if (!covariateNames.contains(covariate.toString()) || covariateNames.size() != covariateSet.size()) {
                        throw new RuntimeException("The covariates specified for each outcome of type T in the MultiLogitRegression object do not match");
                    }
                }
            }
            count++;
        }
    }

    public void setEnumList() {

        // obtain ordered list
        enumList = Arrays.asList(enumType.getEnumConstants());
        enumList.sort(new Comparator<T>() {
            @Override
            public int compare(final T o1, final T o2) {
                return o1.getValue() - o2.getValue();
            }
        });
    }
    public Map<T, MultiKeyCoefficientMap> getMaps() {return maps;}
    public Class<T> getEnumType() {return enumType;}
    public List<T> getEnumList() {return enumList;}

    public <E extends Enum<E>> Map<T,Double> getScores(IDoubleSource iDblSrc, Class<E> Regressors) {

        Map<T,Double> map = new HashMap<>();
        for (T event : maps.keySet()) {
            double score = getScore(event, iDblSrc, Regressors);
            map.put(event, score);
        }
        return map;
    }

    public <E extends Enum<E>> double getScore(T event, IDoubleSource iDblSrc, Class<E> Regressors) {

        MultiKeyCoefficientMap map = maps.get(event);
        double score;
        if(map.getKeysNames().length == 1) {
            score = LinearRegression.computeScore(map, iDblSrc, Regressors, true);            //No additional conditioning regression keys used, so no need to check for them
        }
        else {
            score = LinearRegression.computeScore(map, iDblSrc, Regressors);        //Additional conditioning regression keys used (map has more than one key in the multiKey, so need to use reflection (perhaps slow) in order to extract the underlying agents' properties e.g. gender or civil status, in order to determine the relevant regression co-efficients.  If time is critical, consider making the underlying agent (the IDoubleSource) also implement the IObjectSource interface, which uses a faster method to retrieve information about the agent instead of reflection.
        }
        return score;
    }
}