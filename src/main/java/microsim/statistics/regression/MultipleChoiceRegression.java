package microsim.statistics.regression;

import lombok.NonNull;

import java.util.Map;

public interface MultipleChoiceRegression<T extends Enum<T>> {

    T eventType(final @NonNull Object individual);

    T eventType(final @NonNull Map<String, Double> values);
}
