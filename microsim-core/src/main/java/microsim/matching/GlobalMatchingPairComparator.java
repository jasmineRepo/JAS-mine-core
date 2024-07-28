package microsim.matching;

import java.util.Comparator;

/**
 * HELPER CLASS TO ASSIST SORTING OF GlobalMatchingPairs
 */
public class GlobalMatchingPairComparator implements Comparator<GlobalMatchingPair> {

    @Override
    public int compare(GlobalMatchingPair o1, GlobalMatchingPair o2) {
        return Double.compare(o1.getScore(), o2.getScore());
    }
}
