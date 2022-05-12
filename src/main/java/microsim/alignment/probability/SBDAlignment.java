package microsim.alignment.probability;

/**
 * @param <T> A class usually representing an agent.
 * @see <a href="https://www.jasss.org/17/1/15.html">Jinjing Li and Cathal O'Donoghue, Evaluating Binary Alignment
 * Methods in Microsimulation Models, Journal of Artificial Societies and Social Simulation 17 (1) 15</a>
 */
public class SBDAlignment<T> extends AbstractSortByDifferenceAlignment<T>{
	@Override
	double[] generateSortingVariable(double[] pArray, double[] rArray){
		double[] returnValues = new double[pArray.length];
		for (var i = 0; i < pArray.length; i++)
			returnValues[i] = pArray[i] - rArray[i];
		return returnValues;
	}
}
