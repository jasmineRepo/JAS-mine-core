package microsim.alignment.multiple;

import microsim.agent.Weight;

/**
 * This class is a weighted implementation of {@link microsim.alignment.multiple.AbstractLogitScalingAlignment}, for
 * full class documentation see the abstract class itself.
 * @see LogitScalingAlignment
 */
public class LogitScalingWeightedAlignment<T extends Weight> extends AbstractLogitScalingAlignment<T> {

    /**
     * @inheritDoc This is a weighted version where values may be different.
     */
    @Override final void extractWeights(double[] w) {
        for (int i = 0; i < getFilteredAgentList().size(); i++)
            w[i] = getFilteredAgentList().get(i).getWeight();
    }
}
