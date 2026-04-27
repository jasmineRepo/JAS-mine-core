package microsim.statistics.weighted;

import microsim.statistics.IUpdatableSource;
import microsim.statistics.TimeChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import microsim.agent.Weight;
import microsim.event.CommonEventType;
import microsim.event.EventListener;

/// A weighted cross section is a collection of values each of them representing
/// the status of a given variable of a weighted element of a collection
/// of agents.
public class WeightedCrossSection<A extends Weight, T>
        implements EventListener, IUpdatableSource, IWeightedArraySource<T> {
    private final ArrayList<A> source;
    private final Function<A, T> getObservable;
    private final Optional<Predicate<A>> filter;
    private final TimeChecker timeChecker;

    private List<A> filteredSource;
    private List<T> values;
    private List<Double> weights;

    public WeightedCrossSection(ArrayList<A> source, Function<A, T> getObservable, Predicate<A> filter) {
        this.source = source;
        this.getObservable = getObservable;
        this.filter = Optional.of(filter);
        this.timeChecker = new TimeChecker();
    }

    public WeightedCrossSection(ArrayList<A> source, Function<A, T> getObservable) {
        this.source = source;
        this.getObservable = getObservable;
        this.filter = Optional.empty();
        this.timeChecker = new TimeChecker();
    }

    private Stream<A> sourceStream() {
        var stream = this.source.stream();
        return this.filter.map(p -> stream.filter(p)).orElse(stream);
    }

    public void updateSource() {
        if (this.timeChecker.isUpToDate())
            return;

        this.filteredSource = this.sourceStream().collect(Collectors.toUnmodifiableList());
        this.values = this.filteredSource.stream().map(this.getObservable).collect(Collectors.toUnmodifiableList());
        this.weights = this.filteredSource.stream().map(Weight::getWeight).collect(Collectors.toUnmodifiableList());
    }

    /// ISimEventListener callback function. It supports only
    /// jas.engine.Sim.EVENT_UPDATE event.
    ///
    /// @throws UnsupportedOperationException If actionType is not supported.
    public void onEvent(Enum<?> type) {
        if (type.equals(CommonEventType.Update))
            this.updateSource();
        else
            throw new UnsupportedOperationException(
                    "The SimpleStatistics object does not support " + type + " operation.");
    }

    public List<T> getValues() {
        return this.values;
    }

    public List<Double> getWeights() {
        return this.weights;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("CrossSection.Double [");
        int size = this.values.size() - 1;
        for (int i = 0; i < size; i++) {
            buf.append(this.values.get(i) + " ");
        }
        buf.append(this.values.get(size) + "]; weights [");
        for (int i = 0; i < size; i++) {
            buf.append(this.weights.get(i) + " ");
        }
        buf.append(this.weights.get(size) + "]");
        return buf.toString();
    }

    /// Return the current status of the time checker. A time checker avoid the
    /// object to update more than one time per simulation step. The default value is
    /// enabled (true).
    ///
    /// @return True if the computer is currently checking time before update cached
    ///         data, false if disabled.
    public boolean isCheckingTime() {
        return this.timeChecker.isEnabled();
    }

    /// Set the current status of the time checker. A time checker avoid the object
    /// to update more than one time per simulation step. The default value is
    /// enabled (true).
    ///
    /// @param b True if the computer is currently checking time before update cached
    ///          data, false if disabled.
    public void setCheckingTime(boolean b) {
        this.timeChecker.setEnabled(b);
    }
}
