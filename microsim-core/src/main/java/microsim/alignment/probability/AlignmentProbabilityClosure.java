package microsim.alignment.probability;

public interface AlignmentProbabilityClosure<T> {

	/**
	 * Returns the unaligned probability of a 'positive' outcome 
	 * for the agent (the user should define what the positive 
	 * outcome is; it could be that something happens or does 
	 * not happen).
	 * 
	 * @param agent
	 * @return the probability of a 'positive' outcome for the agent
	 */
	double getProbability(T agent);
	
	/**
	 * Method specifying the sampling of the aligned probability to
	 * determine the outcome for the agent.
	 * 
	 * @param agent
	 * @param alignedProbability
	 */
	void align(T agent, double alignedProbability);
	
}
