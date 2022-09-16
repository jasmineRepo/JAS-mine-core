package microsim.matching;

import lombok.NonNull;
import lombok.val;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IterativeSimpleMatching<T> extends AbstractMatcher<T> implements IterativeMatchingAlgorithm<T> {

    private static IterativeSimpleMatching<?> iterativeMatching;

    private IterativeSimpleMatching() {
    }

    public static @NonNull IterativeSimpleMatching<?> getInstance() {
        if (iterativeMatching == null)
            iterativeMatching = new IterativeSimpleMatching<>();

        return iterativeMatching;
    }

    public Pair<Set<T>, Set<T>> matching(final @NonNull Collection<T> collection1, final @Nullable Predicate<T> filter1,
                                         final @Nullable Comparator<T> comparator,
                                         final @NonNull Collection<T> collection2, final @Nullable Predicate<T> filter2,
                                         final @NonNull MatchingScoreClosure<T> doubleClosure,
                                         final @NonNull MatchingClosure<T> matching) {
        val unmatchedCollection1 = new LinkedHashSet<T>();
        val unmatchedCollection2 = new LinkedHashSet<T>();
        val unmatched = new Pair<Set<T>, Set<T>>(unmatchedCollection1, unmatchedCollection2);

        val c1 = filterAgents(collection1, filter1);
        val c2 = filterAgents(collection2, filter2);
        validateDisjointSets(c1, c2);

        double score;
        for (T agent1 : c1) {
            val listToSort = new ArrayList<Pair<Double, T>>();
            for (T candidate : c2) {
                score = doubleClosure.getValue(agent1, candidate);
                if (Double.isFinite(score)) listToSort.add(new Pair<>(score, candidate));
            }

            //List in ascending order of score
            listToSort.sort((pair1, pair2) -> (int) Math.signum(pair1.getFirst() - pair2.getFirst()));

            if (!listToSort.isEmpty()) {
                T partner = listToSort.get(0).getSecond();
                matching.match(agent1, partner);
                c2.remove(partner);
            } else unmatchedCollection1.add(agent1);

        }

        unmatchedCollection2.addAll(c2);
        return unmatched;
    }
}
