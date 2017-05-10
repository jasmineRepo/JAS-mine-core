package microsim.alignment.multiple;

public interface AlignmentMultiProbabilityClosure<T> {

	double[] getProbability(T agent);
	
	void align(T agent, double[] alignedProbability);
	
}