package microsim.matching;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.function.Predicate;

/**
 * MATCHING CLASS BASED ON THE ITERATIVE RANDOM MATCHING CLASS
 *
 * Whereas the IterativeRandomMatching class matches units in collection1 to
 * those in collection2 in order of collection1
 * the current class evaluates all potential match combinations between
 * collection1 and collection2 and proceeds to
 * select matches from lowest to highest "score". The routine is consequently
 * agnostic concerning the order of each collection.
 *
 * @param <T> agent type.
 */
public class GlobalMatching<T> {

    public Pair<Set<T>, Set<T>> matching(Collection<T> collection1, Predicate<T> filter1, Collection<T> collection2,
            Predicate<T> filter2, MatchingScoreClosure<T> doubleClosure, MatchingClosure<T> matching) {

        LinkedHashSet<T> unmatchedCollection1 = new LinkedHashSet<T>();
        LinkedHashSet<T> unmatchedCollection2 = new LinkedHashSet<T>();
        Pair<Set<T>, Set<T>> unmatched = new Pair<>(unmatchedCollection1, unmatchedCollection2);

        var c1 = new HashSet<T>();
        if (filter1 != null)
            collection1.stream().filter(filter1).forEachOrdered(c1::add);
        else
            c1.addAll(collection1);

        var c2 = new HashSet<T>();
        if (filter2 != null)
            collection2.stream().filter(filter2).forEachOrdered(c2::add);
        else
            c2.addAll(collection2);

        if (CollectionUtils.intersection(c1, c2).size() > 0)
            throw new IllegalArgumentException("Matching algorithm cannot match not disjuctable collections");

        // evaluate list of global candidate pairs
        var candidates = new ArrayList<GlobalMatchingPair<T>>();
        for (T agent1 : c1) {

            for (T agent2 : c2) {

                Double score = doubleClosure.getValue(agent1, agent2);
                if (Double.isFinite(score)) {

                    var pair = new GlobalMatchingPair<T>(agent1, agent2, score);
                    candidates.add(pair);
                }
            }
        }

        // sort candidate pairs from best to worst candidate matches
        Collections.sort(candidates, new GlobalMatchingPairComparator<T>());

        // allocate matches
        for (int ii = 0; ii < candidates.size(); ii++) {
            var pair = candidates.get(ii);
            var agent1 = pair.getAgent1();
            var agent2 = pair.getAgent2();
            if (c1.contains(agent1) && c2.contains(agent2)) {
                matching.match(agent1, agent2);
                c1.remove(agent1);
                c2.remove(agent2);
            }
        }

        unmatchedCollection1.addAll(c1);
        unmatchedCollection2.addAll(c2);

        return unmatched;
    }

    private GlobalMatching() {

    }

    // FIXME: remove static instance
    private static GlobalMatching<?> globalMatching;

    @SuppressWarnings("rawtypes")
    public static GlobalMatching getInstance() {
        if (globalMatching == null)
            globalMatching = new GlobalMatching<>();

        return globalMatching;
    }
}
