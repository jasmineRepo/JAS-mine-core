package microsim.statistics.weighted;

import java.util.List;

/// An object that can compute an array of values and their corresponding weights.
public interface IWeightedArraySource<T> {
    /// Return the values.
    ///
    /// @return the list of values.
    public List<T> getValues();

    /// Return the weights.
    ///
    /// This should have the same length as [IWeightedArraySource#getValues()].
    ///
    /// @return the list of weights.
    public List<Double> getWeights();
}
