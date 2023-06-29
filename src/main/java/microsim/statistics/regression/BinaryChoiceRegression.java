package microsim.statistics.regression;

import lombok.NonNull;

import java.util.Map;

public interface BinaryChoiceRegression extends LinReg {

    boolean event(final @NonNull Object individual);

    boolean event(final @NonNull Map<String, Double> values);
}
