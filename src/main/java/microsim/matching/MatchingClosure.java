package microsim.matching;

import lombok.NonNull;

public interface MatchingClosure<T> {

    void match(final @NonNull T t1, final @NonNull T t2);

}
