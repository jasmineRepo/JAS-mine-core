package microsim.alignment.probability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import microsim.agent.Weight;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;


/**
 * Logit Scaling alignment (as introduced by P. Stephensen in International Journal of Microsimulation (2016) 9(3) 89-102), 
 * but for agents with binary choices rather than the general case of 'A' choices and agents who implement the Weight
 * interface, so that the weight of each agent represents the number of individuals it represents.  For use with multiple 
 * choices, use the LogitScalingWeightedAlignment class instead.  If the agents do not implement the Weight interface, 
 * use the LogitScalingBinaryAlignment class instead.
 * 
 * @author Ross Richardson
 *
 * @param <T>
 */
public class LogitScalingBinaryWeightedAlignment<T extends Weight> extends AbstractProbabilityAlignment<T> {

	/**
	 * 
	 * Aligns a sub-population of objects using Logit Scaling alignment. 
	 * 
	 *  This method sets the default maximum number of iterations as 100, which should be much more than enough for most purposes 
	 *  (based on information in the original paper).  This method also assumes a default precision of 1.e-5 and enables warnings
	 *  to be sent to the System.out if the alignment target is not successfully reached. 
	 *  If it is desired to use a different maximum number of iterations or allowed error precison, or to turn off warnings, 
	 *  please use the method: 
	 *  align(List<T> agentList, Predicate filter, AlignmentProbabilityClosure<T> closure, double targetShare, int maxNumberIterations, double precision, boolean enableWarnings)
	 *  
	 * @param agents - list of agents to potentially apply alignment to (will be filtered by the 'filter' Predicate class); 
	 * 	the agent class must implement the Weight interface by providing a getWeight() method.  In the case of the alignment algorithm, getWeight() must return a positive value.
	 * @param filter - filters the agentList so that only the relevant sub-population of agents is sampled
	 * @param closure - AlignmentProbabilityClosure that specifies how to define the (unaligned) probability of the agent and how to implement the result of the aligned probability. 
	 * @param targetShare - the target share of the relevant sub-population (specified as a proportion of the filtered population) for which the mean of the aligned probabilities (defined by the AlignmentProbabilityClosure) must equal
	 * 
	 */
	@Override
	public void align(Collection<T> agents, Predicate<T> filter, AlignmentProbabilityClosure<T> closure, double targetShare) {
		
		final int maxNumberIterations = 100;		//The maximum number of iterations until the iterative loop in the alignment algorithm terminates.  The resulting probabilities at that time are then used.
		final double precision = 1.e-5;				//The appropriate value here depends on the precision of the probabilities.  If the probabilities are stated to x decimal places, then EPSILON should be 1.e-x.
		
		align(agents, filter, closure, targetShare, maxNumberIterations, precision, true);		//Set default max number of iterations as 100 (the paper claims convergence is quick, of the order of 10s).  If you want to set manually, use the other align() method.
	}
	
	
	/**
	 * 
	 * Aligns a sub-population of objects using Logit Scaling alignment.
	 * 
	 * @param agents - list of agents to potentially apply alignment to (will be filtered by the 'filter' Predicate class); 
	 * 	the agent class must implement the Weight interface by providing a getWeight() method.  In the case of the alignment algorithm, getWeight() must return a positive value.
	 * @param filter - filters the agentList so that only the relevant sub-population of agents is sampled
	 * @param closure - AlignmentProbabilityClosure that specifies how to define the (unaligned) probability of the 
	 * 	agent and how to implement the result of the aligned probability. 
	 * @param targetShare - the target share of the relevant sub-population (specified as a proportion of the filtered 
	 * 	population) for which the mean of the aligned probabilities (defined by the AlignmentProbabilityClosure) must equal
	 * @param maxNumberIterations - The maximum number of iterations of the aligned probabilities before termination.  
	 * The probabilities derived will be used as aligned probabilities, although if the maximum number of iterations is reached,
	 *  it is possible that the mean of the probabilities has not converged to the alignment targetShare.
	 * @param precision - The allowed error between the mean of the alignment probabilities and the targetShare.  
	 * 	When the mean of the probabilities has converged to the targetShare within this precision, the algorithm will stop 
	 * 	iterating (assuming the number of iterations has not exceeded the maxNumberIterations parameter) and the resultant 
	 * 	probabilities should be considered the aligned probabilities.
	 * @param enableWarnings - If set to true, warnings will be sent to the System.out if the alignment has not converged within 
	 * 	the desired precision. If set to false, warnings will not be sent to the System.out.
	 * 
	 */
	public void align(Collection<T> agents, Predicate<T> filter, AlignmentProbabilityClosure<T> closure, double targetShare, int maxNumberIterations, double precision, boolean enableWarnings) {
		if (targetShare < 0. || targetShare > 1.) {
			throw new IllegalArgumentException("target probability in LogitScalingBinaryWeightedAlignment.align() method must lie in [0,1]");
		}
		if (maxNumberIterations < 1) {
			throw new IllegalArgumentException("maxNumberIterations in LogitScalingBinaryWeightedAlignment.align() method must be at least 1");
		} 
		if (precision <= 0.) {
			throw new IllegalArgumentException("precision in LogitScalingBinaryWeightedAlignment.align() method must be greater than 0");
		}

		List<T> list = new ArrayList<T>();		
		if (filter != null)
			CollectionUtils.select(agents, filter, list);
		else
			list.addAll(agents);
		
		int n = list.size();
		double total = 0.;			//The total weight, i.e. will sum the weight of each agent in the sub-population to be aligned. 
		
		double[] prob = new double[list.size()];					//Array of probabilities that will be adjusted by iteration
		double[] notProb = new double[list.size()];					//The complement probabilities (i.e. probabilities of the alternative choice), that will also be adjusted by iteration
		
		// compute total expected number of simulated positive outcomes
		for (int i=0; i<n; i++) {
			T agent = list.get(i);
			double weight = agent.getWeight();
			if(weight <= 0.) {
				throw new IllegalArgumentException("Weight cannot be zero or negative in ResamplingWeightedAlignment!");
			}
			total += weight;
			prob[i] =  closure.getProbability(agent) * weight;		//Unaligned probabilities (to be aligned by the procedure), weighted by the agents' weight.
			notProb[i] = weight - prob[i];							//Now, prob + notProb = weight, instead of 1 as prob counts for potentially more (or less) numbers of individuals, specified by the agent's getWeight method.
		}
		double target = targetShare * total;
		double allowedError = precision * total;		//precision refers to the share of the sub-population aligned, allowedError refers to the target, so is scaled up by the total weight of the sub-population.
		
		int count = 0;
		double error = Double.MAX_VALUE;
		double sumProb = 0., sumNotProb = 0.;
		double previousSumProb, previousSumNotProb;
		while( (error >= allowedError) && (count < maxNumberIterations) ) { 
			
			previousSumProb = sumProb;				//Save previous values to test convergence
			previousSumNotProb = sumNotProb;		//Save previous values to test convergence
			sumProb = 0.;							//Reset values to add up
			sumNotProb = 0.;						//Reset values to add up

			double sum = 0.;
			for(int i = 0; i < n; i++) {
				sum += prob[i];					//Note, this sum contains the weight of the agents already.
			}
			double gamma = target / sum;
			double notGamma = (total - target) / (total - (target / gamma));		//If you do the maths, this is 'gamma' for the complementary (not) scenario.
			
			for(int i = 0; i < n; i++) {
				T agent = list.get(i);
				
				//Gamma transform of the probabilities
				prob[i] *= gamma;
				notProb[i] *= notGamma;
				double alpha = agent.getWeight() / (prob[i] + notProb[i]);		//Because prob and notProb are scaled by agent.getWeight already, so prob + notProb = agent.getWeight() instead of 1.  Therefore, we need to use the agent's weight in the numerator here.

				//Alpha transform of the probabilities
				prob[i] *= alpha;
				notProb[i] *= alpha;
				
				//To calculate convergence, we need the sum of the probabilities
				sumProb += prob[i];
				sumNotProb += notProb[i];	
				
			}

			//To calculate convergence, we add the absolute difference between the sum of probabilities and the previous sum of probabilities for both prob and notProb states.
			error = Math.abs(sumProb - previousSumProb) + Math.abs(sumNotProb - previousSumNotProb);
			error /= 2.;		//2 options, so divide by two to be consistent with multiple choice case (and P. Stephensen's implementation of the termination condition)

//			System.out.println("Count, " + count + ", precision, " + error/(double)total + ", targetShare, " + targetShare + ", aligned prob[0], " + prob[0]/list.get(0).getWeight());
			count++;
		}
		
		if( (error >= allowedError) && enableWarnings) {			
			System.out.println("WARNING: The LogitScalingBinaryWeightedAlignment.align() method terminated with an error of " 
							+ (error/total) + ", which has a greater magnitude than the precision bounds of +/-" + precision + ".  The size of "
							+ " the filtered agent collection is " + n + " and the number of iterations was  " + count 
							+ ".  Check the results of the Logit Scaling Binary Weighted alignment to ensure that "
							+ "alignment is good enough for the purpose in question, or consider increasing the maximum number of iterations "
							+ "or the precision!");
			new Exception().printStackTrace();
		}

		// Correct individual probabilities with the aligned probabilities, prob[i]
		for (int i=0; i<n; i++) {
			T agent = list.get(i);
			closure.align(agent, prob[i]/agent.getWeight());		//As prob was previously scaled up by weight, need to scale down here to 'renormalise'.			
		}

	}	

}
