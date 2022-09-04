package microsim.alignment.probability;

import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

/**
 * @param <T> A class usually representing an agent.
 * @see <a href="https://www.jasss.org/17/1/15.html">Jinjing Li and Cathal O'Donoghue, Evaluating Binary Alignment
 * Methods in Microsimulation Models, Journal of Artificial Societies and Social Simulation 17 (1) 15</a>
 */
public class SBDAlignment<T> extends AbstractSortByDifferenceAlignment<T> {

    /**
     * {@inheritDoc}
     * @implSpec {@code Q_i = P_i - R_i}.
     */
    @Override
    double @NotNull [] generateSortingVariable(final double @NotNull [] pArray, final double @NotNull [] rArray) {
        return IntStream.range(0, pArray.length).mapToDouble(i -> pArray[i] - rArray[i]).toArray();
    }
}
