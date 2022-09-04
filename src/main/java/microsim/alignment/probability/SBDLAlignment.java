package microsim.alignment.probability;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import static java.lang.StrictMath.log;

/**
 * @param <T> A class usually representing an agent.
 * @see <a href="https://www.jasss.org/17/1/15.html">Jinjing Li and Cathal O'Donoghue, Evaluating Binary Alignment
 * Methods in Microsimulation Models, Journal of Artificial Societies and Social Simulation 17 (1) 15</a>
 */
public class SBDLAlignment<T> extends AbstractSortByDifferenceAlignment<T> {
    @Override
    double @NotNull [] generateSortingVariable(final double @NotNull [] pArray, final double @NotNull [] rArray) {
        val returnValues = new double[pArray.length];
        for (var i = 0; i < pArray.length; i++)
            returnValues[i] = log(1 / rArray[i] - 1) + log(pArray[i] / (1 - pArray[i]));
        return returnValues;
    }
}
