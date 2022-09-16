package microsim.matching;

import lombok.NonNull;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractMatcher<T> {

    public List<T> filterAgents(final @NonNull Collection<T> collection, final @Nullable Predicate<T> filter) {
        val filteredAgents = new ArrayList<T>();
        if (filter != null) CollectionUtils.select(collection, filter, filteredAgents);
        else filteredAgents.addAll(collection);
        return filteredAgents;
    }

    public void validateDisjointSets(final @NonNull List<T> c1, final @NonNull List<T> c2){
        if (CollectionUtils.intersection(c1, c2).size() > 0)
            throw new IllegalArgumentException("Lists of people for matching must not intersect!");
    }
}
