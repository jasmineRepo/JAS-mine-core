package microsim.alignment.probability;

public interface AlignmentProbabilityClosure<T> {

	double getProbability(T agent);
	
	void align(T agent, double alignedProbability);
	
}
