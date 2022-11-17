package microsim.alignment.outcome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import microsim.engine.SimulationEngine;
import microsim.event.EventListener;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

/**
 * Align the population by resampling.  This involves picking an agent from the relevant collection of agents at (uniform) 
 * random, and resampling it's relevant attribute (as specified by the AlignmentOutcomeClosure).  This process is continued until
 * either the alignment target is reached, or the maximum number of attempts to resample has been reached.
 * Implementation closely follows 
 * "Richiardi M., Poggi A. (2014). Imputing Individual Effects in Dynamic Microsimulation Models. An application to household formation and labor market participation in Italy. International Journal of Microsimulation, 7(2), pp. 3-39."
 * and "Leombruni R, Richiardi M (2006). LABORsim: An Agent-Based Microsimulation of Labour Supply. An application to Italy. Computational Economics, vol. 27, no. 1, pp. 63-88"

 * 
 * @author Ross Richardson
 */
public class ResamplingAlignment<T extends EventListener> extends AbstractOutcomeAlignment<T> {


	//-----------------------------------------------------------------------------------
	//
	// Align share of population (to align absolute numbers, see alternative methods below)
	//
	//------------------------------------------------------------------------------------
	/**
	 * Align share of population by resampling.  This involves picking an agent from the relevant collection of agents at (uniform) 
	 * random, and resampling it's relevant attribute (as specified by the AlignmentOutcomeClosure).  This process is continued until
	 * either the alignment target is reached, or the default maximum number of attempts to resample has been reached (which is 20 attempts per agent on average). 
	 * 
	 * @param agents - list of agents to potentially apply alignment to (will be filtered by the 'filter' Predicate class)
	 * @param filter - filters the agentList so that only the relevant sub-population of agents is sampled
	 * @param closure - AlignmentOutcomeClosure that specifies how to define the outcome of the agent and how to resample it 
	 * @param targetShare - the target share of the relevant sub-population (specified as a proportion of the filtered population) for which the outcome (defined by the AlignmentOutcomeClosure) must be true
	 */
	public void align(Collection<T> agents, Predicate<T> filter, AlignmentOutcomeClosure<T> closure, double targetShare) {
		align(agents, filter, closure, targetShare, -1);		//No maximum Resampling Attempts specified, so pass a negative number to be handled appropriately within the method.
	}
	
	/**
	 * Align share of population by resampling.  This involves picking an agent from the relevant collection of agents at (uniform) 
	 * random, and resampling it's relevant attribute (as specified by the AlignmentOutcomeClosure).  This process is continued until
	 * either the alignment target is reached, or the maximum number of attempts to resample has been reached, as specified by the maxResamplingAttempts parameter. 
	 * 
	 * @param agents - list of agents to potentially apply alignment to (will be filtered by the 'filter' Predicate class)
	 * @param filter - filters the agentList so that only the relevant sub-population of agents is sampled
	 * @param closure - AlignmentOutcomeClosure that specifies how to define the outcome of the agent and how to resample it 
	 * @param targetShare - the target share of the relevant sub-population (specified as a proportion of the filtered population) for which the outcome (defined by the AlignmentOutcomeClosure) must be true
	 * @param maxResamplingAttempts - the maximum number of attempts to resample before terminating the alignment (this is in case the resampling (as defined by the AlignmentOutcomeClosure) is unable to alter
	 *  the outcomes of enough agents, due to the nature of the sub-population and the definition of the outcome (i.e. if agents' attributes are so far away from a binary outcome threshold boundary, that the
	 *   probability of enough of them switching to the desired outcome is vanishingly small).  
	 */
	public void align(Collection<T> agents, Predicate<T> filter, AlignmentOutcomeClosure<T> closure, double targetShare, int maxResamplingAttempts) {
		
		if(targetShare > 1.) {
			throw new IllegalArgumentException("ResamplingAlignment targetShare is greater than 1 (meaning 100%)!  This is impossible.");
		} else if(targetShare < 0.) {
			throw new IllegalArgumentException("ResamplingAlignment targetShare is negative!  This is impossible.");
		}
		
		List<T> list = new ArrayList<T>();		
		if (filter != null)
			CollectionUtils.select(agents, filter, list);
		else
			list.addAll(agents);

		Collections.shuffle(list, SimulationEngine.getRnd());
		int n = list.size();
		double sum = 0;
		
		// compute total number of simulated positive outcomes
		for (int i=0; i<n; i++) {
			T agent = list.get(i);
			sum += (closure.getOutcome(agent) ? 1 : 0); 
		}
		double avgResampleAttemptPerCapita = 20.; 
		if(maxResamplingAttempts < sum) {			//This will catch the case where maxResamplingAttempts is not included in the arguments.  Also it provides a lower bound for the user to specify, which is the size of the subset of the population whose outcomes need changing.  Anything less, and the number is automatically enlarged (in the line below).
			maxResamplingAttempts = (int)(avgResampleAttemptPerCapita * sum);	//This creates a default value of 20 times the size of the subset of the population to be resampled in order to move the delta towards 0 by 1.  Therefore, in order to improve delta by 1, a member of the population undergoing alignment will be resampled up to a maximum of 20 times on average in order to change their outcome, before the alignment algorithm will give up and terminate.  
		}
		
//		if(sum == 0) {
//			System.out.println("Warning!  The filtered population of objects passed to the Resampling Alignment algorithm all have false outcomes initially, which means that the existing heterogeneity of the objects is not enough to provide variation in the outcomes of the alignment.  It may be the case that resampling them will not produce enough changes in outcomes to reach the alignment target.  Check the procedure for calculating the outcomes to see if there is any reason that only one of the outcomes occurs.");
//			System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
//		} else if(sum == n) {
//			System.out.println("Warning!  The filtered population of objects passed to the Resampling Alignment algorithm all have true outcomes initially, which means that the existing heterogeneity of the objects is not enough to provide variation in the outcomes of the alignment.  It may be the case that resampling them will not produce enough changes in outcomes to reach the alignment target.  Check the procedure for calculating the outcomes to see if there is any reason that only one of the outcomes occurs.");
//			System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
//		}
		
		// compute difference between simulation and target
		double delta = sum - targetShare * n;
//		System.out.println("start delta is ," + delta + " and size of list is " + list.size() + " sum is ," + sum);
		
		int count = 0;
		
		// if too many positive outcomes (delta is positive)
		if(delta > 0) {
			while ((Math.abs(delta) > 1.) && (count < maxResamplingAttempts)) {
				T agent = list.get(SimulationEngine.getRnd().nextInt(list.size()));
				//			System.out.println("count " + count);
				if (closure.getOutcome(agent)) {
					count++;
					closure.resample(agent);
					if (!closure.getOutcome(agent)) { 
						delta--;
//						System.out.println("delta is now," + delta + " count was " + count);
						count = 0;
					}
				}

			}
		}
		else if(delta < 0) {	// if too few positive outcomes (delta is negative)
			while ((Math.abs(delta) > 1.) && (count < maxResamplingAttempts)) {
				T agent = list.get(SimulationEngine.getRnd().nextInt(list.size()));
				if (!closure.getOutcome(agent)) {	
					count++;
					closure.resample(agent);
					if (closure.getOutcome(agent)) { 	
						delta++;
//						System.out.println("delta is now," + delta + " count was " + count);
						count = 0;
					}
				}
			}
		}
		
		if(count >= maxResamplingAttempts) { 
			System.out.println("Resampling Alignment Algorithm has reached the maximum number of resample attempts "
					+ "(on average, " + avgResampleAttemptPerCapita + " attempts per object to be aligned) and has "
							+ "terminated.  Alignment may have failed.  The difference between the population in "
							+ "the system with the desired outcome and the target number is " + delta + " (" 
							+ (delta*100./((double)n)) + " percent).  If this is too large, check the resampling "
							+ "method and the subset of population to understand why not enough of the "
							+ "population are able to change their outcomes.");
			System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
		}
//		System.out.println("final delta is ," + delta);
		
	}
	
	//-----------------------------------------------------------------------------------
	//
	// Align absolute numbers
	//
	//------------------------------------------------------------------------------------
	/**
	 * Align absolute number of population by resampling.  This involves picking an agent from the relevant collection of agents at (uniform) 
	 * random, and resampling it's relevant attribute (as specified by the AlignmentOutcomeClosure).  This process is continued until
	 * either the alignment target is reached, or the default maximum number of attempts to resample has been reached (which is 20 attempts per agent on average). 
	 * 
	 * @param agents - list of agents to potentially apply alignment to (will be filtered by the 'filter' Predicate class)
	 * @param filter - filters the agentList so that only the relevant sub-population of agents is sampled
	 * @param closure - AlignmentOutcomeClosure that specifies how to define the outcome of the agent and how to resample it 
	 * @param targetNumber - the target number of the filtered population for which the outcome (defined by the AlignmentOutcomeClosure) must be true
	 */
	public void align(Collection<T> agents, Predicate<T> filter, AlignmentOutcomeClosure<T> closure, int targetNumber) {
		align(agents, filter, closure, targetNumber, -1);		//No maximum Resampling Attempts specified, so pass a negative number to be handled appropriately within the method.
	}
	
	/**
	 * Align absolute number of population by resampling.  This involves picking an agent from the relevant collection of agents at (uniform) 
	 * random, and resampling it's relevant attribute (as specified by the AlignmentOutcomeClosure).  This process is continued until
	 * either the alignment target is reached, or the maximum number of attempts to resample has been reached, as specified by the maxResamplingAttempts parameter. 
	 * 
	 * @param agents - list of agents to potentially apply alignment to (will be filtered by the 'filter' Predicate class)
	 * @param filter - filters the agentList so that only the relevant sub-population of agents is sampled
	 * @param closure - AlignmentOutcomeClosure that specifies how to define the outcome of the agent and how to resample it 
	 * @param targetNumber - the target number of the filtered population for which the outcome (defined by the AlignmentOutcomeClosure) must be true
	 * @param maxResamplingAttempts - the maximum number of attempts to resample before terminating the alignment (this is in case the resampling (as defined by the AlignmentOutcomeClosure) is unable to alter
	 *  the outcomes of enough agents, due to the nature of the sub-population and the definition of the outcome (i.e. if agents' attributes are so far away from a binary outcome threshold boundary, that the
	 *   probability of enough of them switching to the desired outcome is vanishingly small).  
	 */
	public void align(Collection<T> agents, Predicate<T> filter, AlignmentOutcomeClosure<T> closure, int targetNumber, int maxResamplingAttempts) {
		
		if(targetNumber > agents.size()) {
			throw new IllegalArgumentException("ResamplingAlignment targetNumber is larger than the population size!  This is impossible to reach.");
		} else if(targetNumber < 0) {
			throw new IllegalArgumentException("ResamplingAlignment targetNumber is negative!  This is impossible to reach.");
		}

		List<T> list = new ArrayList<T>();
		if (filter != null)
			CollectionUtils.select(agents, filter, list);
		else
			list.addAll(agents);

		Collections.shuffle(list, SimulationEngine.getRnd());
		int n = list.size();
		int sum = 0;
		
		// compute total number of simulated positive outcomes
		for (int i=0; i<n; i++) {
			T agent = list.get(i);
			sum += (closure.getOutcome(agent) ? 1 : 0); 
		}
		int avgResampleAttemptPerCapita = 20; 
		if(maxResamplingAttempts < sum) {			//This will catch the case where maxResamplingAttempts is not included in the arguments.  Also it provides a lower bound for the user to specify, which is the size of the subset of the population whose outcomes need changing.  Anything less, and the number is automatically enlarged (in the line below).
			maxResamplingAttempts = avgResampleAttemptPerCapita * sum;	//This creates a default value of 20 times the size of the subset of the population to be resampled in order to move the delta towards 0 by 1.  Therefore, in order to improve delta by 1, a member of the population undergoing alignment will be resampled up to a maximum of 20 times on average in order to change their outcome, before the alignment algorithm will give up and terminate.  
		}
		
//		if(sum == 0) {
//			System.out.println("Warning!  The filtered population of objects passed to the Resampling Alignment algorithm all have false outcomes initially, which means that the existing heterogeneity of the objects is not enough to provide variation in the outcomes of the alignment.  It may be the case that resampling them will not produce enough changes in outcomes to reach the alignment target.  Check the procedure for calculating the outcomes to see if there is any reason that only one of the outcomes occurs.");
//			System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
//		} else if(sum == n) {
//			System.out.println("Warning!  The filtered population of objects passed to the Resampling Alignment algorithm all have true outcomes initially, which means that the existing heterogeneity of the objects is not enough to provide variation in the outcomes of the alignment.  It may be the case that resampling them will not produce enough changes in outcomes to reach the alignment target.  Check the procedure for calculating the outcomes to see if there is any reason that only one of the outcomes occurs.");
//			System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
//		}
		
		// compute difference between simulation and target
		double delta = sum - targetNumber;
//		System.out.println("start delta is ," + delta + " and size of list is " + list.size() + " sum is ," + sum);
		int count = 0;
		
		// if too many positive outcomes (delta is positive)
		if(delta > 0) {
			int resample = sum;
			int indx = 0;
			while ( (delta > 0) && (count < maxResamplingAttempts) && (resample > 0)) {
				T agent = list.get(indx);
				if (closure.getOutcome(agent)) {
					// candidate for change
					closure.resample(agent);
					if (!closure.getOutcome(agent)) {
						// change succeeded
						delta--;
						resample--;
						count = 0;
					} else {
						// change failed
						count++;
					}
				}
				if (indx < n-1)
					indx++;
				else
					indx = 0;
			}
		}
		else if(delta < 0) {	// if too few positive outcomes (delta is negative)
			int resample = n - sum;
			int indx = 0;
			while ( (delta < 0) && (count < maxResamplingAttempts) && (resample > 0)) {
				T agent = list.get(indx);
				if (!closure.getOutcome(agent)) {
					// candidate for change
					count++;
					closure.resample(agent);
					if (closure.getOutcome(agent)) {
						// change succeeded
						delta++;
						resample--;
						count = 0;
					} else {
						// change failed
						count++;
					}
				}
				if (indx < n-1)
					indx++;
				else
					indx = 0;
			}
		}
		if ( delta != 0) {

			if(count >= maxResamplingAttempts) {
				System.out.println("Resampling Alignment Algorithm has reached the maximum number of "
						+ "resample attempts (on average, " + avgResampleAttemptPerCapita + " attempts "
						+ "per object to be aligned) and has terminated.  Alignment may have "
						+ "failed.");
			} else {
				System.out.println("Resampling Alignment Algorithm has run out of sample to meet alignment "
						+ "target. Alignment may have failed.");
			}
			System.out.println("The difference between the population in the system with the "
					+ "desired outcome and the target number is " + delta + " ("
					+ (delta*100./((double)n)) + " percent).  If this is too large, check "
					+ "the resampling method and the subset of population to understand why "
					+ "not enough of the population are able to change their outcomes.");
			System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
		}
	}
}
