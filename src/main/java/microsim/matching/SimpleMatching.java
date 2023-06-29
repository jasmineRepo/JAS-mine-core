package microsim.matching;

import lombok.NonNull;
import lombok.val;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class SimpleMatching<T> extends AbstractMatcher<T> implements MatchingAlgorithm<T> {

    private static SimpleMatching<?> simpleMatching;

    private SimpleMatching() {
    }

    public static SimpleMatching<?> getInstance() {
        if (simpleMatching == null)
            simpleMatching = new SimpleMatching<>();

        return simpleMatching;
    }

    public void matching(final @NonNull Collection<T> collection1, final @Nullable Predicate<T> filter1,
                         final @Nullable Comparator<T> comparator,
                         final @NonNull Collection<T> collection2, final @Nullable Predicate<T> filter2,
                         final @NonNull MatchingScoreClosure<T> doubleClosure,
                         final @NonNull MatchingClosure<T> matching) {
        val c1 = filterAgents(collection1, filter1);
        val c2 = filterAgents(collection2, filter2);
        validateDisjointSets(c1, c2);

        long numberMatchesMade = 0L;

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
                numberMatchesMade++;
            }
        }

        if (numberMatchesMade == 0) {
            throw new IllegalArgumentException("Error - no matches have occurred, check the arguments of the matching method!");
        }
    }
}
