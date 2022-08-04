package microsim.alignment;

import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface AlignmentUtils<T> {
    /**
     * Sorts out {@code agents} according to the filter requirements.
     *
     * @param agents An unsorted list of agents.
     * @param filter Null, or a predicate, one for all agents - to filter some of them out.
     * @return A filtered list of agents.
     */
    @NotNull
    default List<T> extractAgentList(final @NotNull Collection<T> agents,
                                     final @Nullable Predicate<T> filter) {
        val list = new ArrayList<T>();
        if (filter != null) CollectionUtils.select(agents, filter, list);
        else list.addAll(agents);
        return list;
    }
}
