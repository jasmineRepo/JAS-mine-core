package microsim.alignment.probability;

import lombok.NonNull;
import lombok.val;

import static java.lang.StrictMath.log;

/**
 * @param <T> A class usually representing an agent.
 * @see <a href="https://www.jasss.org/17/1/15.html">Jinjing Li and Cathal O'Donoghue, Evaluating Binary Alignment
 * Methods in Microsimulation Models, Journal of Artificial Societies and Social Simulation 17 (1) 15</a>
 */
public class SBDLAlignment<T> extends AbstractSortByDifferenceAlignment<T> {
    @Override
    double @NonNull [] generateSortingVariable(final double @NonNull [] pArray, final double @NonNull [] rArray) {
        val returnValues = new double[pArray.length];
        for (var i = 0; i < pArray.length; i++)
            returnValues[i] = log(1 / rArray[i] - 1) + log(pArray[i] / (1 - pArray[i]));
        return returnValues;
    }
}
