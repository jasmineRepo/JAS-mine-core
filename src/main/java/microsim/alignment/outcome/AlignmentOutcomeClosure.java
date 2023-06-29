package microsim.alignment.outcome;

import lombok.NonNull;

public interface AlignmentOutcomeClosure<T> {

    boolean getOutcome(final @NonNull T agent);

    void resample(final @NonNull T agent);

}
