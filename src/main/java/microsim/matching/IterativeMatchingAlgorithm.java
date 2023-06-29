package microsim.matching;

import lombok.NonNull;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.math3.util.Pair;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

public interface IterativeMatchingAlgorithm<T> {

    Pair<Set<T>, Set<T>> matching(final @NonNull Collection<T> collection1, final @NonNull Predicate<T> filter1,
                                  final @NonNull Comparator<T> comparator1, final @NonNull Collection<T> collection2,
                                  final @NonNull Predicate<T> filter2,
                                  final @NonNull MatchingScoreClosure<T> doubleClosure,
                                  final @NonNull MatchingClosure<T> matching);

}
