package microsim.alignment.multi;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import microsim.agent.Weighting;


/**
 * Logit Scaling alignment (as introduced by P. Stephensen in International Journal of Microsimulation (2016) 9(3) 89-102), 
 * for the general case of 'A' choices and agents who implement the Weighting interface, so that the weighting of each agent 
 * represents the number of individuals it represents.  If the agents do not implement the Weighting interface, 
 * use the LogitScalingAlignment class instead.
 * 
 * @author Ross Richardson
 *
 * @param <T>
 */
public class LogitScalingWeightedAlignment<T extends Weighting> extends AbstractMultiProbabilityAlignment<T> {

	/**
	 * 
	 * Aligns a sub-population of objects using Logit Scaling alignment. 
	 * 
	 *  This method sets the default maximum number of iterations as 10000, which should be much more than enough for most purposes 
	 *  (based on information in the original paper).  This method also assumes a default precision of 1.e-10 and enables warnings
	 *  to be sent to the System.out if the alignment target is not successfully reached. 
	 *  If it is desired to use a different maximum number of iterations or allowed error precison, or to turn off warnings, 
	 *  please use the method: 
	 *  align(List<T> agentList, Predicate filter, AlignmentProbabilityClosure<T> closure, double targetShare, int maxNumberIterations, double precision, boolean enableWarnings)
	 *  
	 * @param agentList - list of agents to potentially apply alignment to (will be filtered by the 'filter' Predicate class); 
	 * 	the agent class must implement the Weighting interface by providing a getWeighting() method.  In the case of the alignment algorithm, getWeighting() must return a positive value.
	 * @param filter - filters the agentList so that only the relevant sub-population of agents is sampled
	 * @param closure - AlignmentProbabilityClosure that specifies how to define the (unaligned) probability of the agent and how to implement the result of the aligned probability. 
	 * @param targetShare - the target share of the relevant sub-population (specified as a proportion of the filtered population) for which the mean of the aligned probabilities (defined by the AlignmentProbabilityClosure) must equal
	 * 
	 */
	@Override
	public void align(List<T> agentList, Predicate filter, AlignmentMultiProbabilityClosure<T> closure, double[] targetShare) {
		
		final int maxNumberIterations = 100;		//The maximum number of iterations until the iterative loop in the alignment algorithm terminates.  The resulting probabilities at that time are then used.
		final double precision = 1.e-5;				//The appropriate value here depends on the precision of the probabilities.  If the probabilities are stated to x decimal places, then EPSILON should be 1.e-x.
		
		align(agentList, filter, closure, targetShare, maxNumberIterations, precision, true);		//Set default max number of iterations as 100 (the paper claims convergence is quick, of the order of 10s).  If you want to set manually, use the other align() method.
	}
	
	
	/**
	 * 
	 * Aligns a sub-population of objects using Logit Scaling alignment.
	 * 
	 * @param agentList - list of agents to potentially apply alignment to (will be filtered by the 'filter' Predicate class); 
	 * 	the agent class must implement the Weighting interface by providing a getWeighting() method.  In the case of the alignment algorithm, getWeighting() must return a positive value.
	 * @param filter - filters the agentList so that only the relevant sub-population of agents is sampled
	 * @param closure - AlignmentProbabilityClosure that specifies how to define the (unaligned) probability of the 
	 * 	agent and how to implement the result of the aligned probability. 
	 * @param targetShare - the target share of the relevant sub-population (specified as a proportion of the filtered 
	 * 	population) for which the mean of the aligned probabilities (defined by the AlignmentProbabilityClosure) must equal
	 * @param maxNumberIterations - The maximum number of iterations of the aligned probabilities before termination.  
	 * The probabilities derived will be used as aligned probabilities, although if the maximum number of iterations is reached,
	 *  it is possible that the mean of the probabilities has not converged to the alignment targetShare.
	 * @param precision - the largest accepted difference between two iterations of the average (mean) aligned probability.  
	 * 	When the average aligned probability iteration has converged to a value within this precision, the algorithm will stop 
	 * 	iterating (assuming the number of iterations has not exceeded the maxNumberIterations parameter) and the resultant 
	 * 	probabilities should be considered the true aligned probabilities.
	 * @param enableWarnings - If set to true, warnings will be sent to the System.out if the alignment has not converged within 
	 * 	the desired precision. If set to false, warnings will not be sent to the System.out.
	 * 
	 */
	public void align(List<T> agentList, Predicate filter, AlignmentMultiProbabilityClosure<T> closure, double[] targetShare, int maxNumberIterations, double precision, boolean enableWarnings) {

		int numOptions = targetShare.length;			//The length of the targetShare corresponds to the number of possible choices or outcomes of the event (the 'A' in Stephensen's paper)
		double targetSum = 0.;
		for(int choice = 0; choice < numOptions; choice++) {
			if (targetShare[choice] < 0. || targetShare[choice] > 1.) {
				throw new IllegalArgumentException("Each targetShare element in LogitScalingAlignment.align() method must lie in [0,1]");
			}
			targetSum += targetShare[choice];		//If targetShare[choice] was negative, would have already thrown the exception above, so no need to check here again.
		}
		if (targetSum > 1.) {
			throw new IllegalArgumentException("Sum of targetShare outcomes in LogitScalingAlignment.align() must be less than or equal to 1");
		}
		if (maxNumberIterations < 1) {
			throw new IllegalArgumentException("maxNumberIterations in LogitScalingAlignment.align() method must be at least 1");
		} 
		if (precision <= 0.) {
			throw new IllegalArgumentException("precision in LogitScalingAlignment.align() method must be greater than 0");
		}

		List<T> list = new ArrayList<T>();		
		if (filter != null)
			CollectionUtils.select(agentList, filter, list);
		else
			list.addAll(agentList);
		
		int n = list.size();
		double total = 0.;			//The total weighting, i.e. will sum the weighting of each agent in the sub-population to be aligned.

		//2-dimensional array of probabilities that will be adjusted by iteration.  
		//The goal of the alignment algorithm is for the sum of each column (iterate over the array's first index)
		//to equal the corresponding target, while the sum of each row (iterate over the array's second index) to 
		//equal 1 (i.e. the sum of state probabilities, where one of the states must be true).
		double[][] prob = new double[list.size()][numOptions];		
		
		// compute total expected number of simulated positive outcomes
		for (int i=0; i<n; i++) {
			T agent = list.get(i);
			double weight = agent.getWeighting();
			if(weight <= 0.) {
				throw new IllegalArgumentException("Weighting cannot be zero or negative in ResamplingWeightedAlignment!");
			}
			total += weight;

			for(int choice = 0; choice < numOptions; choice++) {
				prob[i][choice] =  closure.getProbability(agent)[choice] * weight;		//Unaligned probabilities (to be aligned by the procedure).
				if(i == 0) {		//Just do for one agent, so as not to clutter output (all agents have same prob in the test)
					System.out.print(", prob[" + choice + "], " + prob[i][choice]/agent.getWeighting());
				}
			}
		}
		System.out.print('\n');

		double[] target = new double[numOptions];
		for(int choice = 0; choice < numOptions; choice++) {
			target[choice] = targetShare[choice] * total;
		}
		double allowedError = precision * total;		//precision refers to the share of the sub-population aligned, allowedError refers to the target, so is scaled up by the total weighting of the sub-population.
		
		int count = 0;
		double error = Double.MAX_VALUE;
		double[] probSumOverAgents = new double[numOptions];
		double[] previousProbSumOverAgents = new double[numOptions];
		for(int choice = 0; choice < numOptions; choice++) {
			probSumOverAgents[choice] = 0.;
		}
		while( (Math.abs(error) >= allowedError) && (count < maxNumberIterations) ) { 
			
			error = 0.;
			double[] gamma = new double[numOptions];
			
			for(int choice = 0; choice < numOptions; choice++) {
				
				previousProbSumOverAgents[choice] = probSumOverAgents[choice];	//Store previous iteration values (will compare iterations for convergence)	
				probSumOverAgents[choice] = 0.;								//Reset values
				
				for(int agent = 0; agent < n; agent++) {
					probSumOverAgents[choice] += prob[agent][choice];
				}
				gamma[choice] = target[choice] / probSumOverAgents[choice];
				
				probSumOverAgents[choice] = 0.;				//Reset for use in summing final probabilities for test of convergence
			}
			
			for(int agent = 0; agent < n; agent++) {

				double probSumOverChoices = 0;
				for(int choice = 0; choice < numOptions; choice++) {
					
					//Gamma transform of the probabilities
					prob[agent][choice] *= gamma[choice];
					
					probSumOverChoices += prob[agent][choice];
				}
				double alpha = list.get(agent).getWeighting() / probSumOverChoices;			//Because probs are scaled by the agent's weight already, the sum of probs over the possible choices = agent.getWeighting() instead of 1.  Therefore, we need to use the agent's weight in the numerator here.

				//Alpha transform of the probabilities
				for(int choice = 0; choice < numOptions; choice++) {
					prob[agent][choice] *= alpha;
					
					probSumOverAgents[choice] += prob[agent][choice];
				}
				
			}
			
			for(int choice = 0; choice < numOptions; choice++) {
				error += Math.abs(probSumOverAgents[choice] - previousProbSumOverAgents[choice]);
			}
			error /= (double)numOptions;
			System.out.print("count, " + count + ", precision, " + error/(double)total);
			for(int i = 0; i < numOptions; i++) {
				System.out.print(", prob[" + i + "], " + prob[0][i] / list.get(0).getWeighting());
			}
			System.out.print('\n');
			count++;
		}
		
		if( (error >= allowedError) && enableWarnings) {
			System.out.println("WARNING: The LogitScalingWeightedAlignment.align() method terminated with an error of " 
					+ (error/(double)total) + ", which has a greater magnitude than the precision bounds of +/-" + precision + ".  The number "
					+ "of iterations was  " + count + ".  Check the results of the Logit Scaling Weighted alignment to ensure that "
					+ "alignment is good enough for the purpose in question, or consider increasing the maximum number of iterations "
					+ "or the precision!");
		}

		// Correct individual probabilities with the aligned probabilities, prob[i]
		for (int i=0; i<n; i++) {
			T agent = list.get(i);
			for(int choice = 0; choice < numOptions; choice++) {
				prob[i][choice] /= agent.getWeighting();			//As prob was previously scaled up by weight, need to scale down here to 'renormalise'.
			}
			closure.align(agent, prob[i]);			
		}

	}	

}
