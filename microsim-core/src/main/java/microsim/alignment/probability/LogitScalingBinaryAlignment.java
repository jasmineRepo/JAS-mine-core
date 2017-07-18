package microsim.alignment.probability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import microsim.alignment.probability.AbstractProbabilityAlignment;
import microsim.alignment.probability.AlignmentProbabilityClosure;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;


/**
 * Logit Scaling alignment (as introduced by P. Stephensen in International Journal of Microsimulation (2016) 9(3) 89-102), 
 * but for agents with binary choices rather than the general case of 'A' choices.  For use with multiple choices, use the
 * LogitScalingAlignment class instead.  For use with weighted agents (where the weighting of an agent corresponds to 
 * the number of individuals it represents, use the LogitScalingBinaryWeightedAlignment (for binary choices) or
 * the LogitScalingWeightedAlignment class (for multiple choices) instead.
 * 
 * @author Ross Richardson
 *
 * @param <T>
 */
public class LogitScalingBinaryAlignment<T> extends AbstractProbabilityAlignment<T> {

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
	 * @param agents - collection of agents to potentially apply alignment to (will be filtered by the 'filter' Predicate class)
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
	 * @param agents - list of agents to potentially apply alignment to (will be filtered by the 'filter' Predicate class)
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
			throw new IllegalArgumentException("target probability in LogitScalingBinaryAlignment.align() method must lie in [0,1]");
		}
		if (maxNumberIterations < 1) {
			throw new IllegalArgumentException("maxNumberIterations in LogitScalingBinaryAlignment.align() method must be at least 1");
		} 
		if (precision <= 0.) {
			throw new IllegalArgumentException("precision in LogitScalingBinaryAlignment.align() method must be greater than 0");
		}

		List<T> list = new ArrayList<T>();		
		if (filter != null)
			CollectionUtils.select(agents, filter, list);
		else
			list.addAll(agents);
		
		int n = list.size();
		double target = targetShare * (double)n;
		double allowedError = precision * (double)n;		//precision refers to the share of the sub-population aligned, allowedError refers to the target number, so is scaled up by n.
		
		double[] prob = new double[list.size()];					//Array of probabilities that will be adjusted by iteration
		double[] notProb = new double[list.size()];					//The complement probabilities (i.e. probabilities of the alternative choice), that will also be adjusted by iteration
		
		// compute total expected number of simulated positive outcomes
		for (int i=0; i<n; i++) {
			T agent = list.get(i);
			prob[i] =  closure.getProbability(agent);		//Unaligned probabilities (to be aligned by the procedure).
			notProb[i] = 1. - prob[i];
		}

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
				sum += prob[i];
			}
			double gamma = target / sum;
			double notGamma = (n - target) / (n - (target / gamma));		//If you do the maths, this is 'gamma' for the complementary (not) scenario.
				
			for(int i = 0; i < n; i++) {

				//Gamma transform of the probabilities
				prob[i] *= gamma;
				notProb[i] *= notGamma;
				double alpha = 1. / (prob[i] + notProb[i]);

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

//			System.out.println("Count, " + count + ", precision, " + error/(double)n + ", targetShare, " + targetShare + ", aligned prob[0], " + prob[0]);
			count++;
		}
		
		if( (error >= allowedError) && enableWarnings) {
			System.out.println("WARNING: The LogitScalingBinaryAlignment.align() method terminated with an error of " 
					+ (error/(double)n) + ", which has a greater magnitude than the precision bounds of +/-" + precision + ".  The number "
					+ "of iterations was  " + count + ".  Check the results of the Logit Scaling Binary alignment to ensure that "
					+ "alignment is good enough for the purpose in question, or consider increasing the maximum number of iterations "
					+ "or the precision!");
		}

		// Correct individual probabilities with the aligned probabilities, prob[i]
		for (int i=0; i<n; i++) {
			T agent = list.get(i);
			closure.align(agent, prob[i]);			
		}

	}	

}
