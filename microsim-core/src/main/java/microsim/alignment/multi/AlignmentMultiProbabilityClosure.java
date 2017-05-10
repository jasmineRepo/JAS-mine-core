package microsim.alignment.multi;

public interface AlignmentMultiProbabilityClosure<T> {

	double[] getProbability(T agent);
	
	void align(T agent, double[] alignedProbability);
	
}