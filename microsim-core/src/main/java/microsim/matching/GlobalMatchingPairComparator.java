package microsim.matching;

import java.util.Comparator;

/**
 * HELPER CLASS TO ASSIST SORTING OF GlobalMatchingPairs
 */
public class GlobalMatchingPairComparator implements Comparator<GlobalMatchingPair> {

    public int compare(GlobalMatchingPair o1, GlobalMatchingPair o2) {
        double val = (o1.getScore() - o2.getScore()) * 1000.0;
        return (int)val;
    }
}
