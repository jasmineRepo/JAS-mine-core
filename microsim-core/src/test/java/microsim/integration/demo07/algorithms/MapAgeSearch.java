package microsim.integration.demo07.algorithms;

import microsim.data.MultiKeyCoefficientMap;
import microsim.integration.demo07.model.enums.Gender;

import org.apache.commons.collections4.keyvalue.MultiKey;

public class MapAgeSearch {

    public static Double getValue(MultiKeyCoefficientMap map, int age, Gender gender, Integer index) {
        for (var iterator = map.mapIterator(); iterator.hasNext();) {
            iterator.next();
            var mk = (MultiKey<?>) iterator.getKey();
            var ageFrom = (int) mk.getKey(0);
            var ageTo = (int) mk.getKey(1);
            var g = (String) mk.getKey(2);

            if (age >= ageFrom && age <= ageTo && g.equalsIgnoreCase(gender.toString()))
                return ((Number) map.getValue(ageFrom, ageTo, g, index)).doubleValue();
        }

        throw new IllegalArgumentException("Age " + age + " cannot be mapped for gender " + gender);
    }

    public static Double getValue(MultiKeyCoefficientMap map, int age, Gender gender, String stringIndex) {
        for (var iterator = map.mapIterator(); iterator.hasNext();) {
            iterator.next();
            var mk = (MultiKey<?>) iterator.getKey();
            var ageFrom = (int) mk.getKey(0);
            var ageTo = (int) mk.getKey(1);
            var g = (String) mk.getKey(2);

            if (age >= ageFrom && age <= ageTo && g.equalsIgnoreCase(gender.toString()))
                return ((Number) map.getValue(ageFrom, ageTo, g, stringIndex)).doubleValue();
        }

        throw new IllegalArgumentException("Age " + age + " cannot be mapped for gender " + gender);
    }
}
