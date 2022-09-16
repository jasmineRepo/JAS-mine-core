package microsim.statistics.regression;

import lombok.NonNull;
import microsim.statistics.DoubleSource;
import microsim.statistics.ObjectSource;

import java.util.Map;

public interface LinReg {

    double getScore(final @NonNull Object individual);

    double getScore(final @NonNull Map<String, Double> values);

    <T extends Enum<T>> double getScore(final @NonNull DoubleSource iDblSrc, final @NonNull Class<T> enumType);

    <T extends Enum<T>, U extends Enum<U>> double getScore(final @NonNull DoubleSource iDblSrc,
                                                           final @NonNull Class<T> enumTypeDouble,
                                                           final @NonNull ObjectSource iObjSrc,
                                                           final @NonNull Class<U> enumTypeObject);
}
