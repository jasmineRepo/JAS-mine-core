package microsim.alignment.probability;

import lombok.NonNull;
import microsim.alignment.AlignmentUtils;
import microsim.engine.SimulationEngine;
import org.apache.commons.collections4.Predicate;

import java.util.*;

abstract public class AbstractSortByDifferenceAlignment<T> implements AlignmentUtils<T> {
    public void align(Collection<T> agents, Predicate<T> filter, AlignmentProbabilityClosure<T> closure,
                      double targetProbability) {
        if (targetProbability < 0. || targetProbability > 1.)
            throw new IllegalArgumentException("Target probability must lie in [0,1]");

        List<T> list = extractAgentList(agents, filter);

        Map<T, Double> map = new HashMap<>();
        double[] pValues = list.stream().mapToDouble(closure::getProbability).toArray();
        double[] rValues = new double[list.size()];
        for (var i = 0; i < list.size(); i++) rValues[i] = SimulationEngine.getRnd().nextDouble();
        // todo
        //  this generates numbers in [0, 1), not in [0, 1]
        //  see https://stackoverflow.com/questions/3680637/generate-a-random-double-in-a-range
        //  see https://stackoverflow.com/questions/58920019/java-math-random-closed-double-interval
        double[] sortingVariable = generateSortingVariable(pValues, rValues);
        for (var i = 0; i < list.size(); i++) map.put(list.get(i), sortingVariable[i]);
        map = sortByComparator(map, false);

        int i = 0;
        for (T agent : map.keySet()) {
            closure.align(agent, i <= targetProbability * list.size() ? 1.0 : 0.0);
            i++;
        }

    }
    /**
     *
     * Sorting of objects of type T (usually the agents)
     * by an associated Double number. This method is used in
     * the SBD and SBDL alignment algorithms.
     *
     * @param unsortedMap
     * @param ascendingOrder - if true, the method returns a map ordered
     * 	by the Double value increasing, otherwise the map will be ordered
     * 	by the Double value decreasing.
     * @return - the map of type T objects sorted by the Double numbers.
     * Returns a LinkedHashMap, that maintains the order of insertion.
     */
    protected Map<T, Double> sortByComparator(@NonNull Map<T, Double> unsortedMap, final boolean ascendingOrder) {

        List<Map.Entry<T, Double>> list = new LinkedList<>(unsortedMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> o1.getValue().compareTo(ascendingOrder ? o2.getValue() : o1.getValue()));

        // Maintaining insertion order with the help of LinkedList
        Map<T, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<T, Double> entry : list) sortedMap.put(entry.getKey(), entry.getValue());

        return sortedMap;
    }

    abstract double[] generateSortingVariable(double[] p, double[] q);
}
