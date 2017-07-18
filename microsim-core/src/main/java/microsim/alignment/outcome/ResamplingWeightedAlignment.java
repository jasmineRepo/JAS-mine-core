package microsim.alignment.outcome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import microsim.agent.Weighting;
import microsim.engine.SimulationEngine;
import microsim.event.EventListener;
import microsim.statistics.regression.RegressionUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

/**
 * Align the population by weighted resampling.  This involves picking an agent from the relevant collection of agents at 
 * random with a probability that depends on an associated weight.  The chosen agent then undergoes resampling of it's relevant attribute (as specified by the AlignmentOutcomeClosure).  This process is continued until
 * either the alignment target is reached, or the maximum number of attempts to resample has been reached.
 * Implementation closely follows the JAS-mine ResamplingAlignment class, which is based on:
 * "Richiardi M., Poggi A. (2014). Imputing Individual Effects in Dynamic Microsimulation Models. An application to household formation and labor market participation in Italy. International Journal of Microsimulation, 7(2), pp. 3-39."
 * and "Leombruni R, Richiardi M (2006). LABORsim: An Agent-Based Microsimulation of Labour Supply. An application to Italy. Computational Economics, vol. 27, no. 1, pp. 63-88"

 * 
 * @author Ross Richardson
 */
public class ResamplingWeightedAlignment<T extends EventListener & Weighting> extends AbstractOutcomeAlignment<T> {


	//-----------------------------------------------------------------------------------
	//
	// Align share of population (to align absolute numbers, see alternative methods below)
	//
	//------------------------------------------------------------------------------------
	/**
	 * Align share of population by weighted resampling.  This involves picking an agent from the relevant collection of agents at 
	 * random with a probability that depends on an associated weight.  The chosen agent then undergoes resampling of it's relevant attribute (as specified by the AlignmentOutcomeClosure).  This process is continued until
	 * either the alignment target is reached, or the default maximum number of attempts to resample has been reached (which is 20 attempts per agent on average). 
	 * 
	 * @param agents - a list of agents to potentially be resampled; the agent class must implement the Weighting interface by providing a getWeighting() method.  In the case of the alignment algorithm, getWeighting() must return a positive value.
	 * @param filter - filters the agentList so that only the relevant sub-population of agents is sampled
	 * @param closure - AlignmentOutcomeClosure that specifies how to define the outcome of the agent and how to resample it 
	 * @param targetShare - the target share of the relevant sub-population (specified as a proportion of the filtered population) for which the outcome (defined by the AlignmentOutcomeClosure) must be true
	 */
	public void align(Collection<T> agents, Predicate<T> filter, AlignmentOutcomeClosure<T> closure, double targetShare) {
		align(agents, filter, closure, targetShare, -1);		//No maximum Resampling Attempts specified, so pass a negative number to be handled appropriately within the method.
	}
	
	/**
	 * Align share of population by weighted resampling.  This involves picking an agent from the relevant collection of agents at 
	 * random with a probability that depends on an associated weight.  The chosen agent then undergoes resampling of it's relevant attribute (as specified by the AlignmentOutcomeClosure).  This process is continued until
	 * either the alignment target is reached, or the maximum number of attempts to resample has been reached, as specified by the maxResamplingAttempts parameter. 
	 * 
	 * @param agents - a list of agents to potentially be resampled; the agent class must implement the Weighting interface by providing a getWeighting() method.  In the case of the alignment algorithm, getWeighting() must return a positive value.
	 * @param filter - filters the agentList so that only the relevant sub-population of agents is sampled
	 * @param closure - AlignmentOutcomeClosure that specifies how to define the outcome of the agent and how to resample it 
	 * @param targetShare - the target share of the relevant sub-population (specified as a proportion of the filtered population) for which the outcome (defined by the AlignmentOutcomeClosure) must be true
	 * @param maxResamplingAttempts - the maximum number of attempts to resample before terminating the alignment (this is in case the resampling (as defined by the AlignmentOutcomeClosure) is unable to alter
	 *  the outcomes of enough agents, due to the nature of the sub-population and the definition of the outcome (i.e. if agents' attributes are so far away from a binary outcome threshold boundary, that the
	 *   probability of enough of them switching to the desired outcome is vanishingly small).  
	 */
	public void align(Collection<T> agents, Predicate<T> filter, AlignmentOutcomeClosure<T> closure, double targetShare, int maxResamplingAttempts) {

		if(targetShare > 1.) {
			throw new IllegalArgumentException("ResamplingWeightedAlignment targetShare is larger than 100% of the population)!  This is impossible to reach.");
		}		
		else if(targetShare < 0.) {
			throw new IllegalArgumentException("ResamplingWeightedAlignment targetShare is negative!  This is impossible to reach.");
		}
		
		List<T> list = new ArrayList<T>();		
		if (filter != null)
			CollectionUtils.select(agents, filter, list);
		else
			list.addAll(agents);

		double total = 0.;
		
		// compute total number of simulated positive outcomes
		for (int i = 0; i < list.size(); i++) {
			T agent = list.get(i);
			double weighting = agent.getWeighting();
			total += weighting;
		}		
		
		double targetNumber = targetShare * total;
		
		doAlignment(list, closure, (int)targetNumber, maxResamplingAttempts);		
		
	}
	
	//-----------------------------------------------------------------------------------
	//
	// Align absolute numbers
	//
	//------------------------------------------------------------------------------------
	/**
	 * Align absolute number of population by weighted resampling.  This involves picking an agent from the relevant collection of agents at 
	 * random with a probability that depends on an associated weight.  The chosen agent then undergoes resampling of it's relevant attribute (as specified by the AlignmentOutcomeClosure).  This process is continued until
	 * either the alignment target is reached, or the default maximum number of attempts to resample has been reached (which is 20 attempts per agent on average). 
	 * 
	 * @param agents - a list of agents to potentially be resampled; the agent class must implement the Weighting interface by providing a getWeighting() method.  In the case of the alignment algorithm, getWeighting() must return a positive value.
	 * @param filter - filters the agentList so that only the relevant sub-population of agents is sampled
	 * @param closure - AlignmentOutcomeClosure that specifies how to define the outcome of the agent and how to resample it 
	 * @param targetNumber - the target number of the filtered population for which the outcome (defined by the AlignmentOutcomeClosure) must be true
	 */
	public void align(Collection<T> agents, Predicate<T> filter, AlignmentOutcomeClosure<T> closure, int targetNumber) {
		align(agents, filter, closure, targetNumber, -1);		//No maximum Resampling Attempts specified, so pass a negative number to be handled appropriately within the method.
	}
	
	/**
	 * Align absolute number of population by weighted resampling.  This involves picking an agent from the relevant collection of agents at 
	 * random with a probability that depends on an associated weight.  The chosen agent then undergoes resampling of it's relevant attribute (as specified by the AlignmentOutcomeClosure).  This process is continued until
	 * either the alignment target is reached, or the maximum number of attempts to resample has been reached, as specified by the maxResamplingAttempts parameter. 
	 * 
	 * @param agents - a list of agents to potentially be resampled; the agent class must implement the Weighting interface by providing a getWeighting() method.  In the case of the alignment algorithm, getWeighting() must return a positive value.
	 * @param filter - filters the agentList so that only the relevant sub-population of agents is sampled
	 * @param closure - AlignmentOutcomeClosure that specifies how to define the outcome of the agent and how to resample it 
	 * @param targetNumber - the target number of the filtered population for which the outcome (defined by the AlignmentOutcomeClosure) must be true
	 * @param maxResamplingAttempts - the maximum number of attempts to resample before terminating the alignment (this is in case the resampling (as defined by the AlignmentOutcomeClosure) is unable to alter
	 *  the outcomes of enough agents, due to the nature of the sub-population and the definition of the outcome (i.e. if agents' attributes are so far away from a binary outcome threshold boundary, that the
	 *   probability of enough of them switching to the desired outcome is vanishingly small).  
	 */
	public void align(Collection<T> agents, Predicate<T> filter, AlignmentOutcomeClosure<T> closure, int targetNumber, int maxResamplingAttempts) {
		
		if(targetNumber < 0) {
			throw new IllegalArgumentException("ResamplingWeightedAlignment targetNumber is negative!  This is impossible to reach.");
		}
		
		List<T> list = new ArrayList<T>();		
		if (filter != null)
			CollectionUtils.select(agents, filter, list);
		else
			list.addAll(agents);
		
		doAlignment(list, closure, targetNumber, maxResamplingAttempts);
		
	}
		
	//Note, the list argument here is already filtered for the relevant agents in the align(...) methods.
	public void doAlignment(List<T> list, AlignmentOutcomeClosure<T> closure, int targetNumber, int maxResamplingAttempts) {
		
//		System.out.println("Starting Resampling Alignment.  This may take some time, please wait...");
		
		Collections.shuffle(list, SimulationEngine.getRnd());

		int avgResampleAttemptsPerAgent = 20;
		double sum = 0.;
		double total = 0.;
		HashMap<T, Double> trueAgentMap = new HashMap<T, Double>();
		HashMap<T, Double> falseAgentMap = new HashMap<T, Double>();
		
		// compute total number of simulated positive outcomes
		for (int i=0; i< list.size(); i++) {
			T agent = list.get(i);
			double weighting = agent.getWeighting();
			if(weighting <= 0.) {
				throw new IllegalArgumentException("Weighting cannot be zero or negative in ResamplingWeightedAlignment!");
			}
			total += weighting;
			if(closure.getOutcome(agent)) {
				sum += weighting;
				trueAgentMap.put(agent, weighting);
			} 
			else {
				falseAgentMap.put(agent, weighting);
			}
		}
		
		if(targetNumber > total) {
			throw new IllegalArgumentException("ResamplingWeightedAlignment target is larger than the population size (over 100% of the population)!  This is impossible to reach.");
		}
				
		// compute difference between simulation and target
		double delta = sum - targetNumber;
		if(delta == 0.) {		//If already meeting alignment target, do not need to align.  We need this here, as for delta = 0 case, maxResamplingAttempts can still be assigned a value of -1 (by default when align method without maxResamplingAttempts argument is called).  Then count = 0 is > maxResamplingAttempts, and the warning at the bottom is called unnecessarily.
//			System.out.println("No need for alignment as delta is ," + delta);
			return;
		}
		
		int count = 0;
		T agentSmallestButTooLargeWeight = null;			//Store the agent with the smallest weight that is too big to be used 
		
		// if too many positive outcomes (delta is positive)
		if(delta > 0.) {
			if(maxResamplingAttempts < trueAgentMap.size()) {			//This will catch the case where maxResamplingAttempts is not included in the arguments.  Also it provides a lower bound for the user to specify, which is the size of the subset of the population whose outcomes need changing.  Anything less, and the number is automatically enlarged (in the line below).
				maxResamplingAttempts = avgResampleAttemptsPerAgent * trueAgentMap.size();	//This creates a default value of 20 times the size of the subset of the population to be resampled in order to move the delta towards 0.  Therefore, in order to improve delta, a member of the population undergoing alignment will be resampled up to a maximum of 20 times on average in order to change their outcome, before the alignment algorithm will give up and terminate.  
			}
			while ( (delta > 0.) && (count < maxResamplingAttempts) ) {
//				System.out.println("count, " + count + ", maxCount, " + maxResamplingAttempts + ", delta, " + delta + ", sum, " + (delta + targetNumber) + ", targetNumber, " + targetNumber);
				count++;
				T agent = RegressionUtils.event(trueAgentMap, SimulationEngine.getRnd(), false);		//This makes sample probability proportional to weighting (which are the values of the trueAgentMap)
				double weight = agent.getWeighting();
				if(delta >= weight) {					//Agent has small enough weighting to be allowed to make the change.
					closure.resample(agent);
					if (!closure.getOutcome(agent)) {
						delta -= weight;
						count = 0;
						trueAgentMap.remove(agent);
					}
				}
				else {
					if(agentSmallestButTooLargeWeight == null) {
						agentSmallestButTooLargeWeight = agent;
					}
					else if(agentSmallestButTooLargeWeight.getWeighting() > agent.getWeighting()) {
						agentSmallestButTooLargeWeight = agent;		//Replace with agent that has smaller weight (that is still just too big to be used)
					}
					trueAgentMap.remove(agent);		//Agent is too big to be resampled normally, only at the end if it brings delta closer to zero.  Therefore, we should not still include it in the map to be sampled as it cannot be resampled, so it just wastes time to potentially try it again and again!
				}
			} 
			if(agentSmallestButTooLargeWeight != null) {
				if(Math.abs( delta - agentSmallestButTooLargeWeight.getWeighting() ) < delta ) {		//Allow resampling of smallest agent that is too big if it would bring delta closer to zero
					int countLast = 0;
					while(countLast < avgResampleAttemptsPerAgent) {				//Allow several attempts to resample - on average the same as all the other randomly chosen agents above
						countLast++;
						closure.resample(agentSmallestButTooLargeWeight);
						if(!closure.getOutcome(agentSmallestButTooLargeWeight)) {
							delta -= agentSmallestButTooLargeWeight.getWeighting();
							break;
						}
					}
				}
			}
		}

		else if(delta < 0) {	// if too few positive outcomes (delta is negative)

			if(maxResamplingAttempts < falseAgentMap.size()) {			//This will catch the case where maxResamplingAttempts is not included in the arguments.  Also it provides a lower bound for the user to specify, which is the size of the subset of the population whose outcomes need changing.  Anything less, and the number is automatically enlarged (in the line below).
				maxResamplingAttempts = avgResampleAttemptsPerAgent * falseAgentMap.size();	//This creates a default value of 20 times the size of the subset of the population to be resampled in order to move the delta towards 0.  Therefore, in order to improve delta, a member of the population undergoing alignment will be resampled up to a maximum of 20 times on average in order to change their outcome, before the alignment algorithm will give up and terminate.  
			}
			while ( (delta < 0.) && (count < maxResamplingAttempts) ) {
//				System.out.println("count, " + count + ", maxCount, " + maxResamplingAttempts + ", delta, " + delta + ", sum, " + (delta + targetNumber) + ", targetNumber, " + targetNumber);
				count++;
				T agent = RegressionUtils.event(falseAgentMap, SimulationEngine.getRnd(), false);		//This makes sample probability proportional to weighting (which are the values of the falseAgentMap)
				double weight = agent.getWeighting();
				if(-delta >= weight) {					//Agent has small enough weighting to be allowed to make the change.
					closure.resample(agent);
					if (closure.getOutcome(agent)) {
						delta += weight;
						count = 0;
						falseAgentMap.remove(agent);
					}
				}
				else {
					if(agentSmallestButTooLargeWeight == null) {
						agentSmallestButTooLargeWeight = agent;
					}
					else if(agentSmallestButTooLargeWeight.getWeighting() > agent.getWeighting()) {
						agentSmallestButTooLargeWeight = agent;		//Replace with agent that has smaller weight (that is still just too big to be used)
					}
					falseAgentMap.remove(agent);		//Agent is too big to be resampled normally, only at the end if it brings delta closer to zero.  Therefore, we should not still include it in the map to be sampled as it cannot be resampled, so it just wastes time to potentially try it again and again!
				}
			} 
			if(agentSmallestButTooLargeWeight != null) {
				if(Math.abs( delta + agentSmallestButTooLargeWeight.getWeighting() ) < -delta ) {		//Allow resampling of smallest agent that is too big if it would bring delta closer to zero
					int countLast = 0;
					while(countLast < avgResampleAttemptsPerAgent) {				//Allow several attempts to resample - on average the same as all the other randomly chosen agents above
						countLast++;
						closure.resample(agentSmallestButTooLargeWeight);
						if(closure.getOutcome(agentSmallestButTooLargeWeight)) {
							delta += agentSmallestButTooLargeWeight.getWeighting();
							break;
						}
					}
				}
			}
		}
		
		if(count >= maxResamplingAttempts) { 
			System.out.println("Count, " + count + ", maxResamplingAttempts, " + maxResamplingAttempts + ", Weighted Resampling Alignment Algorithm has reached the maximum number of "
							+ "resample attempts (on average, " + avgResampleAttemptsPerAgent + " attempts "
							+ "per object to be aligned) and has terminated.  Alignment may have "
							+ "failed.  The difference between the population in the system with the "
							+ "desired outcome and the target number is " + delta + " (" 
							+ (delta*100./((double)targetNumber)) + " percent).  If this is too large, check "
							+ "the resampling method and the subset of population to understand why "
							+ "not enough of the population are able to change their outcomes.");
			System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
		}
//		System.out.println("final delta is ," + delta);
		
	}

}
