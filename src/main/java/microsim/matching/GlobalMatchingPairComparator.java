package microsim.matching;

import java.util.Comparator;

/**
 * HELPER CLASS TO ASSIST SORTING OF GlobalMatchingPairs
 */
public class GlobalMatchingPairComparator<T> implements Comparator<GlobalMatchingPair<T>> {

    @Override
    public int compare(GlobalMatchingPair<T> o1, GlobalMatchingPair<T> o2) {
        return Double.compare(o1.getScore(), o2.getScore());
    }
}
