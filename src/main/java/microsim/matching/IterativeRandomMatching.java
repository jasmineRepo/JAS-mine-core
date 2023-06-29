package microsim.matching;

import lombok.NonNull;
import lombok.val;
import microsim.statistics.regression.RegressionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IterativeRandomMatching<T> extends AbstractMatcher<T> implements IterativeMatchingAlgorithm<T> {

    private static IterativeRandomMatching<?> iterativeRandomMatching;

    private IterativeRandomMatching() {
    }

    public static @NonNull IterativeRandomMatching<?> getInstance() {
        if (iterativeRandomMatching == null)
            iterativeRandomMatching = new IterativeRandomMatching<>();

        return iterativeRandomMatching;
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
            //Now check all agents in c1, because a match does not always occur (if, for example the matching score is infinity or NaN).
            val potentialMatches = new LinkedHashMap<T, Double>();
            for (T candidate : c2) {
                score = doubleClosure.getValue(agent1, candidate);
                if (Double.isFinite(score)) {
                    //Do not add people with scores of infinity or NaN
                    potentialMatches.put(candidate, Math.exp(-score));
                    //Lower scores are more likely.
                    // Note therefore that we shouldn't use a pseudo-normal (i.e. Math.exp(-score*score/2), because that
                    // would be a symmetric distribution, whereas we need one that favours one direction of the score
                    // (i.e. negative - a large negative score is more favoured than a small positive one, unlike in the normal dist.).
                }
            }

            //Randomly sample a partner with a probability associated with the score (lowest scores are more likely)
            if (!potentialMatches.isEmpty()) {        //If no potential partners, move on to the next agent1
                T partner = RegressionUtils.event(potentialMatches, false);
                matching.match(agent1, partner);
                c2.remove(partner);
            } else unmatchedCollection1.add(agent1);
        }

        unmatchedCollection2.addAll(c2);
        return unmatched;
    }
}
